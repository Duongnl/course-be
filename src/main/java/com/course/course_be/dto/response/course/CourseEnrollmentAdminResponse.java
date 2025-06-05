package com.course.course_be.dto.response.course;

import lombok.Data;

@Data
public class CourseEnrollmentAdminResponse {
    String id;
    String courseId;
    String courseName;
    String accountId;
    String accountName;
    String enrolledAt;
    String status;
}
