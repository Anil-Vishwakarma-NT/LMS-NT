package com.nt.course_service_lms.config;

import java.security.Principal;

/**
 * Custom principal for service authentication
 */
public class ServicePrincipal implements Principal {

    private final String serviceId;
    private final String userId;
    private final String userEmail;
    private final String userFullName;
    private final String originalTokenType;

    private ServicePrincipal(Builder builder) {
        this.serviceId = builder.serviceId;
        this.userId = builder.userId;
        this.userEmail = builder.userEmail;
        this.userFullName = builder.userFullName;
        this.originalTokenType = builder.originalTokenType;
    }

    @Override
    public String getName() {
        return serviceId != null ? serviceId : "unknown-service";
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public String getOriginalTokenType() {
        return originalTokenType;
    }

    public static class Builder {
        private String serviceId;
        private String userId;
        private String userEmail;
        private String userFullName;
        private String originalTokenType;

        public Builder serviceId(String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder userEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public Builder userFullName(String userFullName) {
            this.userFullName = userFullName;
            return this;
        }

        public Builder originalTokenType(String originalTokenType) {
            this.originalTokenType = originalTokenType;
            return this;
        }

        public ServicePrincipal build() {
            return new ServicePrincipal(this);
        }
    }

    @Override
    public String toString() {
        return "ServicePrincipal{" +
                "serviceId='" + serviceId + '\'' +
                ", userId='" + userId + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userFullName='" + userFullName + '\'' +
                ", originalTokenType='" + originalTokenType + '\'' +
                '}';
    }
}