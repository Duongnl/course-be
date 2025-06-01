package com.course.course_be.mapper;

import com.course.course_be.dto.response.homeclient.CourseSearchingResponse;
import com.course.course_be.dto.response.homeclient.CourseStudyingResponse;
import com.course.course_be.entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    public CourseStudyingResponse toCourseStudyingResponse (Course course);

    public CourseSearchingResponse toCourseSearchingResponse (Course course);

}
