package com.course.course_be.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    private String courseId;
    private String lessonId;
    private String commentParentId;

    @NotNull(message = "CONTENT_REQUIRED")
    @NotBlank(message = "CONTENT_REQUIRED")
    @Size(max = 1023, message = "CONTENT_TOO_LONG")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹ0-9 .,!?()\"':;\\-\\n\\r]+$", message = "CONTENT_INVALID_CHAR")
    private String content;
}
