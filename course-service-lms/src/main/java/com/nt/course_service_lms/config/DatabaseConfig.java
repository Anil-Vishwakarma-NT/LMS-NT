package com.nt.course_service_lms.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Autowired
    private Environment environment;

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