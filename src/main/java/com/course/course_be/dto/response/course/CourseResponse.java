package com.course.course_be.dto.response.course;
import com.course.course_be.dto.response.category.CategoryResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    String id;
    String name;
    String detail;
    BigDecimal price;
    String videoUrl;
    String imageUrl;
    CategoryResponse category;
    String status;
}
