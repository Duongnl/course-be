package com.course.course_be.dto.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusRequest {

    @NotNull(message = "COMMENT_ID_REQUIRED")
    private Integer commentId;

    @NotNull(message = "STATUS_REQUIRED")
    @NotBlank(message = "STATUS_REQUIRED")
    private String status;
}
