package com.course.course_be.controller;

import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.statistic.CountResponse;
import com.course.course_be.dto.response.statistic.StatCourseEnrollResponse;
import com.course.course_be.service.StatisticService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistic")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticController {

    StatisticService statisticService;


    @GetMapping("/count")
    public ApiResponse<List<CountResponse>> count() {
        return ApiResponse.<List<CountResponse>>builder()
                .result(statisticService.count())
                .build();
    }

    @GetMapping("/most-enroll")
    public ApiResponse<List<StatCourseEnrollResponse>> findMostEnrolledCourses (@RequestParam(required = false) Integer page,
                                                                                @RequestParam(required = false) Integer perPage) {

        Page<StatCourseEnrollResponse> pageData = statisticService.findMostEnrolledCourses(page, perPage);
        return ApiResponse.<List<StatCourseEnrollResponse>>builder()
                .result(pageData.stream().toList())
                .totalPages(pageData.getTotalPages())
                .build();
    }


}
