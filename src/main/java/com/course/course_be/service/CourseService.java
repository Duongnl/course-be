package com.course.course_be.service;

import com.course.course_be.dto.request.course.CreateCourseRequest;
import com.course.course_be.dto.request.course.UpdateCourseRequest;
import com.course.course_be.dto.response.course.CourseResponse;
import com.course.course_be.dto.response.homeclient.CourseCardResponse;
import com.course.course_be.entity.Category;
import com.course.course_be.entity.Course;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.CategoryErrorCode;
import com.course.course_be.exception.CourseErrorCode;
import com.course.course_be.mapper.CourseMapper;
import com.course.course_be.repository.CategoryRepository;
import com.course.course_be.repository.CourseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final CategoryRepository categoryRepository;


    public Page<CourseResponse> getAll(String name, String detail, String status, Pageable pageable) {
        name = name == null ? "" : name;
        Page<Course> page;
        if (status == null || status.isEmpty()) {
            page = (detail != null && !detail.isEmpty()) ? courseRepository.findByNameContainingAndDetailContainingAndStatusNot(name, detail, "deleted", pageable)
                    : courseRepository.findByNameContainingAndStatusNot(name, "deleted", pageable);
        } else {
            List<String> statusList = Arrays.asList(status.split("\\."));
            page = (detail != null && !detail.isEmpty()) ? courseRepository.findByNameContainingAndDetailContainingAndStatusIn(name, detail, statusList, pageable)
                    : courseRepository.findByNameContainingAndStatusIn(name, statusList, pageable);
        }
        return page.map(courseMapper::toCourseResponse);
    }

    public Page<CourseCardResponse> searchCoursesByName(String keyword, Pageable pageable) {
        Page<Course> coursesPage = courseRepository.findByNameContainingIgnoreCase(keyword, pageable);
        return coursesPage.map(courseMapper::toCourseCardResponse);
    }

    public Page<CourseCardResponse> filterCourse(String sort, String category, String keyword, int page, int size) {

        Page<Course> PageCourse = null;
        Pageable pageable = PageRequest.of(page, size);
        if (keyword == null) {
            keyword = "";

        }
        if (sort != null && sort.equals("hot")) {
            PageCourse = courseRepository.searchByKeywordAndCategoryOrderByHot(keyword, category, pageable);
        } else if (sort != null && sort.equals("newest")) {
            PageCourse = courseRepository.searchByKeywordAndCategoryOrderByNewest(keyword, category, pageable);
        } else {
            PageCourse = courseRepository.searchByKeywordAndCategory(keyword, category, pageable);
        }


        return PageCourse.map(courseMapper::toCourseCardResponse);
    }


    public List<CourseCardResponse> getHotCourse(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<CourseCardResponse> coursesHot = new ArrayList<>();
        List<Course> ListCourse = new ArrayList<Course>();
        ListCourse = courseRepository.getHotCourse(pageable);
        for (Course course : ListCourse) {
            coursesHot.add(courseMapper.toCourseCardResponse(course));
        }
        return coursesHot;
    }

    public List<CourseCardResponse> getNewestCourse(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<CourseCardResponse> coursesNewest = new ArrayList<>();
        List<Course> ListCourse = new ArrayList<Course>();
        ListCourse = courseRepository.getNewestCourse(pageable);
        for (Course course : ListCourse) {
            coursesNewest.add(courseMapper.toCourseCardResponse(course));
        }
        return coursesNewest;

    }


    public CourseResponse createNew(CreateCourseRequest request){
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new AppException(CategoryErrorCode.CATEGORY_NOT_FOUND));
        Course course = Course.builder()
                .name(request.getName())
                .detail(request.getDetail())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .videoUrl(request.getVideoUrl())
                .status("active")
                .createdAt(LocalDateTime.now())
                .category(category)
                .build();
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toCourseResponse(savedCourse);
    }

    public CourseResponse update(String id, UpdateCourseRequest request){
        Course course = courseRepository.findById(id).orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new AppException(CategoryErrorCode.CATEGORY_NOT_FOUND));
        course.setName(request.getName());
        course.setDetail(request.getDetail());
        course.setPrice(request.getPrice());
        course.setImageUrl(request.getImageUrl());
        course.setVideoUrl(request.getVideoUrl());
        course.setStatus(request.getStatus());
        course.setCategory(category);
        course.setUpdatedAt(LocalDateTime.now());
        return courseMapper.toCourseResponse(courseRepository.save(course));
    }

    public void delete(String id){
        Course course = courseRepository.findById(id).orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
        course.setStatus("deleted");
        courseRepository.save(course);
    }
}
