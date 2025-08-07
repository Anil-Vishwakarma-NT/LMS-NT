package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.FileMetadata;
import com.nt.course_service_lms.exception.FileStreamingException;
import com.nt.course_service_lms.service.S3StreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/service-api/streaming")
public class StreamingController {

    @Autowired
    private S3StreamingService s3StreamingService;

    /**
     * Stream video/audio content with range support for ReactPlayer
     * Supports HTTP Range requests for efficient streaming
     */
    @GetMapping("/video/{fileName:.+}")
    public ResponseEntity<?> streamVideo(
            @PathVariable String fileName,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        try {
            String objectKey = "video/" + fileName;
            return s3StreamingService.streamFileChunk(objectKey, rangeHeader);
        } catch (FileStreamingException e) {
            // log the error if needed
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Unexpected streaming error"));
        }
    }

    /**
     * Stream PDF content - handles both chunked and full requests
     * If no Range header is provided, returns the full file
     * If Range header is provided, returns the requested chunk
     */
    @GetMapping("/pdf/{filename:.+}")
    public ResponseEntity<?> streamPdf(
            @PathVariable String filename,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {

        try {
            String objectKey = "pdf/" + filename;
            // If no range header is provided, return the full file
//            if (rangeHeader == null || rangeHeader.trim().isEmpty()) {
//                System.out.println("No range header - returning full file");
//                return s3StreamingService.streamFullFile(objectKey);
//            }
                return s3StreamingService.streamFileChunk(objectKey, rangeHeader);

        } catch (FileStreamingException e) {
            // log the error if needed
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Unexpected streaming error"));
        }
    }


    /**
     * Get file metadata (size, type, etc.)
     * Updated to handle PDF files correctly
     */
    @GetMapping("/metadata/{filename:.+}")
    public ResponseEntity<FileMetadata> getFileMetadata(@PathVariable String filename) {
        try {
            // Determine file type based on extension and try appropriate folder first
            String objectKey;
            long fileSize = -1;

            if (filename.toLowerCase().endsWith(".pdf")) {
                // For PDF files, look in pdf/ folder
                objectKey = "pdf/" + filename;
                fileSize = s3StreamingService.getFileSize(objectKey);
                System.out.println("Checking PDF: " + objectKey + ", Size: " + fileSize);
            } else {
                // For other files (video/audio), look in video/ folder
                objectKey = "video/" + filename;
                fileSize = s3StreamingService.getFileSize(objectKey);
                System.out.println("Checking Video: " + objectKey + ", Size: " + fileSize);
            }

            // If not found in expected folder, try the other folder
            if (fileSize == -1) {
                String alternateObjectKey;
                if (filename.toLowerCase().endsWith(".pdf")) {
                    alternateObjectKey = "video/" + filename;
                } else {
                    alternateObjectKey = "pdf/" + filename;
                }
                fileSize = s3StreamingService.getFileSize(alternateObjectKey);
                System.out.println("Checking alternate location: " + alternateObjectKey + ", Size: " + fileSize);

                if (fileSize != -1) {
                    objectKey = alternateObjectKey; // Update objectKey if found in alternate location
                }
            }

            if (fileSize == -1) {
                System.out.println("File not found: " + filename);
                return ResponseEntity.notFound().build();
            }

            FileMetadata metadata = new FileMetadata();
            metadata.setSize(fileSize);
            metadata.setObjectKey(filename);

            System.out.println("File metadata success - Name: " + filename + ", Size: " + fileSize + " bytes");
            return ResponseEntity.ok(metadata);

        } catch (Exception e) {
            System.err.println("Metadata fetch failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Stream any file type with full content (no chunking)
     * Use this for smaller files or when full download is needed
     */
    @GetMapping("/download/{objectKey:.+}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String objectKey) {
        return s3StreamingService.streamFullFile(objectKey);
    }
}
