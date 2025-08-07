package com.nt.course_service_lms.service.serviceImpl;

import com.nt.course_service_lms.exception.FileStreamingException;
import com.nt.course_service_lms.service.S3StreamingService;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;

import static com.nt.course_service_lms.constants.S3Constants.DEFAULT_CHUNK_SIZE;

@Service
public class S3StreamingServiceImpl implements S3StreamingService {

    @Value("${aws.s3.bucket:}")
    private String BUCKET_NAME;


    @Autowired
    private S3Client s3Client;


    @Override
    public ResponseEntity<InputStreamResource> streamFileChunk(String objectKey, String rangeHeader) {
        try {
            HeadObjectResponse objectMetadata = s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(objectKey)
                            .build()
            );
            long contentLength = objectMetadata.contentLength();
            String contentType = objectMetadata.contentType();

            RangeInfo rangeInfo = parseRangeHeader(rangeHeader, contentLength);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(objectKey)
                    .range("bytes=" + rangeInfo.start + "-" + rangeInfo.end)
                    .build();

            InputStream inputStream = s3Client.getObject(getObjectRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Range", "bytes " + rangeInfo.start + "-" + rangeInfo.end + "/" + contentLength);
            headers.add("Accept-Ranges", "bytes");
            headers.add("Content-Length", String.valueOf(rangeInfo.end - rangeInfo.start + 1));
            headers.add("Cache-Control", "no-cache");

            if (contentType != null) {
                headers.setContentType(MediaType.parseMediaType(contentType));
            }

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));

        } catch (NoSuchKeyException e) {
            throw new FileStreamingException("File not found in S3: " + objectKey, e);
        } catch (S3Exception e) {
            throw new FileStreamingException("S3 error while streaming file: " + objectKey, e);
        } catch (IOException e) {
            throw new FileStreamingException("IO error while streaming file: " + objectKey, e);
        } catch (Exception e) {
            throw new FileStreamingException("Unexpected error while streaming file: " + objectKey, e);
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> streamFullFile(String objectKey) {
        try {
            // Get object metadata
            HeadObjectResponse objectMetadata = s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(objectKey)
                            .build()
            );

            // Get the object stream
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(objectKey)
                    .build();

            InputStream inputStream = s3Client.getObject(getObjectRequest);

            // Create response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Length", String.valueOf(objectMetadata.contentLength()));
            headers.add("Accept-Ranges", "bytes");

            if (objectMetadata.contentType() != null) {
                headers.setContentType(MediaType.parseMediaType(objectMetadata.contentType()));
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));

        } catch (NoSuchKeyException e) {
            throw new FileStreamingException("File not found in S3: " + objectKey, e);
        } catch (S3Exception e) {
            throw new FileStreamingException("S3 error while streaming file: " + objectKey, e);
        } catch (IOException e) {
            throw new FileStreamingException("IO error while streaming file: " + objectKey, e);
        } catch (Exception e) {
            throw new FileStreamingException("Unexpected error while streaming file: " + objectKey, e);
        }
    }

    @Override
    public long getFileSize(String objectKey) {
        try {
            HeadObjectResponse objectMetadata = s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(objectKey)
                            .build()
            );
            return objectMetadata.contentLength();
        } catch (Exception e) {
            throw new FileStreamingException("Failed to retrieve file size for: " + objectKey, e);
        }
    }

    private RangeInfo parseRangeHeader(String rangeHeader, long contentLength) {
        if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
            return new RangeInfo(0, Math.min(DEFAULT_CHUNK_SIZE - 1, contentLength - 1));
        }

        try {
            String range = rangeHeader.substring(6); // Remove "bytes="
            String[] parts = range.split("-");

            long start = Long.parseLong(parts[0]);
            long end;

            if (parts.length > 1 && !parts[1].isEmpty()) {
                end = Long.parseLong(parts[1]);
            } else {
                end = Math.min(start + DEFAULT_CHUNK_SIZE - 1, contentLength - 1);
            }

            return new RangeInfo(start, Math.min(end, contentLength - 1));
        } catch (Exception e) {
            return new RangeInfo(0, Math.min(DEFAULT_CHUNK_SIZE - 1, contentLength - 1));
        }
    }

    private static class RangeInfo {
        final long start;
        final long end;

        RangeInfo(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }
}

