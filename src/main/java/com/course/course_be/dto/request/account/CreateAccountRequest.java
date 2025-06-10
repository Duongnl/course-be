package com.course.course_be.dto.request.account;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAccountRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Username is required")
    private String username;


    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Role is required")
    @Pattern(regexp = "ADMIN|CLIENT", message = "Role must be ADMIN or CLIENT")
    private String role;

    @NotNull(message = "Status is required")
    @Pattern(regexp = "active|inactive", message = "Status must be active or inactive")
    private String status;

    @NotNull(message = "Sex is required")
    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Sex must be MALE, FEMALE, or OTHER")
    private String sex;

    private String phone;

    private String avatarUrl;


    private String birthday;
}

