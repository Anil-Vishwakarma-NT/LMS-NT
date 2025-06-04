package com.nt.LMS.repository;

import com.nt.LMS.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    Enrollment getByUserIdAndCourseId(Long userId, Long courseId);
    @Query(value = """
    SELECT course_id
    FROM enrollment
    WHERE is_enrolled = true
    GROUP BY course_id
    ORDER BY COUNT(*) DESC
    LIMIT 1
    """, nativeQuery = true)
    Long findMostFrequentEnrolledCourseId();

    @Query(value = "SELECT COUNT(*) FROM enrollment e WHERE e.user_id = :userId", nativeQuery = true)
    long getAllUserEnrols(@Param("userId") long userId);

    @Query(value = """
    SELECT COUNT(*) 
    FROM enrollment e
    JOIN users u ON e.user_id = u.user_id
    WHERE u.is_active = true AND u.user_id <> 1
    """, nativeQuery = true)
    long count();


}
