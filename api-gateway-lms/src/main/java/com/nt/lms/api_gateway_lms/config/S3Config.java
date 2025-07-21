package com.nt.lms.api_gateway_lms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Configuration class for setting up an AWS S3 client.
 * <p>
 * This configuration is only active for non-local profiles.
 * It supports both static credentials (for dev/test) and default
 * credential provider chain (recommended for production).
 * </p>
 */
@Configuration
@Profile("!local")
public class S3Config {

    /**
     * The AWS region where the S3 service is located.
     * Injected from the property <code>aws.region</code>.
     */
    @Value("${aws.region}")
    private String region;

    /**
     * AWS access key used for static credentials (optional).
     * Injected from the property <code>aws.access-key</code>.
     * If not provided, default credential provider is used.
     */
    @Value("${aws.access-key:}")
    private String accessKey;

    /**
     * AWS secret key used for static credentials (optional).
     * Injected from the property <code>aws.secret-key</code>.
     * If not provided, default credential provider is used.
     */
    @Value("${aws.secret-key:}")
    private String secretKey;

    /**
     * Creates and returns a configured {@link S3Client} instance.
     * <p>
     * If both access key and secret key are provided, it uses
     * {@link StaticCredentialsProvider}. Otherwise, it falls back to
     * {@link DefaultCredentialsProvider} which supports environment variables,
     * IAM roles, and ~/.aws/credentials.
     * </p>
     *
     * @return a configured {@link S3Client} bean
     */
    @Bean
    public S3Client s3Client() {
        if (accessKey != null && !accessKey.isBlank() && secretKey != null && !secretKey.isBlank()) {
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
                    )
                    .build();
        }

        // Recommended for production (uses IAM role, environment variables, or ~/.aws/credentials)
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
