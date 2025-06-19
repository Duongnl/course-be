package com.course.course_be.dto.response.courseclient;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterClientResponse {
    Integer id;
    String name;
    Integer chapterNumber;
    List<LessonClientResponse> lessons;
}
