package com.course.course_be.dto.request.course;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCourseRequest {
    @NotNull
    @NotBlank(message = "NOT_BLANK")
    @Size(max = 255, message = "NAME_TOO_LONG")
    String name;
    String detail;
    @NotNull
    @Digits(integer = 15, fraction = 0, message = "Invalid price")
    BigDecimal price;
    String imageUrl;
    String videoUrl;
    @NotNull
    String categoryId;
}
