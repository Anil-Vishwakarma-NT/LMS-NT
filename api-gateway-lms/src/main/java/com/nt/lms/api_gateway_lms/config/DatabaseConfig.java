package com.nt.lms.api_gateway_lms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import javax.sql.DataSource;

/**
 * Configuration class for setting up the database {@link DataSource} using credentials
 * stored securely in AWS Secrets Manager.
 * <p>
 * This configuration is excluded when the "local" Spring profile is active.
 * </p>
 */
@Configuration
@Profile("!local")
public class DatabaseConfig {

    /**
     * Spring {@link Environment} object used to read AWS configuration properties.
     */
    @Autowired
    private Environment environment;

    /**
     * Creates and configures a {@link DataSource} bean using secret credentials
     * retrieved from AWS Secrets Manager.
     * <p>
     * Expects the AWS secret to be a JSON with the following fields:
     * <ul>
     *     <li><code>username</code></li>
     *     <li><code>password</code></li>
     *     <li><code>hostname</code></li>
     *     <li><code>db_name</code></li>
     *     <li><code>schema_name</code></li>
     * </ul>
     * </p>
     *
     * @return configured {@link DataSource} object
     * @throws Exception if the secret is missing or cannot be parsed
     */
    @Bean
    public DataSource getDataSource() throws Exception {
        JsonNode secret = getSecretDetails();

        String username = secret.get("username").asText();
        String password = secret.get("password").asText();
        String hostname = secret.get("hostname").asText();
        String dbName = secret.get("db_name").asText();
        String schema = secret.get("schema_name").asText();

        String jdbcUrl = String.format("jdbc:postgresql://%s:5432/%s?currentSchema=%s", hostname, dbName, schema);

        return DataSourceBuilder.create()
                .username(username)
                .password(password)
                .url(jdbcUrl)
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    /**
     * Retrieves database credentials from AWS Secrets Manager and parses them as a JSON object.
     * <p>
     * The secret ID and region are fetched from Spring properties:
     * <ul>
     *     <li><code>aws.secretsmanager.secretName</code></li>
     *     <li><code>aws.secretsmanager.region</code></li>
     * </ul>
     * </p>
     *
     * @return {@link JsonNode} representing the parsed secret JSON
     * @throws RuntimeException if secret retrieval or parsing fails
     */
    private JsonNode getSecretDetails() {
        String secretName = environment.getProperty("aws.secretsmanager.secretName");
        String region = environment.getProperty("aws.secretsmanager.region");

        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.of(region))
                .build();

        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response = client.getSecretValue(request);
        String secretString = response.secretString();

        try {
            return new ObjectMapper().readTree(secretString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AWS secret JSON", e);
        }
    }
}
