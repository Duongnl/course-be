package com.course.course_be.service;

import com.course.course_be.dto.response.homeclient.CourseCardResponse;
import com.course.course_be.entity.Course;
import com.course.course_be.mapper.CourseMapper;
import com.course.course_be.repository.CourseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private  final CourseMapper courseMapper;



    public Page<CourseCardResponse> searchCoursesByName(String keyword, Pageable pageable) {
        Page<Course> coursesPage = courseRepository.findByNameContainingIgnoreCase(keyword, pageable);
        return coursesPage.map(courseMapper::toCourseSearchingResponse);
    }

    public List<CourseCardResponse> getHotCourse (int page , int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        List<CourseCardResponse> coursesHot = new ArrayList<>();
        List<Course> ListCourse = new ArrayList<Course>();
        ListCourse = courseRepository.getHotCourse(pageable);
        for (Course course : ListCourse )
        {
            coursesHot.add(courseMapper.toCourseSearchingResponse(course));
        }
        return coursesHot;
    }
    public List<CourseCardResponse> getNewestCourse (int page , int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        List<CourseCardResponse> coursesNewest = new ArrayList<>();
        List<Course> ListCourse = new ArrayList<Course>();
        ListCourse = courseRepository.getNewestCourse(pageable);
        for (Course course : ListCourse)
        {
            coursesNewest.add(courseMapper.toCourseSearchingResponse(course));
        }
        return coursesNewest;

    }

}
