package com.course.course_be.dto.response.course;
import com.course.course_be.dto.response.category.CategoryResponse;
import lombok.Data;

import java.math.BigDecimal;

@Data
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
