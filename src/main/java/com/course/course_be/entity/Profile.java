package com.course.course_be.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "profile")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
// Bang quan ly profile cua hoc vien
public class Profile {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

//  Ten cua hoc vien
    @Column(name = "name", nullable = false)
    String 	name;

//    Link Anh cua hoc vien
    @Column(name = "avatar_url", nullable = true, length = 500)
    String avatarUrl;

//    Chi tiet
    @Column(name = "detail" ,columnDefinition = "TEXT", nullable = true)
    String detail;

//    Ngay sinh cua hoc vien
    @Column(name = "birthday", columnDefinition = "date", nullable = true)
    LocalDate birthday;

//    Gioi tinh cua hoc vien
    @Column(name = "sex", nullable = true)
    String sex;

//    Tai khoan cua profile
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    @JsonManagedReference
    private Account account;

}
