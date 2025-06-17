package com.course.course_be.dto.response.course_enrollment;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseEnrollmentResponse {
    Integer id;
    String courseId;
    String accountId;
    LocalDateTime enrolledAt;
    String status;
}
