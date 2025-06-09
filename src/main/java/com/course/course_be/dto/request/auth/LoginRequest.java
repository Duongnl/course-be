package com.course.course_be.dto.request.auth;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

    @NotBlank(message = "NOT_BLANK")
    String username;

    @NotBlank(message = "NOT_BLANK")
    String password;
}
