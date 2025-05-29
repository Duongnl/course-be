package com.course.course_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "chapter")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chapter {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "detail" ,columnDefinition = "TEXT", nullable = true)
    String detail;

    @Column(name = "chapter_numer", nullable = false)
    Integer chapterNumber;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "status", nullable = false)
    String status;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Course course;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    @JsonManagedReference
    @OrderBy("lessonNumber ASC")
    private Set<Lesson> lessons;


}
