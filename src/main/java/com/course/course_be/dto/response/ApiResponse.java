package com.course.course_be.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// An nhung bien co  gi tri la null di
@JsonInclude(JsonInclude.Include.NON_NULL)
// Dinh nghia 1 kieu tra ve cho FE
public class ApiResponse <T>{
//    status cua response
    @Builder.Default
    Integer status = HttpStatus.OK.value() ;

//    Message cua response
    @Builder.Default
    String message = HttpStatus.OK.getReasonPhrase();

//    Du lieu tra ve
    T result;

//    Loi tra ve
    List<ErrorResponse> errors;

//    Thoi gian
    @Builder.Default
    long timestamp = System.currentTimeMillis();

//    Tong trang
    Integer totalPages;

//    Tong phan tu
    Integer totalItems;

//    Page hien tai
    Integer currentPage;
}
