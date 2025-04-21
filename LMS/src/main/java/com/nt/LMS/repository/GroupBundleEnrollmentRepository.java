package com.nt.LMS.repository;

import com.nt.LMS.entities.GroupBundleEnrollment;
import com.nt.LMS.entities.GroupCourseEnrollment;
import com.nt.LMS.entities.UserBundleEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupBundleEnrollmentRepository extends JpaRepository <GroupBundleEnrollment, Long> {
    Optional<GroupBundleEnrollment> findByGroupIdAndBundleIdAndStatusNotIn(Long groupId, Long bundleId, List<String> statuses);
    List<GroupBundleEnrollment> findByBundleId(Long bundleId);
}
