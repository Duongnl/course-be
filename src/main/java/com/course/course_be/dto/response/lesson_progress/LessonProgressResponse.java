package com.course.course_be.dto.response.lesson_progress;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonProgressResponse {
    Integer id;
    String accountId;
    String lessonId;
    Integer watchedDuration;
    String status;
}
