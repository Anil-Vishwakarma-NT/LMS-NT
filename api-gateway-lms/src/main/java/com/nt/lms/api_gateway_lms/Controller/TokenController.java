package com.nt.lms.api_gateway_lms.Controller;

import com.nt.lms.api_gateway_lms.config.JwtUtil;
import com.nt.lms.api_gateway_lms.constant.CommonConstants;
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

import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.BEARER_PREFIX;

/**
 * REST controller responsible for handling token-related operations in the LMS API Gateway.
 * This controller provides endpoints for generating service tokens based on user access tokens.
 *
 * <p>The controller validates incoming authorization headers and generates service-specific tokens
 * that can be used for inter-service communication within the LMS ecosystem.</p>
 *
 * @author LMS Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/lms/api/token-api")
@Slf4j
public class TokenController {

    /**
     * JWT utility component for handling token operations including generation,
     * validation, and extraction of token information.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Generates a service token for direct access to specified target services.
     *
     * <p>This endpoint validates the provided authorization header, extracts the access token,
     * and generates a new service-specific token that can be used for inter-service communication.
     * The generated token inherits the expiration time from the original access token.</p>
     *
     * <p><strong>Request Requirements:</strong></p>
     * <ul>
     *   <li>Valid Authorization header with Bearer token</li>
     *   <li>Request body containing target service information</li>
     * </ul>
     *
     * <p><strong>Response Scenarios:</strong></p>
     * <ul>
     *   <li>200 OK: Service token generated successfully</li>
     *   <li>401 Unauthorized: Missing or invalid Authorization header</li>
     *   <li>500 Internal Server Error: Token processing error</li>
     * </ul>
     *
     * @param request the incoming HTTP request containing headers and other request information
     * @param serviceTokenRequest the request body containing target service details for token generation
     * @return a Mono containing ResponseEntity with ServiceTokenResponse - either success with token or error message
     *
     * @throws RuntimeException if token generation fails due to invalid access token or service configuration
     *
     * @see ServiceTokenRequest
     * @see ServiceTokenResponse
     * @see JwtUtil#generateServiceTokenForDirectAccess(String, String)
     */
    @PostMapping("/service-token")
    public Mono<ResponseEntity<ServiceTokenResponse>> getServiceToken(
            final ServerHttpRequest request,
            final @RequestBody ServiceTokenRequest serviceTokenRequest) {

        // 1. Extract Authorization header
        final String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return Mono.just(ResponseEntity.status(CommonConstants.INTEGER_FOUR_HUNDRED_ONE)
                    .body(new ServiceTokenResponse("Missing or invalid Authorization header")));
        }

        final String accessToken = authHeader.substring(BEARER_PREFIX.length());

        try {
            // 2. Generate service token
            final String serviceToken = jwtUtil.generateServiceTokenForDirectAccess(
                    accessToken, serviceTokenRequest.getTargetService());

            final long expiry = jwtUtil.extractExpiration(accessToken).getTime();

            // 3. Return response DTO
            return Mono.just(ResponseEntity.ok(new ServiceTokenResponse(serviceToken)));
        } catch (final Exception e) {
            log.error("Token processing error: {}", e.getMessage());
            return Mono.just(ResponseEntity.status(CommonConstants.INTEGER_FIVE_HUNDRED)
                    .body(new ServiceTokenResponse("Token processing error")));
        }
    }
}
