package com.course.course_be.dto.request.submissionclient;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionClientRequest {

    @NotBlank(message = "NOT_BLANK")
    String submissionUrl;

    @NotBlank(message = "NOT_BLANK")
    String lessonId;

}
