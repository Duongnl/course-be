package com.course.course_be.dto.response.course;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponseAdmin {
    String id;
    String courseId;
    String courseName;
    String accountId;
    String accountName;
    String enrolledAt;
    String status;
}
