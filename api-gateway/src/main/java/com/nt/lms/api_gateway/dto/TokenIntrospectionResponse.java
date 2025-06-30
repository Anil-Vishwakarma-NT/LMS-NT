package com.nt.lms.api_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenIntrospectionResponse {
    private Boolean active;
    private String subject;
    private String audience;
    private String issuer;
    private String tokenType;
    private String scope;
    private List<String> roles;
    private String email;
    private Long issuedAt;
    private Long expiration;
}