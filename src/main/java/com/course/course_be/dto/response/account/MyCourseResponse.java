package com.course.course_be.dto.response.account;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyCourseResponse {

    String id;
    String name;
    long viewedLessons;
    long totalLessons;
    LocalDateTime enrolledAt;
    String status;
    String imageUrl;
}