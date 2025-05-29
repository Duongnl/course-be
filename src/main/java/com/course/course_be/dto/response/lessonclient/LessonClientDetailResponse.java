package com.course.course_be.dto.response.lessonclient;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonClientDetailResponse {

    String id;
    Integer lessonNumber;

    String name;

    String videoUrl;

    Integer duration;

    String documentUrl;

    String assignmentUrl;

    String detail;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String status;
}
