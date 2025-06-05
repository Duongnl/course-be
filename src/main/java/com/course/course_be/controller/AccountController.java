package com.course.course_be.controller;

import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.account.AccountResponse;
import com.course.course_be.dto.response.account.CourseProgressResponse;
import com.course.course_be.dto.response.account.CurrentAccountResponse;
import com.course.course_be.dto.response.account.ResultPaginationDTO;
import com.course.course_be.entity.Account;
import com.course.course_be.service.AccountService;
import com.course.course_be.service.CourseEnrollmentService;
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
   CourseEnrollmentService courseEnrollmentService;

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

   @GetMapping("/course-progress/{id}")
   public ApiResponse<List<CourseProgressResponse>> getCourseProgress(@PathVariable String id,
                                                                      @RequestParam(required = false) Integer page,
                                                                      @RequestParam(required = false) Integer perPage,
                                                                      @RequestParam(required = false) String name,
                                                                      @RequestParam(required = false) String status

   ) {

      Page<CourseProgressResponse> courseProgressPage =   courseEnrollmentService.getCourseProgress(id,page, perPage, name, status);
      return ApiResponse.<List<CourseProgressResponse>>builder()
              .result(courseProgressPage.stream().toList())
              .totalPages(courseProgressPage.getTotalPages())
              .build();
   }



}
