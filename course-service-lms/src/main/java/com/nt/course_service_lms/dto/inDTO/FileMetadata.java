package com.nt.course_service_lms.dto.inDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileMetadata {
    @JsonProperty("objectKey")
    private String objectKey;

    @JsonProperty("size")
    private long size;

    @JsonProperty("chunkSize")
    private long chunkSize = 1024 * 1024 * 5; // 1MB default

}
