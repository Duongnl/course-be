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
    private String email;
    private String username;
    private LocalDateTime createdAt;
    private String status;
    private String role;
    private String sex;
    private LocalDate birthday;
}
