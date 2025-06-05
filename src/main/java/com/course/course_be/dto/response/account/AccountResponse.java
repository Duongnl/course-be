package com.course.course_be.dto.response.account;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private String id;
    private String name;
    private String username;
    private String email;
    private String role;
    private String status;
    private String sex;
    private String phone;
    private String password;
    private String avatarUrl;
    private String birthday;
    private String createdAt;
}
