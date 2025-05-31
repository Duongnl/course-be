package com.course.course_be.dto.request.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileUpdateRequest {
    @NotBlank(message = "NOT_BLANK")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    String name;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(regexp = "^[0-9]*$", message = "Phone number must contain only digits")
    String phoneNumber;

    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    String avatarUrl;

    String detail;

    LocalDate birthday;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)?$", message = "Sex must be MALE, FEMALE, or OTHER")
    String sex;
}
