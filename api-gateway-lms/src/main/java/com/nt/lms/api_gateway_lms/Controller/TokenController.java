package com.nt.lms.api_gateway_lms.Controller;

import com.nt.lms.api_gateway_lms.config.JwtUtil;
import com.nt.lms.api_gateway_lms.dto.ServiceTokenRequest;
import com.nt.lms.api_gateway_lms.dto.ServiceTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.*;

@RestController
@RequestMapping("/lms/api/token-api")
@Slf4j
public class TokenController {

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/service-token")
    public Mono<ResponseEntity<ServiceTokenResponse>> getServiceToken(ServerHttpRequest request, @RequestBody ServiceTokenRequest serviceTokenRequest) {

        // 1. Extract Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return Mono.just(ResponseEntity.status(401)
                    .body(new ServiceTokenResponse("Missing or invalid Authorization header")));
        }

        String accessToken = authHeader.substring(BEARER_PREFIX.length());

        try {
            // 2. Generate service token
            String serviceToken = jwtUtil.generateServiceTokenForDirectAccess(
                    accessToken, serviceTokenRequest.getTargetService());
            long expiry = jwtUtil.extractExpiration(accessToken).getTime();

            // 3. Return response DTO
            return Mono.just(ResponseEntity.ok(new ServiceTokenResponse(serviceToken)));
        } catch (Exception e) {
            log.error("Token processing error: {}", e.getMessage());
            return Mono.just(ResponseEntity.status(500)
                    .body(new ServiceTokenResponse("Token processing error")));
        }
    }
}

