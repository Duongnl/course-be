package com.course.course_be.dto.response.lessonclient;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class LessonSidebarResponse {

    String id;
    String name;
    Integer lessonNumber;
    Integer duration;
    boolean viewed;


}
