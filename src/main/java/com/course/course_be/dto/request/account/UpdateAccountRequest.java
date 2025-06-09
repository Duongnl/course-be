package com.course.course_be.dto.request.account;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAccountRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Role is required")
    @Pattern(regexp = "ADMIN|CLIENT", message = "Role must be ADMIN or CLIENT")
    private String role;

    @NotNull(message = "Status is required")
    @Pattern(regexp = "active|inactive", message = "Status must be active or inactive")
    private String status;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Sex is required")
    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Sex must be MALE, FEMALE, or OTHER")
    private String sex;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Avatar URL is required")
    private String avatarUrl;

    @NotBlank(message = "Birthday is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Birthday must be in YYYY-MM-DD format")
    private String birthday;
}
