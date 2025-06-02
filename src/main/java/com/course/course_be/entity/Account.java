package com.course.course_be.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "account")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
// Bang tai khoan cua hoc vien
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

//    Id cua fb khi dang nhap
    @Column(name = "google_id", nullable = true)
    String googleId;

//    Email cua tai khoan
    @Column(name = "email", nullable = true)
    String email;

    @Column(name = "username", nullable = true)
    String username;

    @Column(name = "password", nullable = true)
    String password;

    //    Sdt cua tai khoan
    @Column(name = "phone_number", nullable = true)
    String phoneNumber;

//    Role cua tai khoan
    @Column(name = "role", nullable = false)
    String role;

//    Thoi gian tao tai khoan
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

//    Trang thai cua tai khoan
    @Column(name = "status", nullable = false)
    String status;

//     Profile cua tai khoan
    @OneToOne(mappedBy = "account")
    @JsonBackReference
    private Profile profile;

//    Danh sach token cua tai khoan
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<RefreshToken> refreshTokens;

//    Danh sach khoa hoc ma hoc vien da dky
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonManagedReference
    @OrderBy("enrolledAt DESC")
    private Set<CourseEnrollment> courseEnrollments;


//    Danh sach tien trinh cua tai khoan
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<LessonProgress> lessonProgresses;

//    Danh sach bai da nop cua tai khoan
    @OneToMany(mappedBy = "accountSubmitter", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Submission> submissions;

//    Danh sach bai ma tai khoann da cham diem
    @OneToMany(mappedBy = "accountGrader", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Submission> gradedSubmissions;

//    Comment cua tai khoan
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Comment> comments ;
}
