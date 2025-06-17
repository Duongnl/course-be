package com.course.course_be.dto.response.courseclient;

import com.course.course_be.dto.response.category.CategoryResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseClientResponse {
    String id;
    String name;
    String detail;
    BigDecimal price;
    String videoUrl;
    String imageUrl;
    CategoryResponse category;
    List<ChapterClientResponse> chapters;
    Integer totalLessons;
    Integer totalDuration;
}
