package com.course.course_be.dto.response.submissionadmin;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionAdminResponse {
   String id; // id bai nop
   String courseName; // ten khoa hoc
   String lessonName; // ten bai hoc

   String submitterEmail; // email nguoi nop
   String submitterName; // ten nguoi nop
   String submissionUrl;

   String graderEmail;
   String graderName;
   String score;
   String comment;
   String  reviewedAt;
   LocalDateTime submittedAt;
   String status; // trang thai
}
