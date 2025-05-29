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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChapterSidebarResponse {
    Integer id;
    String name;
    Integer chapterNumber;
    List<LessonSidebarResponse> lessons;
}
