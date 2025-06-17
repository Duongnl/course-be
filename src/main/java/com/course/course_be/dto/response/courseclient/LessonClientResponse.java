package com.course.course_be.dto.response.courseclient;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonClientResponse {
    String id;
    Integer lessonNumber;
    String name;
    Integer duration;
}
