package com.course.course_be.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    /*
     * Nhan access token cua FB
     * */
    @NotBlank(message = "NOT_BLANK")
    String accessToken;
}
