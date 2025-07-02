package com.course.course_be.dto.request.course_enrollment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseEnrollmentAdminCreateRequest {

    @NotNull(message = "COURSE_ID_REQUIRED")
    @NotBlank(message = "COURSE_ID_REQUIRED")
    private String courseId;

    @NotNull(message = "ACCOUNT_ID_REQUIRED")
    @NotBlank(message = "ACCOUNT_ID_REQUIRED")
    private String accountId;
}
