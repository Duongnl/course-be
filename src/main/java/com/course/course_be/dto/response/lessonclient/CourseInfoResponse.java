package com.course.course_be.dto.response.lessonclient;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class CourseInfoResponse {
    String id;
    String name;
    List<ChapterSidebarResponse> chapters;



}
