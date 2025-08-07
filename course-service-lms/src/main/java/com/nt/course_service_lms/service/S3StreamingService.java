package com.nt.course_service_lms.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3StreamingService {
    ResponseEntity<InputStreamResource> streamFileChunk(String objectKey, String rangeHeader);
    ResponseEntity<InputStreamResource> streamFullFile(String objectKey);
    long getFileSize(String objectKey);
}
