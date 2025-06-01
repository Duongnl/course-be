package com.course.course_be.controller;

import com.course.course_be.dto.request.category.CreateCategoryRequest;
import com.course.course_be.dto.request.category.UpdateCategoryRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.category.CategoryResponse;
import com.course.course_be.dto.response.homeclient.CourseSearchingResponse;
import com.course.course_be.service.CategoryService;
import com.course.course_be.utils.PageableUtil;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
        if (page != null) {
            int pageIndex = (page > 0) ? page - 1 : 0;
            int pageSize = (perPage!= null && perPage > 0) ? perPage : 10;
            Pageable pageable = PageableUtil.createPageable(pageIndex, pageSize, sort); // page - 1 vì Pageable index bắt đầu 0
            Page<CategoryResponse> pageResult = categoryService.getAllPaging(name, detail, status, pageable);
            return ApiResponse.<List<CategoryResponse>>builder().
                    result(pageResult.stream().toList())
                    .currentPage(page)
                    .totalPages(pageResult.getTotalPages())
                    .totalItems(pageResult.getTotalElements()).build();
        }
        return ApiResponse.<List<CategoryResponse>>builder().result(
                categoryService.getAll(name, detail, status, sort)
        ).build();
    }

    @GetMapping ("/search/{id}")
    public ApiResponse<List<CourseSearchingResponse>> getCourseByCategory (@PathVariable String id , @RequestParam int page , @RequestParam int size ) {
        return ApiResponse.<List<CourseSearchingResponse>>builder().result(categoryService.getCourseByCategory(id,page,size)).build();
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
