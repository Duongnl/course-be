package com.course.course_be.dto.request.course_enrollment;

import lombok.Data;

@Data
public class ChangeStatusRequest {
    Integer courseEnrollmentId;
    String status;
}
