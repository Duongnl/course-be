package com.course.course_be.mapper;

import com.course.course_be.dto.response.course.CourseResponse;
import com.course.course_be.dto.response.courseclient.ChapterClientResponse;
import com.course.course_be.dto.response.courseclient.CourseClientResponse;
import com.course.course_be.dto.response.homeclient.CourseCardResponse;
import com.course.course_be.dto.response.homeclient.CourseEnrollCardResponse;
import com.course.course_be.dto.response.lessonclient.CourseInfoResponse;
import com.course.course_be.entity.Course;
import com.course.course_be.entity.Lesson;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    public CourseEnrollCardResponse toCourseStudyingResponse (Course course);
    public CourseInfoResponse toCourseInfoResponse(Course course);
    public CourseCardResponse toCourseCardResponse(Course course);
    public CourseResponse toCourseResponse(Course course);
    CourseClientResponse toCourseClientResponse(Course course);

}
