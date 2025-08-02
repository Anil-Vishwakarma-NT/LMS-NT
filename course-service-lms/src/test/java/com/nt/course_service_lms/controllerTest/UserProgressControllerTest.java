package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.ServicePrincipal;
import com.nt.course_service_lms.controller.UserProgressController;
import com.nt.course_service_lms.dto.outDTO.UserProgressOutDTO;
import com.nt.course_service_lms.dto.outDTO.CourseProgressWithMetaDTO;
import com.nt.course_service_lms.service.UserProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProgressController.class)
@Import(UserProgressControllerTest.TestConfig.class)
class UserProgressControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserProgressService userProgressService() {
            return Mockito.mock(UserProgressService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserProgressService userProgressService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserProgressOutDTO progressDTO;

    @BeforeEach
    void setUp() {
        progressDTO = new UserProgressOutDTO();
        progressDTO.setUserId(1L);
        progressDTO.setCourseId(100L);
        progressDTO.setContentId(200L);
        progressDTO.setContentCompletionPercentage(75.0);
        progressDTO.setLastPosition(300);
    }

    @Test
    void updateProgress_shouldSucceed() throws Exception {
        mockMvc.perform(post("/api/service-api/user-progress/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(progressDTO)))
                .andExpect(status().isOk());

        verify(userProgressService).updateProgress(any(UserProgressOutDTO.class));
    }

    @Test
    void getCourseProgressWithMetaWithId_shouldReturnData() throws Exception {
        CourseProgressWithMetaDTO expected = new CourseProgressWithMetaDTO(90.0, LocalDateTime.now());

        when(userProgressService.getCourseProgressWithMeta(1L, 100L)).thenReturn(expected);

        mockMvc.perform(get("/api/service-api/user-progress/meta")
                        .param("userId", "1")
                        .param("courseId", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completionPercentage").value(90.0));

        verify(userProgressService).getCourseProgressWithMeta(1L, 100L);
    }

    @Test
    void getLastPosition_shouldReturnPosition() throws Exception {
        when(userProgressService.getLastPosition(1L, 100L, 200L)).thenReturn(300);

        mockMvc.perform(get("/api/service-api/user-progress/last-position")
                        .param("userId", "1")
                        .param("courseId", "100")
                        .param("contentId", "200"))
                .andExpect(status().isOk())
                .andExpect(content().string("300"));

        verify(userProgressService).getLastPosition(1L, 100L, 200L);
    }

    @Test
    void getContentProgress_shouldReturnValue() throws Exception {
        when(userProgressService.getContentProgress(1L, 100L, 200L)).thenReturn(55.5);

        mockMvc.perform(get("/api/service-api/user-progress/content")
                        .param("userId", "1")
                        .param("courseId", "100")
                        .param("contentId", "200"))
                .andExpect(status().isOk())
                .andExpect(content().string("55.5"));

        verify(userProgressService).getContentProgress(1L, 100L, 200L);
    }

//    @Test
//    void getCourseProgressWithMetaCourseId_shouldReturnData() throws Exception {
//        CourseProgressWithMetaDTO expected = new CourseProgressWithMetaDTO(100.0, LocalDateTime.now());
//
//        // Build mock principal (adjust method names if needed)
//        ServicePrincipal principal = ServicePrincipal.builder()
//                .userId("1")
//                .email("test@example.com")
//                .role("USER")
//                .build();
//
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getPrincipal()).thenReturn(principal);
//        SecurityContext context = mock(SecurityContext.class);
//        when(context.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(context);
//
//        when(userProgressService.getCourseProgressWithMeta(1L, 100L)).thenReturn(expected);
//
//        mockMvc.perform(get("/api/service-api/user-progress/meta-courseId")
//                        .param("courseId", "100"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.completionPercentage").value(100.0));
//
//        verify(userProgressService).getCourseProgressWithMeta(1L, 100L);
//    }


    @Test
    void getCourseProgressWithMetaCourseId_shouldFailOnMissingPrincipal() throws Exception {
        // No ServicePrincipal in authentication
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("someUser");
        SecurityContextHolder.setContext(mock(SecurityContext.class));
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(auth);

        mockMvc.perform(get("/api/service-api/user-progress/meta-courseId")
                        .param("courseId", "100"))
                .andExpect(status().isNotFound());
    }
    @Test
    void getCourseProgressWithMetaWithId_empty_shouldReturnDefault() throws Exception {
        CourseProgressWithMetaDTO dto = new CourseProgressWithMetaDTO(0.0, null);
        when(userProgressService.getCourseProgressWithMeta(1L, 2L)).thenReturn(dto);

        mockMvc.perform(get("/api/service-api/user-progress/meta")
                        .param("userId", "1")
                        .param("courseId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completionPercentage").value(0.0))
                .andExpect(jsonPath("$.firstCompletedAt").doesNotExist());
    }
    @Test
    void getContentProgress_zeroProgress_shouldReturnZero() throws Exception {
        when(userProgressService.getContentProgress(1L, 2L, 3L)).thenReturn(0.0);

        mockMvc.perform(get("/api/service-api/user-progress/content")
                        .param("userId", "1")
                        .param("courseId", "2")
                        .param("contentId", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string("0.0"));
    }
    @Test
    void getLastPosition_nullResponse_shouldReturnZero() throws Exception {
        when(userProgressService.getLastPosition(1L, 2L, 3L)).thenReturn(0);

        mockMvc.perform(get("/api/service-api/user-progress/last-position")
                        .param("userId", "1")
                        .param("courseId", "2")
                        .param("contentId", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
    @Test
    void getCourseProgressWithMetaCourseId_invalidPrincipal_shouldThrowException() throws Exception {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/service-api/user-progress/meta-courseId")
                        .param("courseId", "123"))
                .andExpect(status().isNotFound());
    }

}

