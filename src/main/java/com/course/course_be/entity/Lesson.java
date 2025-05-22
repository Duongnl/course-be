package com.course.course_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "lesson")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
// Bang bai hoc
public class Lesson {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

//   Stt cua bai hoc
    @Column(name = "lesson_numer", nullable = false)
    Integer lessonNumber;

//    Ten bai hoc
    @Column(name = "name", nullable = false)
    String name;

//    Link video cua bai hoc
    @Column(name = "video_url", nullable = true)
    String videoUrl;

//    Thoi luong cua bai hoc
    @Column(name = "duration", nullable = false)
    Integer duration;

//    Link tai lieu bai hoc
    @Column(name = "document_url", nullable = true)
    String documentUrl;

//    Link bai tap cua bai hoc
    @Column(name = "assignment_url", nullable = true)
    String assignmentUrl;

//    Mo ta bai hoc
    @Column(name = "detail" ,columnDefinition = "TEXT", nullable = true)
    String detail;

//    Thoi gian tao bai hoc
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;


//    Thoi gian cap nhat bai hoc
    @Column(name = "updated_at", nullable = true)
    LocalDateTime updatedAt;

//    Trang thai cua khoa hoc
    @Column(name = "status", nullable = false)
    String status;


//    Khoa hoc cua bai hoc
    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Chapter chapter;

//    Danh sach tien trinh cua bai hoc nay
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<LessonProgress> lessonProgresses;

//    Danh sach bai tap da nop cua bai hoc
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Submission> submissions;

//    Comment cua bai hoc
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Comment> comments;









}
