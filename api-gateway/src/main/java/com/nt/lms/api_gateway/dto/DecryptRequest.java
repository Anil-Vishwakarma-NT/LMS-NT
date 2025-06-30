package com.nt.lms.api_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DecryptRequest {
    private String encryptedPassword;
    private String plainPassword;
}





