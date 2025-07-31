package com.nt.user_service_lms.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Configuration class for setting up the database connection using credentials
 * securely stored in AWS Secrets Manager.
 * <p>
 * This configuration is only active for non-local environments.
 * </p>
 */
@Configuration
@Profile("!local & !test")
public class DatabaseConfig {

    /**
     * Spring Environment used to access configuration properties
     * such as AWS Secrets Manager secret name and region.
     */
    @Autowired
    private Environment environment;

    /**
     * Creates and configures a {@link DataSource} bean using database credentials
     * retrieved from AWS Secrets Manager.
     * <p>
     * The secret is expected to be a JSON object containing fields like:
     * <code>username</code>, <code>password</code>, <code>hostname</code>,
     * <code>db_name</code>, and <code>schema_name</code>.
     * </p>
     *
     * @return configured {@link DataSource} instance
     * @throws Exception if the secret cannot be retrieved or parsed
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
     * Retrieves and parses the secret from AWS Secrets Manager as a JSON object.
     * <p>
     * The secret ID and AWS region are read from the Spring environment properties:
     * <ul>
     *   <li><code>aws.secretsmanager.secretName</code></li>
     *   <li><code>aws.secretsmanager.region</code></li>
     * </ul>
     * </p>
     *
     * @return parsed secret as {@link JsonNode}
     * @throws RuntimeException if the secret cannot be retrieved or parsed
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
