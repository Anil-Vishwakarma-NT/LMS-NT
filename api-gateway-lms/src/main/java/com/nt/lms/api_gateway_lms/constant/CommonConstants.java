package com.nt.lms.api_gateway_lms.constant;

/**
 * Utility class that contains constant values used across the application.
 * <p>
 * This class is not meant to be instantiated.
 * All members are static and provide standardized values for common use cases.
 */
public final class CommonConstants {
    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws an {@link UnsupportedOperationException} if attempted.
     */
    private CommonConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Long 3600 constant.
     */
    public static final long LONG_THREE_THOUSAND_SIX_HUNDRED = 3600L;

    /**
     * Integer 500 constant.
     */
    public static final int INTEGER_FIVE_HUNDRED = 500;

    /**
     * Integer 400 constant.
     */
    public static final int INTEGER_FOUR_HUNDRED = 400;

    /**
     * Integer 401 constant.
     */
    public static final int INTEGER_FOUR_HUNDRED_ONE = 401;

    /**
     * Integer 404 constant.
     */
    public static final int INTEGER_FOUR_HUNDRED_FOUR = 404;
    /**
     * Integer 1000 constant.
     */
    public static final int INTEGER_ONE_THOUSAND = 1000;
}
