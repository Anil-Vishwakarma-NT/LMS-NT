package com.nt.course_service_lms.constants;

public final class S3Constants {

    private S3Constants(){
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Production-optimized constants
    public static final long MIN_PART_SIZE = 5 * 1024 * 1024L; // 5MB (S3 minimum)
    public static final long OPTIMAL_PART_SIZE = 16 * 1024 * 1024L; // 16MB (sweet spot for most cases)
    public static final long MAX_PART_SIZE = 100 * 1024 * 1024L; // 100MB
    public static final long MULTIPART_THRESHOLD = 8 * 1024 * 1024L; // 8MB threshold
    public static final int STREAMING_CHUNK_SIZE = 1024 * 1024; // 1MB for better performance
    public static final int BUFFER_SIZE = 2 * 1024 * 1024; // 2MB buffer

    public static final long DEFAULT_CHUNK_SIZE = 1024 * 1024 * 5;// 5MB chunks


}
