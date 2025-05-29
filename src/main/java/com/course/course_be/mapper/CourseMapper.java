package com.course.course_be.mapper;

import com.course.course_be.dto.response.lessonclient.CourseInfoResponse;
import com.course.course_be.entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    public CourseInfoResponse toCourseInfoResponse(Course course);
}
