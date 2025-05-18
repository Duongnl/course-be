package com.course.course_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_enrollment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
// Bang dang ky khoa hoc
public class CourseEnrollment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

//  Thoi gian dky vao hoc
    @Column(name = "enrolled_at", nullable = false)
    LocalDateTime enrolledAt;

//    Trang thai
    @Column(name = "status", nullable = false)
    String status;

//    Khoa hoc
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Course course;

//   Tai khoan hoc vien
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Account account;

}
