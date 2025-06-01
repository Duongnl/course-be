package com.course.course_be.dto.response.homeclient;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class CourseEnrollCardResponse {
    String name;
    String imageUrl;
    String id;
}
