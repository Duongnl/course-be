package com.course.course_be.dto.response.homeclient;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseCardResponse {
    String name;
    String imageUrl;
    String id;
    BigDecimal price;
    LocalDateTime createdAt;

}
