package com.course.course_be.dto.response.profile;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

    String avatarUrl;
    String email;
    String name;
    String phoneNumber;
    LocalDate birthday;
    String detail;
    String sex;
}
