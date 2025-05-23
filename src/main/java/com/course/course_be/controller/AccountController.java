package com.course.course_be.controller;

import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.account.CurrentAccountResponse;
import com.course.course_be.service.AccountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {
   AccountService accountService;

   @GetMapping("/current-account")
   public ApiResponse<CurrentAccountResponse> getCurrentAccount() {
      return ApiResponse.<CurrentAccountResponse>builder()
              .result(accountService.getCurrentAccount())
              .build();
   }

}
