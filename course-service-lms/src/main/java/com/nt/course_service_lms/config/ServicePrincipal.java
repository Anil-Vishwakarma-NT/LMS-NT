package com.nt.course_service_lms.config;

import java.security.Principal;

/**
 * Custom principal implementation for service authentication that encapsulates
 * both service and user information for secure service-to-service communication.
 */
public final class ServicePrincipal implements Principal {

    /**
     * The unique identifier for the service.
     */
    private final String serviceId;

    /**
     * The unique identifier for the user.
     */
    private final String userId;

    /**
     * The email address of the user.
     */
    private final String userEmail;

    /**
     * The full name of the user.
     */
    private final String userFullName;

    /**
     * The original token type used for authentication.
     */
    private final String originalTokenType;

    /**
     * Constructs a new ServicePrincipal using the builder pattern.
     *
     * @param builder the builder containing the principal information
     */
    private ServicePrincipal(final Builder builder) {
        this.serviceId = builder.builderServiceId;
        this.userId = builder.builderUserId;
        this.userEmail = builder.builderUserEmail;
        this.userFullName = builder.builderUserFullName;
        this.originalTokenType = builder.builderOriginalTokenType;
    }

    /**
     * Returns the name of this principal, which is the service ID if available,
     * otherwise returns "unknown-service".
     *
     * @return the name of this principal
     */
    @Override
    public String getName() {
        return serviceId != null ? serviceId : "unknown-service";
    }

    /**
     * Gets the service identifier.
     *
     * @return the service ID
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Gets the user identifier.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the user's email address.
     *
     * @return the user email
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Gets the user's full name.
     *
     * @return the user's full name
     */
    public String getUserFullName() {
        return userFullName;
    }

    /**
     * Gets the original token type used for authentication.
     *
     * @return the original token type
     */
    public String getOriginalTokenType() {
        return originalTokenType;
    }

    /**
     * Builder class for constructing ServicePrincipal instances using the builder pattern.
     */
    public static class Builder {
        /**
         * The service identifier.
         */
        private String builderServiceId;

        /**
         * The user identifier.
         */
        private String builderUserId;

        /**
         * The user's email address.
         */
        private String builderUserEmail;

        /**
         * The user's full name.
         */
        private String builderUserFullName;

        /**
         * The original token type.
         */
        private String builderOriginalTokenType;

        /**
         * Sets the service identifier.
         *
         * @param serviceId the service ID to set
         * @return this builder instance for method chaining
         */
        public Builder serviceId(final String serviceId) {
            this.builderServiceId = serviceId;
            return this;
        }

        /**
         * Sets the user identifier.
         *
         * @param userId the user ID to set
         * @return this builder instance for method chaining
         */
        public Builder userId(final String userId) {
            this.builderUserId = userId;
            return this;
        }

        /**
         * Sets the user's email address.
         *
         * @param userEmail the user email to set
         * @return this builder instance for method chaining
         */
        public Builder userEmail(final String userEmail) {
            this.builderUserEmail = userEmail;
            return this;
        }

        /**
         * Sets the user's full name.
         *
         * @param userFullName the user's full name to set
         * @return this builder instance for method chaining
         */
        public Builder userFullName(final String userFullName) {
            this.builderUserFullName = userFullName;
            return this;
        }

        /**
         * Sets the original token type.
         *
         * @param originalTokenType the original token type to set
         * @return this builder instance for method chaining
         */
        public Builder originalTokenType(final String originalTokenType) {
            this.builderOriginalTokenType = originalTokenType;
            return this;
        }

        /**
         * Builds and returns a new ServicePrincipal instance.
         *
         * @return a new ServicePrincipal with the configured properties
         */
        public ServicePrincipal build() {
            return new ServicePrincipal(this);
        }
    }

    /**
     * Returns a string representation of this ServicePrincipal.
     *
     * @return a string representation containing all field values
     */
    @Override
    public String toString() {
        return "ServicePrincipal{"
                + "serviceId='" + serviceId + '\''
                + ", userId='" + userId + '\''
                + ", userEmail='" + userEmail + '\''
                + ", userFullName='" + userFullName + '\''
                + ", originalTokenType='" + originalTokenType + '\''
                + '}';
    }
}
