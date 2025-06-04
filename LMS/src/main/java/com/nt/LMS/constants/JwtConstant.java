package com.nt.LMS.constants;

public final class JwtConstant {

    /**
     * Token Bearer String length,to be excluded before getting token.
     */
    public static final int BEARER_PREFIX_LENGTH = 7;

    /**
     * clocked skew seconds in extract claims.
     */
    public static final int ALLOWED_CLOCK_SKEW_SECONDS = 300;

    // Private constructor to prevent instantiation
    private JwtConstant() {
        throw new UnsupportedOperationException("Utility class");
    }
}

