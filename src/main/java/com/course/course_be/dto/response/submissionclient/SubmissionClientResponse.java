package com.course.course_be.dto.response.submissionclient;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class SubmissionClientResponse {
    String id; // id bai nop
    String courseName; // ten khoa hoc
    String lessonName; // ten bai hoc
    String submissionUrl;
    String score;
    String comment;
    LocalDateTime submittedAt;
    String status; // trang thai

}
