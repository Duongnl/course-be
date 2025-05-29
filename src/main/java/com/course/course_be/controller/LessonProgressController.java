package com.course.course_be.controller;

import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.entity.LessonProgress;
import com.course.course_be.service.LessonProgressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lesson-progress")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonProgressController {
    LessonProgressService lessonProgressService;

    @PostMapping("/viewed/{id}")
    public ApiResponse<?> viewedLessonProgress (@PathVariable("id") String id) {
        lessonProgressService.viewedLessonProgress(id);
        return  ApiResponse.builder()
                .build();
    }

}
