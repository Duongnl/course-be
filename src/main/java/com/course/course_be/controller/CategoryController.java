package com.course.course_be.controller;

import com.course.course_be.dto.request.category.CreateCategoryRequest;
import com.course.course_be.dto.request.category.UpdateCategoryRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.category.CategoryResponse;
import com.course.course_be.dto.response.homeclient.CourseCardResponse;
import com.course.course_be.service.CategoryService;
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
@RequestMapping("/category")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;


    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategory(@RequestParam(required = false) Integer page,
                                                              @RequestParam(required = false) Integer perPage,
                                                              @RequestParam(required = false) String sort,
                                                              @RequestParam(required = false) String name,
                                                              @RequestParam(required = false) String detail,
                                                              @RequestParam(required = false) String status) {

        Pageable pageable = PageableUtil.createPageable(page, perPage, sort);
        Page<CategoryResponse> pageResult = categoryService.getAll(name, detail, status, pageable);
        return ApiResponse.<List<CategoryResponse>>builder().
                result(pageResult.stream().toList())
                .currentPage(page != null ? page : 1)
                .totalPages(pageResult.getTotalPages())
                .totalItems(pageResult.getTotalElements()).build();
    }

    @GetMapping ("/search/{id}")
    public ApiResponse<List<CourseCardResponse>> getCourseByCategory (@PathVariable String id , @RequestParam int page , @RequestParam int size ) {
        return ApiResponse.<List<CourseCardResponse>>builder().result(categoryService.getCourseByCategory(id,page,size)).build();
    }

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return  ApiResponse.<CategoryResponse>builder().result(
                categoryService.createNew(request)
        ).build();
    }


    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable String id, @Valid @RequestBody UpdateCategoryRequest request) {
        return  ApiResponse.<CategoryResponse>builder().result(
                categoryService.update(id,request)
        ).build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable String id) {
        categoryService.delete(id);
        return ApiResponse.<String>builder()
                .message("Category deleted successfully")
                .build();
    }
}
