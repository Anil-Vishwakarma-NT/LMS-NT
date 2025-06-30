package com.nt.LMS.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.nt.LMS.constants.TokenConverterConstant.*;

@Component
public class FeignTokenInterceptor implements RequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(FeignTokenInterceptor.class);

    @Autowired
    private ServiceTokenConverter serviceTokenConverter;

    @Value("${gateway.secret.header:X-Gateway-Secret}")
    private String gatewaySecretHeader;

    @Value("${gateway.secret.value:your-super-secret-gateway-key}")
    private String gatewaySecretValue;

    @Value("${gateway.signature.header:X-Gateway-Signature}")
    private String gatewaySignatureHeader;

    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof ServicePrincipal) {
            ServicePrincipal principal = (ServicePrincipal) authentication.getPrincipal();

            try {
                String targetService = "product-service";

                if (targetService != null) {
                    String currentToken = getCurrentServiceToken();

                    if (currentToken != null) {
                        if (!serviceTokenConverter.canConvertToken(currentToken)) {
                            logger.warn("Cannot convert current token for target service: {}", targetService);
                            return;
                        }

                        if (serviceTokenConverter.needsTokenConversion(currentToken, targetService)) {
                            String convertedToken = serviceTokenConverter.convertServiceToken(currentToken, targetService);
                            template.header(HEADER_X_SERVICE_TOKEN, convertedToken);
                        } else {
                            template.header(HEADER_X_SERVICE_TOKEN, currentToken);
                        }

                        String originalTokenType = ServiceTokenContext.getOriginalTokenType();
                        if (originalTokenType != null) {
                            template.header(HEADER_X_ORIGINAL_TOKEN_TYPE, originalTokenType);
                        }

                        addUserContextHeaders(template, principal);
                    }
                }

                addGatewayHeaders(template);

            } catch (Exception e) {
                logger.error("Failed to add service token to Feign request for URL {}: {}", template.url(), e.getMessage(), e);
            }
        }
    }

    private String getCurrentServiceToken() {
        return ServiceTokenContext.getCurrentToken();
    }

    private void addUserContextHeaders(RequestTemplate template, ServicePrincipal principal) {
        try {
            if (principal.getUserId() != null) {
                template.header(HEADER_X_USER_ID, principal.getUserId());
            }
            if (principal.getUserEmail() != null) {
                template.header(HEADER_X_USER_EMAIL, principal.getUserEmail());
            }
            if (principal.getServiceId() != null) {
                template.header(HEADER_X_SOURCE_SERVICE, principal.getServiceId());
            }
            template.header(HEADER_X_REQUEST_SOURCE, DEFAULT_SOURCE_SERVICE);
            template.header(HEADER_X_REQUEST_TIMESTAMP, String.valueOf(System.currentTimeMillis()));

        } catch (Exception e) {
            logger.debug("Could not add user context headers: {}", e.getMessage());
        }
    }

    private void addGatewayHeaders(RequestTemplate template) {
        try {
            template.header(gatewaySecretHeader, gatewaySecretValue);

            long timestamp = System.currentTimeMillis();
            String nonce = generateNonce();

            template.header(HEADER_X_GATEWAY_TIMESTAMP, String.valueOf(timestamp));
            template.header(HEADER_X_GATEWAY_NONCE, nonce);

            String signature = generateSignature(String.valueOf(timestamp), nonce, gatewaySecretValue);
            template.header(gatewaySignatureHeader, signature);

        } catch (Exception e) {
            logger.warn("Failed to add gateway headers: {}", e.getMessage());
        }
    }

    private String generateNonce() {
        return String.valueOf(System.nanoTime());
    }

    private String generateSignature(String timestamp, String nonce, String secret) {
        String data = timestamp + ":" + nonce + ":" + secret;
        return Integer.toString(data.hashCode());
    }
}
