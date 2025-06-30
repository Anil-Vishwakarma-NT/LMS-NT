package com.nt.LMS.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-local context for storing service tokens during request processing
 */
public class ServiceTokenContext {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTokenContext.class);

    private static final ThreadLocal<String> CURRENT_TOKEN = new ThreadLocal<>();
    private static final ThreadLocal<String> ORIGINAL_TOKEN_TYPE = new ThreadLocal<>();

    /**
     * Sets the current service token for this thread
     */
    public static void setCurrentToken(String token) {
        CURRENT_TOKEN.set(token);
        logger.debug("Set current service token in thread context");
    }

    /**
     * Gets the current service token for this thread
     */
    public static String getCurrentToken() {
        return CURRENT_TOKEN.get();
    }

    /**
     * Sets the original token type for this thread
     */
    public static void setOriginalTokenType(String originalTokenType) {
        ORIGINAL_TOKEN_TYPE.set(originalTokenType);
    }

    /**
     * Gets the original token type for this thread
     */
    public static String getOriginalTokenType() {
        return ORIGINAL_TOKEN_TYPE.get();
    }

    /**
     * Clears the thread-local context
     * Should be called at the end of request processing
     */
    public static void clear() {
        CURRENT_TOKEN.remove();
        ORIGINAL_TOKEN_TYPE.remove();
        logger.debug("Cleared service token context");
    }

    /**
     * Checks if a token is available in the current context
     */
    public static boolean hasToken() {
        return CURRENT_TOKEN.get() != null;
    }
}