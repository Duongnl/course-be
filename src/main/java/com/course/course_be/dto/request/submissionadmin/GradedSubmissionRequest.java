package com.course.course_be.dto.request.submissionadmin;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GradedSubmissionRequest {

    @NotNull(message = "NOT_NULL")
    Double  score;

    @NotNull(message = "NOT_NULL")
    String comment;
}
