package com.course.course_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "submission")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
// Bang bai tap
public class Submission {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

//    Link bai nop
    @Column(name = "submission_url", nullable = false)
    String submissionUrl;

//    Thoi gian nop bai tap
    @Column(name = "submitted_at", nullable = false)
    LocalDateTime submittedAt;

//     Diem
    @Column(name = "score", nullable = true)
    Double  score;

//    Nhan xet
    @Column(name = "comment" ,columnDefinition = "TEXT", nullable = true)
    String comment;

//    Thoi gian cham
    @Column(name = "reviewed_at", nullable = true)
    LocalDateTime reviewedAt;

//    Trang thai bai tap
    @Column(name = "status", nullable = false)
    String status;


//    Bai hoc cua bai tap
    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Lesson lesson;

//    Nguoi nop bai
    @ManyToOne
    @JoinColumn(name = "submitter_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Account accountSubmitter;


//    Nguoi cham bai
    @ManyToOne
    @JoinColumn(name = "grader_id", nullable = true, referencedColumnName = "id")
    @JsonBackReference
    private Account accountGrader;

}
