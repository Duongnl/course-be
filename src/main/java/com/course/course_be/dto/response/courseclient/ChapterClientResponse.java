package com.course.course_be.dto.response.courseclient;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterClientResponse {
    Integer id;
    String name;
    Integer chapterNumber;
    Set<LessonClientResponse> lessons;
}
