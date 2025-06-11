package com.course.course_be.dto.response.chapter;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterResponse {
    int id;
    Integer chapterNumber;
    String name;
    String detail;
    String courseId;
    String status;
}
