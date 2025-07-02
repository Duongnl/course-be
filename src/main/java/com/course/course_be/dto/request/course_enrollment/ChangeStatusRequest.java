package com.course.course_be.dto.request.course_enrollment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeStatusRequest {

    @NotNull(message = "COURSE_ENROLLMENT_ID_REQUIRED")
    private Integer courseEnrollmentId;

    @NotNull(message = "STATUS_REQUIRED")
    @NotBlank(message = "STATUS_REQUIRED")
    private String status;
}
