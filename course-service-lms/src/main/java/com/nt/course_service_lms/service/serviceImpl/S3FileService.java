package com.nt.course_service_lms.service.serviceImpl;

import com.nt.course_service_lms.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nt.course_service_lms.constants.S3Constants.*;


@Service
@Slf4j
public class S3FileService {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    /**
     * MOST EFFICIENT METHOD - handles any file size optimally
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new ResourceNotFoundException("File not found");
        }

        String fileName = generateFileName(folder, file.getOriginalFilename());
        long fileSize = file.getSize();

        // Use size-based strategy for optimal performance
        if (fileSize <= 0) {
            // Unknown size - use adaptive streaming
            return uploadWithAdaptiveStreaming(file, fileName, folder);
        } else if (fileSize < MULTIPART_THRESHOLD) {
            // Small files - direct upload is most efficient
            return uploadSmallFile(file, fileName, folder);
        } else {
            // Large files - optimized multipart upload
            return uploadLargeFileOptimized(file, fileName, fileSize, folder);
        }
    }

    /**
     * Optimized small file upload (< 8MB)
     */
    private String uploadSmallFile(MultipartFile file, String fileName, String folder) throws IOException {
        try {
            String fileNameWithFolder = folder + "/" + fileName;
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileNameWithFolder)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            // Direct upload with input stream - most efficient for small files
            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return fileName;

        } catch (Exception e) {
            throw new IOException("Failed to upload small file: " + e.getMessage(), e);
        }
    }

    /**
     * Optimized large file upload with intelligent part sizing
     */
    private String uploadLargeFileOptimized(MultipartFile file, String fileName, long fileSize, String folder) throws IOException {
        // Calculate optimal part size based on file size
        long partSize = calculateOptimalPartSize(fileSize);

        String fileNameWithFolder = folder + "/" + fileName;

        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileNameWithFolder)
                .contentType(file.getContentType())
                .build();

        CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
        String uploadId = createResponse.uploadId();

        List<CompletedPart> completedParts = new ArrayList<>();

        try (BufferedInputStream bufferedInput = new BufferedInputStream(file.getInputStream(), BUFFER_SIZE)) {

            int partNumber = 1;
            long remainingBytes = fileSize;

            while (remainingBytes > 0) {
                // Calculate current part size
                long currentPartSize = Math.min(partSize, remainingBytes);

                // Read part data efficiently
                byte[] partData = readPartData(bufferedInput, currentPartSize);

                if (partData.length == 0) break;

                // Upload part
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(fileNameWithFolder)
                        .uploadId(uploadId)
                        .partNumber(partNumber)
                        .contentLength((long) partData.length)
                        .build();

                UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest,
                        RequestBody.fromBytes(partData));

                completedParts.add(CompletedPart.builder()
                        .partNumber(partNumber)
                        .eTag(uploadPartResponse.eTag())
                        .build());

                remainingBytes -= partData.length;
                partNumber++;

                // Progress logging (optional)
                double progress = ((double) (fileSize - remainingBytes) / fileSize) * 100;
                if (partNumber % 10 == 0) { // Log every 10th part to avoid spam
                    System.out.printf("Upload progress: %.1f%% (%d/%d parts)%n",
                            progress, partNumber - 1, (int) Math.ceil((double) fileSize / partSize));
                }
            }

            // Complete upload
            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(fileNameWithFolder)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())
                    .build();

            s3Client.completeMultipartUpload(completeRequest);

            System.out.printf("Upload completed: %s (%d parts, %d bytes)%n",
                    fileNameWithFolder, completedParts.size(), fileSize);

            return fileName;

        } catch (Exception e) {
            // Cleanup on failure
            abortMultipartUpload(bucketName, fileName, uploadId);
            throw new IOException("Failed to upload large file: " + e.getMessage(), e);
        }
    }

    /**
     * Adaptive streaming for unknown file sizes
     */
    private String uploadWithAdaptiveStreaming(MultipartFile file, String fileName, String folder) throws IOException {
        String fileNameWithFolder = folder + "/" + fileName;

        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileNameWithFolder)
                .contentType(file.getContentType())
                .build();

        CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
        String uploadId = createResponse.uploadId();

        List<CompletedPart> completedParts = new ArrayList<>();

        try (BufferedInputStream bufferedInput = new BufferedInputStream(file.getInputStream(), BUFFER_SIZE)) {

            int partNumber = 1;
            long totalBytesRead = 0;

            while (true) {
                // Use optimal part size for unknown file sizes
                byte[] partData = readPartData(bufferedInput, OPTIMAL_PART_SIZE);

                if (partData.length == 0) break;

                // Ensure minimum part size (except for last part)
                if (partData.length < MIN_PART_SIZE) {
                    // Try to read more data to meet minimum
                    byte[] additionalData = readPartData(bufferedInput, MIN_PART_SIZE - partData.length);
                    if (additionalData.length > 0) {
                        byte[] combinedData = new byte[partData.length + additionalData.length];
                        System.arraycopy(partData, 0, combinedData, 0, partData.length);
                        System.arraycopy(additionalData, 0, combinedData, partData.length, additionalData.length);
                        partData = combinedData;
                    }
                }

                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(fileNameWithFolder)
                        .uploadId(uploadId)
                        .partNumber(partNumber)
                        .contentLength((long) partData.length)
                        .build();

                UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest,
                        RequestBody.fromBytes(partData));

                completedParts.add(CompletedPart.builder()
                        .partNumber(partNumber)
                        .eTag(uploadPartResponse.eTag())
                        .build());

                totalBytesRead += partData.length;
                partNumber++;

                if (partNumber % 20 == 0) {
                    System.out.printf("Uploaded %d parts, %d bytes%n", partNumber - 1, totalBytesRead);
                }
            }

            // Complete upload
            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(fileNameWithFolder)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())
                    .build();

            s3Client.completeMultipartUpload(completeRequest);

            System.out.printf("Adaptive upload completed: %s (%d parts, %d bytes)%n",
                    fileNameWithFolder, completedParts.size(), totalBytesRead);

            return fileName;

        } catch (Exception e) {
            abortMultipartUpload(bucketName, fileName, uploadId);
            throw new IOException("Failed adaptive upload: " + e.getMessage(), e);
        }
    }

    /**
     * Efficient part data reading with proper buffer management
     */
    private byte[] readPartData(InputStream inputStream, long maxSize) throws IOException {
        ByteArrayOutputStream partData = new ByteArrayOutputStream();
        byte[] buffer = new byte[STREAMING_CHUNK_SIZE];
        long bytesRead = 0;

        while (bytesRead < maxSize) {
            int readSize = inputStream.read(buffer, 0,
                    (int) Math.min(buffer.length, maxSize - bytesRead));

            if (readSize == -1) break; // End of stream

            partData.write(buffer, 0, readSize);
            bytesRead += readSize;
        }

        return partData.toByteArray();
    }

    /**
     * Calculate optimal part size based on file size and network conditions
     */
    private long calculateOptimalPartSize(long fileSize) {
        if (fileSize <= 100 * 1024 * 1024L) { // < 100MB
            return 8 * 1024 * 1024L; // 8MB parts
        } else if (fileSize <= 1024 * 1024 * 1024L) { // < 1GB
            return 16 * 1024 * 1024L; // 16MB parts
        } else if (fileSize <= 10L * 1024 * 1024 * 1024L) { // < 10GB
            return 32 * 1024 * 1024L; // 32MB parts
        } else {
            // For very large files, calculate to stay under 10,000 parts limit
            long partSize = Math.max(fileSize / 9000L, MIN_PART_SIZE);
            return Math.min(partSize, MAX_PART_SIZE);
        }
    }



    // Utility methods
    private String generateFileName(String folder, String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    private void abortMultipartUpload(String bucket, String key, String uploadId) {
        try {
            AbortMultipartUploadRequest abortRequest = AbortMultipartUploadRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .uploadId(uploadId)
                    .build();
            s3Client.abortMultipartUpload(abortRequest);
        } catch (Exception e) {
            System.err.println("Failed to abort multipart upload: " + e.getMessage());
        }
    }


    /**
     * Check if file exists in S3 bucket
     * @param fileName the key/name of the file to check
     * @return true if file exists, false otherwise
     */
    public boolean fileExists(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            log.warn("File name is null or empty");
            return false;
        }

        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName.trim())
                    .build();
            s3Client.headObject(headRequest);
            log.debug("File exists in S3: {}", fileName);
            return true;
        } catch (NoSuchKeyException e) {
            log.debug("File does not exist in S3: {}", fileName);
            return false;
        } catch (S3Exception e) {
            log.error("S3 error while checking file existence: {} - Error: {}", fileName, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while checking file existence: {} - Error: {}", fileName, e.getMessage());
            return false;
        }
    }

    /**
     * Delete file from S3 bucket with comprehensive error handling
     * @param fileName the key/name of the file to delete
     * @return true if file was deleted successfully, false if file didn't exist
     * @throws RuntimeException if deletion fails due to S3 errors
     */
    public boolean deleteFile(String fileName) {
        // Input validation
        if (fileName == null || fileName.trim().isEmpty()) {
            log.warn("Cannot delete file: filename is null or empty");
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        String trimmedFileName = fileName.trim();

        // Check if file exists before attempting deletion
        if (!fileExists(trimmedFileName)) {
            log.info("File does not exist in S3, skipping deletion: {}", trimmedFileName);
            return false;
        }

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(trimmedFileName)
                    .build();

            DeleteObjectResponse response = s3Client.deleteObject(deleteRequest);

            // S3 deleteObject always returns success even if file doesn't exist
            // So we rely on our pre-check for accurate reporting
            log.info("Successfully deleted file from S3: {} (RequestId: {})",
                    trimmedFileName, response.responseMetadata().requestId());
            return true;

        } catch (S3Exception e) {
            String errorMessage = String.format("S3 error while deleting file: %s - ErrorCode: %s, ErrorMessage: %s",
                    trimmedFileName, e.awsErrorDetails().errorCode(), e.getMessage());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format("Unexpected error while deleting file: %s - Error: %s",
                    trimmedFileName, e.getMessage());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
          * Get file metadata
          */
    public HeadObjectResponse getFileMetadata(String fileName) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            return s3Client.headObject(headRequest);
        } catch (Exception e) {
            throw new RuntimeException("File not found: " + fileName, e);
        }
    }

        /**
     * List files in a folder with pagination
     */
    public List<String> listFiles(String folderPrefix, int maxKeys) {
        List<String> fileNames = new ArrayList<>();

        ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(folderPrefix)
                .maxKeys(maxKeys);

        ListObjectsV2Request request = requestBuilder.build();
        ListObjectsV2Response result;

        do {
            result = s3Client.listObjectsV2(request);
            for (S3Object s3Object : result.contents()) {
                fileNames.add(s3Object.key());
            }

            if (result.isTruncated() && fileNames.size() < maxKeys) {
                request = request.toBuilder()
                        .continuationToken(result.nextContinuationToken())
                        .build();
            }
        } while (result.isTruncated() && fileNames.size() < maxKeys);

        return fileNames;
    }

    @FunctionalInterface
    public interface UploadProgressCallback {
        void onProgress(int percentage, long uploadedBytes, long totalBytes);
    }
}