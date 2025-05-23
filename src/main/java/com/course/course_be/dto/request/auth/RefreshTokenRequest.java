package com.course.course_be.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenRequest {
    /*
    * Nhan request chua refresh token
    * */
    @NotBlank(message = "NOT_BLANK")
    String refreshToken;
    
}
