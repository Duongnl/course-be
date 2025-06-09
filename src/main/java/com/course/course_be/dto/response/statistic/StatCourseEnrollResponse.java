package com.course.course_be.dto.response.statistic;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatCourseEnrollResponse {
    String name;
    String imgUrl;
    long enrollmentCount;
}
