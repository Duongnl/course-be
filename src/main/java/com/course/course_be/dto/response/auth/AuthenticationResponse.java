package com.course.course_be.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse {
    /*
     * Class nay tra ve thong tin luc login
     * Muc dich de nguoi dung biet co dang nhap thanh cong hay khong
     * */

    String accessToken;
    String refreshToken;
    boolean authenticated;
}
