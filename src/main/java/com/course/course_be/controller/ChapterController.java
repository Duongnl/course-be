package com.course.course_be.controller;

import com.course.course_be.dto.request.chapter.CreateChapterRequest;
import com.course.course_be.dto.request.chapter.UpdateChapterRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.chapter.ChapterResponse;
import com.course.course_be.service.ChapterService;
import com.course.course_be.utils.PageableUtil;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chapter")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterController {
    final ChapterService chapterService;

    @GetMapping("{courseId}")
    public ApiResponse<List<ChapterResponse>> getAllChapters(@PathVariable String courseId, @RequestParam(required = false) Integer page,
                                                             @RequestParam(required = false) Integer perPage,
                                                             @RequestParam(required = false) String sort,
                                                             @RequestParam(required = false) String name,
                                                             @RequestParam(required = false) String detail,
                                                             @RequestParam(required = false) String status
    ) {
        Pageable pageable = PageableUtil.createPageable(page, perPage, sort);
        Page<ChapterResponse> pageResult = chapterService.getAll(name, detail, status, courseId, pageable);
        return ApiResponse.<List<ChapterResponse>>builder()
                .result(pageResult.stream().toList())
                .currentPage(page != null ? page : 1)
                .totalPages(pageResult.getTotalPages())
                .totalItems(pageResult.getTotalElements())
                .build();
    }

    @PostMapping
    public ApiResponse<ChapterResponse> createChapter(@Valid @RequestBody CreateChapterRequest request) {
        return ApiResponse.<ChapterResponse>builder().result(
                chapterService.createNew(request)
        ).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ChapterResponse> updateChapter(@PathVariable int id, @Valid @RequestBody UpdateChapterRequest request){
        return ApiResponse.<ChapterResponse>builder().result(chapterService.update(id,request)).build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteChapter(@PathVariable int id){
        chapterService.deleteChapter(id);
        return ApiResponse.<String>builder()
                .message("Chapter deleted successfully")
                .build();
    }
}
