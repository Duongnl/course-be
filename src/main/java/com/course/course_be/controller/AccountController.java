package com.course.course_be.controller;

import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.account.AccountResponse;
import com.course.course_be.dto.response.account.CurrentAccountResponse;
import com.course.course_be.dto.response.account.ResultPaginationDTO;
import com.course.course_be.entity.Account;
import com.course.course_be.service.AccountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

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

   @GetMapping("/all-account")
   public ApiResponse<ResultPaginationDTO> getAllAccount(@Filter Specification<Account> spec , Pageable pageable) {

      return ApiResponse.<ResultPaginationDTO>builder()
              .result(accountService.getAllAccounts(spec, pageable))
              .build();
   }


}
