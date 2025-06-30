package com.nt.lms.api_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EncryptResponse {
    private String encryptedPassword;
}