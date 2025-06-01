package com.course.course_be.service;

import com.course.course_be.dto.response.homeclient.CourseSearchingResponse;
import com.course.course_be.entity.Course;
import com.course.course_be.mapper.CourseMapper;
import com.course.course_be.repository.CourseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private  final CourseMapper courseMapper;


    public Page<CourseSearchingResponse> searchCoursesByName(String keyword, Pageable pageable) {
        Page<Course> coursesPage = courseRepository.findByNameContainingIgnoreCase(keyword, pageable);
        return coursesPage.map(courseMapper::toCourseSearchingResponse);
    }

}
