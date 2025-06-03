package com.course.course_be.mapper;

import com.course.course_be.dto.response.homeclient.CourseCardResponse;
import com.course.course_be.dto.response.homeclient.CourseEnrollCardResponse;
import com.course.course_be.dto.response.lessonclient.CourseInfoResponse;
import com.course.course_be.entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    public CourseEnrollCardResponse toCourseStudyingResponse (Course course);
    public CourseInfoResponse toCourseInfoResponse(Course course);
    public CourseCardResponse toCourseSearchingResponse (Course course);

}
