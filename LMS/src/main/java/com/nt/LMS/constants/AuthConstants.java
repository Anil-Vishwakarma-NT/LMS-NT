package com.nt.LMS.constants;

public final class AuthConstants {
    /**
     * To provide time for token expiry.
     */
    public static final long REFRESH_TOKEN_EXPIRY_SECONDS = 7L * 24 * 60 * 60;
    private AuthConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}
