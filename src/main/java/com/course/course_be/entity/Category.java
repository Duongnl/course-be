package com.course.course_be.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "category")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
// Bang danh muc khoa hoc
public class Category {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

//    Ten danh muc
    @Column(name = "name", nullable = false)
    String name;

//    Mo ta danh muc
    @Column(name = "detail" ,columnDefinition = "TEXT", nullable = true)
    String detail;

//    Trang thai khoa hoc
    @Column(name = "status", nullable = false)
    String status;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    //    Danh sach khoa hoc cua danh muc
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Course> courses;

}
