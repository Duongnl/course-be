package com.course.course_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "comment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

//    Noi dung comment
    @Column(name = "content" ,columnDefinition = "TEXT", nullable = false)
    String content;

//    Thoi gian comment
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

//    Trang thai
    @Column(name = "status", nullable = false)
    String status;

//    Khoa hoc cua comment
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = true, referencedColumnName = "id")
    @JsonBackReference
    private Course course ;

//    Bai hoc comment
    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = true, referencedColumnName = "id")
    @JsonBackReference
    private Lesson lesson;

//    Tai khoan comment
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Account account;

//    Comment duoc tra loi
    @ManyToOne
    @JoinColumn(name = "comment_id ", nullable = true, referencedColumnName = "id")
    @JsonBackReference
    private Comment parentComment;

//    Tat ca cac comment da tra loi
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Comment> commentReplies;

}
