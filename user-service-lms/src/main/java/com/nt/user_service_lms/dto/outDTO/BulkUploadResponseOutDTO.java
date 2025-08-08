package com.nt.user_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkUploadResponseOutDTO {
    private int totalRecords;
    private int successfulUploads;
    private int failedUploads;
    private List<String> errors;
    private String message;
}
