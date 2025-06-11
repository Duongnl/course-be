package com.course.course_be.dto.request.chapter;

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
public class CreateChapterRequest {
    @NotNull
    Integer chapterNumber;
    @NotNull
    @NotBlank(message = "NOT_BLANK")
    @Size(max = 255, message = "NAME_TOO_LONG")
    String name;
    String detail;
    @NotNull
    String courseId;
}
