package com.course.course_be.dto.response.lesson;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponse {
    String id;
    int lessonNumber;
    String name;
    String detail;
    String videoUrl;
    int duration;
    String documentUrl;
    String assignmentUrl;
    int chapterId;
    String status;
}
