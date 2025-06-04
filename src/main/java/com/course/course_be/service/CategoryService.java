package com.course.course_be.service;

import com.course.course_be.dto.request.category.CreateCategoryRequest;
import com.course.course_be.dto.request.category.UpdateCategoryRequest;
import com.course.course_be.dto.response.category.CategoryResponse;
import com.course.course_be.dto.response.homeclient.CourseCardResponse;
import com.course.course_be.entity.Category;
import com.course.course_be.entity.Course;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.CategoryErrorCode;
import com.course.course_be.mapper.CategoryMapper;
import com.course.course_be.mapper.CourseMapper;
import com.course.course_be.repository.CategoryRepository;
import com.course.course_be.repository.CourseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    CourseRepository courseRepository;
    CourseMapper courseMapper;


    public List<CourseCardResponse> getCourseByCategory(String id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Tìm category
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        List<CourseCardResponse> results = new ArrayList<CourseCardResponse>();
        // Lấy danh sách khóa học theo category, chỉ lấy ACTIVE
        List<Course> coursePage = courseRepository.findByCategoryAndStatus(category, "active", pageable);
        // Convert sang response DTO
        for (Course course: coursePage)
        {
            results.add(courseMapper.toCourseCardResponse(course));
        }

        return  results;

    }


    public Page<CategoryResponse> getAll(String name, String detail, String status, Pageable pageable) {
        name = name == null ? "" : name;
        Page<Category> page;
        if (status == null || status.isEmpty()) {
            page = (detail != null && !detail.isEmpty()) ? categoryRepository.findByNameContainingAndDetailContainingAndStatusNot(name, detail, "deleted", pageable)
                    : categoryRepository.findByNameContainingAndStatusNot(name,"deleted",pageable);
        } else {
            List<String> statusList = Arrays.asList(status.split("\\."));
            page = (detail != null && !detail.isEmpty()) ? categoryRepository.findByNameContainingAndDetailContainingAndStatusIn(name, detail, statusList, pageable)
                    : categoryRepository.findByNameContainingAndStatusIn(name,statusList,pageable);
        }
        return page.map(categoryMapper::toCategoryResponse);
    }

    public CategoryResponse createNew(CreateCategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .detail(request.getDetail())
                .status("active")
                .createdAt(LocalDateTime.now())
                .build();
        Category saved = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(saved);
    }

    public CategoryResponse update(String id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new AppException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        category.setName(request.getName());
        category.setDetail(request.getDetail());
        category.setStatus(request.getStatus());

        Category saved = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(saved);
    }

    public void delete(String id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new AppException(CategoryErrorCode.CATEGORY_NOT_FOUND));
        category.setStatus("deleted");
        categoryRepository.save(category);
    }

}
