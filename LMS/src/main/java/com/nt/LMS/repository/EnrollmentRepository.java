//package com.nt.LMS.repository;
//
//import com.nt.LMS.entities.Enrollment;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
//    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
//    Enrollment getByUserIdAndCourseId(Long userId, Long courseId);
//    @Query(value = """
//    SELECT course_id
//    FROM enrollment
//    WHERE is_enrolled = true
//    GROUP BY course_id
//    ORDER BY COUNT(*) DESC
//    LIMIT 1
//    """, nativeQuery = true)
//    Long findMostFrequentEnrolledCourseId();
//
//    @Query(value = "SELECT COUNT(*) FROM enrollment e WHERE e.user_id = :userId", nativeQuery = true)
//    long getAllUserEnrols(@Param("userId") long userId);
//
//    @Query(value = """
//    SELECT COUNT(*)
//    FROM enrollment e
//    JOIN users u ON e.user_id = u.user_id
//    WHERE u.is_active = true AND u.user_id <> 1
//    """, nativeQuery = true)
//    long count();
//
//
//}

package com.nt.LMS.repository;

import com.nt.LMS.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT e FROM Enrollment e WHERE e.userId = :userId AND e.courseId = :courseId AND e.isActive = true AND e.status NOT IN ('COMPLETED', 'EXPIRED', 'UNENROLLED')")
    Optional<Enrollment> findActiveEnrollmentByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.userId = :userId AND e.bundleId = :bundleId AND e.isActive = true AND e.status NOT IN ('COMPLETED', 'EXPIRED', 'UNENROLLED')")
    Optional<Enrollment> findActiveEnrollmentByUserAndBundle(@Param("userId") Long userId, @Param("bundleId") Long bundleId);

    @Query("SELECT e FROM Enrollment e WHERE e.groupId = :groupId AND e.courseId = :courseId AND e.isActive = true AND e.status NOT IN ('COMPLETED', 'EXPIRED', 'UNENROLLED')")
    Optional<Enrollment> findActiveEnrollmentByGroupAndCourse(@Param("groupId") Long groupId, @Param("courseId") Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.groupId = :groupId AND e.bundleId = :bundleId AND e.isActive = true AND e.status NOT IN ('COMPLETED', 'EXPIRED', 'UNENROLLED')")
    Optional<Enrollment> findActiveEnrollmentByGroupAndBundle(@Param("groupId") Long groupId, @Param("bundleId") Long bundleId);
}
