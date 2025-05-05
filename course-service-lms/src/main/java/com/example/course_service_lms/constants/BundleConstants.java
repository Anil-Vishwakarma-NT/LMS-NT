package com.example.course_service_lms.constants;

/**
 * Utility class that contains constant values used across the Course Bundle module.
 * <p>
 * This class is not meant to be instantiated.
 * All members are static and provide standardized messages for validations and exceptions.
 */
public final class BundleConstants {

  /**
   * Private constructor to prevent instantiation of this utility class.
   * Throws an {@link UnsupportedOperationException} if attempted.
   */
  private BundleConstants() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /** Message used when a bundle with the given name already exists. */
  public static final String BUNDLE_ALREADY_EXISTS = "A bundle with the name '%s' already exists.";

  /** Message used when a bundle is not found for a given ID. */
  public static final String BUNDLE_NOT_FOUND_BY_ID = "Bundle with ID %d not found";

  /** Message used when no bundles are found in the system. */
  public static final String NO_BUNDLES_FOUND = "No bundles found";

  /** Generic error message for unexpected situations. */
  public static final String GENERAL_ERROR = "Something went wrong";

  /** Validation message indicating that the bundle name cannot be empty. */
  public static final String BUNDLE_NAME_NOT_BLANK = "Bundle Name cannot be empty";

  /** Validation message indicating the minimum required length for a bundle name. */
  public static final String BUNDLE_NAME_MIN_LENGTH = "Bundle Name must be at least 3 characters long";

  /** Validation message used when the bundle name format is invalid. */
  public static final String BUNDLE_NAME_INVALID = "Bundle Name invalid";

  /** Validation indicating the minimum required length for a bundle name. */
  public static final int INT_VALUE_3 = 3;
}

