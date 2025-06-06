package com.course.course_be.mapper;

import com.course.course_be.dto.response.course.CourseEnrollmentAdminResponse;
import com.course.course_be.entity.CourseEnrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseEnrollmentMapper {
    @Mapping(source="account.id", target ="accountId")
    @Mapping(source="account.profile.name", target ="accountName")
    @Mapping(source="course.id", target ="courseId")
    @Mapping(source="course.name", target ="courseName")
    public CourseEnrollmentAdminResponse toCourseEnrollmentAdminResponse(CourseEnrollment courseEnrollment);

//    @Mapping(source="account.id", target ="accountId")
//    @Mapping(source="account.profile.name", target ="accountName")
//    @Mapping(source="account.profile.avatarUrl", target ="authorAvatar")
//    public CourseEnrollmentResponse toCourseEnrollmentResponse(CourseEnrollment photo);
}
