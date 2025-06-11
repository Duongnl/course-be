package com.course.course_be.dto.request.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateLessonRequest {
    @NotNull
    @NotBlank(message = "NOT_BLANK")
    @Size(max = 255, message = "NAME_TOO_LONG")
    String name;
    String detail;
    @NotNull
    Integer lessonNumber;
    @NotNull
    String videoUrl;
    int duration = 0;
    String documentUrl;
    String assignmentUrl;
    @NotNull
    Integer chapterId;
}
