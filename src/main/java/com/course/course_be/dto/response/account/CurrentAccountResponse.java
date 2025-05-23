package com.course.course_be.dto.response.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrentAccountResponse {

    String name;
    String email;
    String phone;
    String role;
    String avatarUrl;


}
