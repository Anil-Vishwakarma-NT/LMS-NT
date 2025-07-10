package com.nt.user_service_lms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-local context for storing service tokens during request processing.
 */
public final class ServiceTokenContext {

    /**
     * Logger instance for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceTokenContext.class);

    /**
     * Thread-local storage for the current service token.
     * Each thread maintains its own token value.
     */
    private static final ThreadLocal<String> CURRENT_TOKEN = new ThreadLocal<>();

    /**
     * Thread-local storage for the original token type.
     * Each thread maintains its own original token type value.
     */
    private static final ThreadLocal<String> ORIGINAL_TOKEN_TYPE = new ThreadLocal<>();

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ServiceTokenContext() {
        // Utility class - prevent instantiation
    }

    /**
     * Sets the current service token for this thread.
     *
     * @param token the service token to store in the current thread context
     */
    public static void setCurrentToken(final String token) {
        CURRENT_TOKEN.set(token);
        LOGGER.debug("Set current service token in thread context");
    }

    /**
     * Gets the current service token for this thread.
     *
     * @return the service token stored in the current thread context, or null if not set
     */
    public static String getCurrentToken() {
        return CURRENT_TOKEN.get();
    }

    /**
     * Sets the original token type for this thread.
     *
     * @param originalTokenType the original token type to store in the current thread context
     */
    public static void setOriginalTokenType(final String originalTokenType) {
        ORIGINAL_TOKEN_TYPE.set(originalTokenType);
    }

    /**
     * Gets the original token type for this thread.
     *
     * @return the original token type stored in the current thread context, or null if not set
     */
    public static String getOriginalTokenType() {
        return ORIGINAL_TOKEN_TYPE.get();
    }

    /**
     * Clears the thread-local context.
     * Should be called at the end of request processing to prevent memory leaks.
     */
    public static void clear() {
        CURRENT_TOKEN.remove();
        ORIGINAL_TOKEN_TYPE.remove();
        LOGGER.debug("Cleared service token context");
    }

    /**
     * Checks if a token is available in the current context.
     *
     * @return true if a token is set in the current thread context, false otherwise
     */
    public static boolean hasToken() {
        return CURRENT_TOKEN.get() != null;
    }
}
