package com.course.course_be.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "lesson_progress")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
// Bang tien trinh cua bai hoc
public class LessonProgress {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

//    Tien do da xem
    @Column(name = "watched_duration", nullable = false)
    int watchedDuration;

//    Trang thai tien trinh
    @Column(name = "status", nullable = false)
    String status;

//   Tai khoan hoc vien
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Account account;

//    Bai hoc cua tien trinh
    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Lesson lesson;
}
