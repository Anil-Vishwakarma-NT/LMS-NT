package com.nt.user_service_lms.service.serviceImpl;

import com.nt.user_service_lms.constants.CommonConstants;
import com.nt.user_service_lms.dto.UsersDetailsViewDTO;
import com.nt.user_service_lms.dto.outDTO.CourseDeadlinesDTO;
import com.nt.user_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserCourseEnrollDetails;
import com.nt.user_service_lms.entities.Enrollment;
import com.nt.user_service_lms.entities.Role;
import com.nt.user_service_lms.entities.User;
import com.nt.user_service_lms.exception.ResourceNotFoundException;
import com.nt.user_service_lms.feignClient.CourseMicroserviceClient;
import com.nt.user_service_lms.repository.EnrollmentRepository;
import com.nt.user_service_lms.repository.RoleRepository;
import com.nt.user_service_lms.repository.UserGroupRepository;
import com.nt.user_service_lms.repository.UserRepository;
import com.nt.user_service_lms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nt.user_service_lms.constants.CommonConstants.NUMBER_FIVE;
import static com.nt.user_service_lms.constants.UserConstants.USER_NOT_FOUND;


/**
 * Service implementation for user-related operations.
 * Handles user authentication, statistics, course deadlines, and enrollment details.
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    /**
     * Repository for accessing user data.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Repository for accessing role data.
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Repository for accessing enrollment data.
     */
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    /**
     * Repository for accessing user group data.
     */
    @Autowired
    private UserGroupRepository userGroupRepository;

    /**
     * Feign client for communicating with the course microservice.
     */
    @Autowired
    private CourseMicroserviceClient courseMicroserviceClient;

    /**
     * Loads user details by email for authentication.
     *
     * @param email the email of the user
     * @return UserDetails for Spring Security
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND + " : " + email));
        Optional<Role> role = roleRepository.findById(user.getRoleId());
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(role.get().getName())
                .build();
    }

    /**
     * Counts the number of active users excluding the user with ID 1.
     *
     * @return the count of active users
     */
    @Override
    public long countActiveUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.isActive() && user.getUserId() != 1)
                .count();
    }

    /**
     * Retrieves details of recently added users.
     *
     * @return list of UsersDetailsViewDTO containing user details
     */
    @Override
    public List<UsersDetailsViewDTO> getRecentUserDetails() {
        List<Object[]> results = userRepository.fetchRecentUserDetails();
        return results.stream().map(obj -> {
            UsersDetailsViewDTO dto = new UsersDetailsViewDTO();
            dto.setFullName((String) obj[0]);
            dto.setEmail((String) obj[1]);
            dto.setRole((String) obj[2]);
            dto.setManagerName((String) obj[CommonConstants.NUMBER_THREE]);
            dto.setCreatedAt((Timestamp) obj[CommonConstants.NUMBER_FOUR]);
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Provides statistics for a user such as enrollments and groups.
     *
     * @param userId the user ID
     * @return map containing statistics
     */
    @Override
    public Map<String, Long> userStatistics(long userId) {
        Map<String, Long> stats = new HashMap<>();
        Long enrols = enrollmentRepository.getUserTotalEnrollments(userId);
        stats.put("enrollments", enrols);
        long userGroup = userGroupRepository.getAllUserGroups(userId);
        stats.put("groups", userGroup);

        return stats;
    }

    /**
     * Retrieves courses with deadlines within the next 5 days for a user.
     *
     * @param email the user's email
     * @return StandardResponseOutDTO containing a list of CourseDeadlinesDTO
     */
    @Override
    public StandardResponseOutDTO<List<CourseDeadlinesDTO>> deadlineCourses(final String email) {
        try {
            Optional<User> user = userRepository.findByEmailIgnoreCase(email);
            if (user.isPresent()) {
                List<Enrollment> enrols = enrollmentRepository.findByUserId(user.get().getUserId());
                LocalDate today = LocalDate.now();
                LocalDate later = today.plusDays(NUMBER_FIVE);
                List<Enrollment> filteredEnrols = enrols.stream()
                        .filter(enrol ->
                                enrol.getDeadline() != null
                                        && !enrol.getDeadline().isBefore(today.atStartOfDay())
                                        && !enrol.getDeadline().isAfter(later.atStartOfDay())
                        )
                        .collect(Collectors.toList());
                List<CourseDeadlinesDTO> courses = new ArrayList<>();
                for (Enrollment enrol : filteredEnrols) {
                    CourseInfoOutDTO course = courseMicroserviceClient.getCourseById(enrol.getCourseId()).getBody().getData();
                    CourseDeadlinesDTO deadlinecourse = new CourseDeadlinesDTO();
                    deadlinecourse.setCourseId(course.getCourseId());
                    deadlinecourse.setTitle(course.getTitle());
                    deadlinecourse.setOwnerId(course.getOwnerId());
                    deadlinecourse.setDeadline(enrol.getDeadline());
                    courses.add(deadlinecourse);
                }
                return StandardResponseOutDTO.success(courses, null);
            } else {
                throw new ResourceNotFoundException(USER_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the earliest active enrollments for each course for a user.
     *
     * @param userId the user ID
     * @return list of UserCourseEnrollDetails
     */
    @Override
    public List<UserCourseEnrollDetails> getUserEnrolledCourses(final Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserIdAndIsActiveTrue(userId);
        Map<Long, Enrollment> earliestCourseEnrollments = new HashMap<>();
        for (Enrollment e : enrollments) {
            Long courseId = e.getCourseId();
            if (courseId == null) {
                continue;
            }
            if (!earliestCourseEnrollments.containsKey(courseId)
                    || e.getAssignedAt().isBefore(earliestCourseEnrollments.get(courseId).getAssignedAt())) {
                earliestCourseEnrollments.put(courseId, e);
            }
        }
        return earliestCourseEnrollments.values().stream()
                .map(e -> {
                    UserCourseEnrollDetails dto = new UserCourseEnrollDetails();
                    dto.setCourseId(e.getCourseId());
                    dto.setAssignedById(e.getAssignedBy());
                    dto.setEnrollmentDate(e.getAssignedAt());
                    dto.setDeadline(e.getDeadline());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
