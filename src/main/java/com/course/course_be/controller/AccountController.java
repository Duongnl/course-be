package com.course.course_be.controller;

import com.course.course_be.dto.request.account.CreateAccountRequest;
import com.course.course_be.dto.request.account.UpdateAccountRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.account.AccountResponse;
import com.course.course_be.dto.response.account.CurrentAccountResponse;
import com.course.course_be.dto.response.account.ResultPaginationDTO;
import com.course.course_be.entity.Account;
import com.course.course_be.service.AccountService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

   @PostMapping
   public ApiResponse<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
      return ApiResponse.<AccountResponse>builder()
              .result(accountService.createNew(request))
              .build();
   }

   @GetMapping("/{id}")
   public ApiResponse<AccountResponse> getAccountById(@PathVariable String id) {
      return ApiResponse.<AccountResponse>builder()
              .result(accountService.findById(id))
              .build();
   }

   @GetMapping("")
   public ApiResponse<List<AccountResponse>> filterAccounts(
           @RequestParam(required = false) Integer page,
           @RequestParam(required = false) Integer perPage,
           @RequestParam(required = false) String name,
           @RequestParam(required = false) String email,
           @RequestParam(required = false) String sex,
           @RequestParam(required = false) String role,
           @RequestParam(required = false) String status
   ) {
      Page<AccountResponse> accountPage = accountService.filterAccounts(
              page, perPage, name, email, sex, role, status
      );
      return ApiResponse.<List<AccountResponse>>builder()
              .result(accountPage.stream().toList())
              .totalPages(accountPage.getTotalPages())
              .build();
   }

   @PutMapping("/{id}")
   public ApiResponse<AccountResponse> updateAccount(@PathVariable String id,
                                                     @Valid @RequestBody UpdateAccountRequest request) {
      return ApiResponse.<AccountResponse>builder()
              .result(accountService.update(id, request))
              .build();
   }

   @DeleteMapping("/{id}")
   public ApiResponse<String> deleteAccount(@PathVariable String id) {
      accountService.delete(id);
      return ApiResponse.<String>builder()
              .message("Account deleted successfully")
              .build();
   }


}
