package com.nt.user_service_lms.config;

import java.security.Principal;

/**
 * Represents the authenticated principal of a service-to-service request.
 * Contains identity and user-related information extracted from a JWT token.
 */
public final class ServicePrincipal implements Principal {

    /**
     * Unique identifier for the service (client ID).
     */
    private final String serviceId;

    /**
     * Unique identifier for the user.
     */
    private final String userId;

    /**
     * Email of the user.
     */
    private final String userEmail;

    /**
     * Full name of the user.
     */
    private final String userFullName;

    /**
     * Type of token originally used (e.g., SERVICE, USER).
     */
    private final String originalTokenType;

    /**
     * Private constructor to enforce usage of the builder pattern.
     *
     * @param builder the builder instance containing the values to set
     */
    public ServicePrincipal(final Builder builder) {
        this.serviceId = builder.serviceId;
        this.userId = builder.userId;
        this.userEmail = builder.userEmail;
        this.userFullName = builder.userFullName;
        this.originalTokenType = builder.originalTokenType;
    }

    /**
     * Returns the service name, falling back to "unknown-service" if unavailable.
     *
     * @return the service identifier or a default value
     */
    @Override
    public String getName() {
        return serviceId != null ? serviceId : "unknown-service";
    }

    /**
     * @return the service ID
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return the user's email
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * @return the user's full name
     */
    public String getUserFullName() {
        return userFullName;
    }

    /**
     * @return the original token type
     */
    public String getOriginalTokenType() {
        return originalTokenType;
    }

    /**
     * Builder class for constructing {@link ServicePrincipal} instances.
     * Uses the builder pattern to provide a fluent API for creating ServicePrincipal objects.
     */
    public static class Builder {

        /**
         * Unique identifier for the service (client ID).
         */
        private String serviceId;

        /**
         * Unique identifier for the user.
         */
        private String userId;

        /**
         * Email of the user.
         */
        private String userEmail;

        /**
         * Full name of the user.
         */
        private String userFullName;

        /**
         * Type of token originally used (e.g., SERVICE, USER).
         */
        private String originalTokenType;


        /**
         * Sets the service ID.
         *
         * @param serviceIdValue the client ID of the service
         * @return the builder instance
         */
        public Builder serviceId(final String serviceIdValue) {
            this.serviceId = serviceIdValue;
            return this;
        }

        /**
         * Sets the user ID.
         *
         * @param userIdValue the unique user identifier
         * @return the builder instance
         */
        public Builder userId(final String userIdValue) {
            this.userId = userIdValue;
            return this;
        }

        /**
         * Sets the user email.
         *
         * @param userEmailValue the user's email address
         * @return the builder instance
         */
        public Builder userEmail(final String userEmailValue) {
            this.userEmail = userEmailValue;
            return this;
        }

        /**
         * Sets the user's full name.
         *
         * @param userFullNameValue the user's full name
         * @return the builder instance
         */
        public Builder userFullName(final String userFullNameValue) {
            this.userFullName = userFullNameValue;
            return this;
        }

        /**
         * Sets the original token type.
         *
         * @param originalTokenTypeValue the original token type (e.g., "SERVICE", "USER")
         * @return the builder instance
         */
        public Builder originalTokenType(final String originalTokenTypeValue) {
            this.originalTokenType = originalTokenTypeValue;
            return this;
        }

        /**
         * Builds the {@link ServicePrincipal} instance.
         *
         * @return a new ServicePrincipal object
         */
        public ServicePrincipal build() {
            return new ServicePrincipal(this);
        }
    }

    /**
     * Returns a string representation of the service principal.
     *
     * @return string describing the principal
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
