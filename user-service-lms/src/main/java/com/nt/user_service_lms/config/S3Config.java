package com.nt.user_service_lms.config;

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
 * Configuration class for setting up the AWS S3 client.
 * <p>
 * This configuration is excluded when the active profile is "local".
 * It supports both static credentials (for development or CI environments)
 * and default credential provider chain (recommended for production).
 * </p>
 */
@Configuration
@Profile("!local")
public class S3Config {

    /**
     * The AWS region where the S3 bucket is hosted.
     */
    @Value("${aws.region}")
    private String region;

    /**
     * The AWS access key used for static credential authentication (optional).
     * If blank or not provided, the default credentials provider will be used.
     */
    @Value("${aws.access-key:}")
    private String accessKey;

    /**
     * The AWS secret key used for static credential authentication (optional).
     * If blank or not provided, the default credentials provider will be used.
     */
    @Value("${aws.secret-key:}")
    private String secretKey;

    /**
     * Creates and configures an {@link S3Client} bean.
     * <p>
     * If access key and secret key are provided, a static credentials provider is used.
     * Otherwise, the default credentials provider chain is used, which looks for IAM roles,
     * environment variables, or credentials files.
     * </p>
     *
     * @return a configured {@link S3Client} instance
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
