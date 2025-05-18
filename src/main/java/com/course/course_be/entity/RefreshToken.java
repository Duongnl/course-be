package com.course.course_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "refresh_token")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
// Bang quan ly refresh token
public class RefreshToken {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

//    Luu refresh_token
    @Column(name = "refresh_token", nullable = false,  length = 500)
    String refreshToken;

//    Ngay het han refresh token
    @Column(name = "expire_date", columnDefinition = "date", nullable = false)
    LocalDate expireDate;

//    Chu tai khoan cua token nay
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Account account;
}
