package com.course.course_be.mapper;

import com.course.course_be.dto.response.lessonclient.LessonClientDetailResponse;
import com.course.course_be.dto.response.lessonclient.LessonSidebarResponse;
import com.course.course_be.entity.Lesson;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    LessonSidebarResponse toLessonSidebarResponse(Lesson lesson);
    public LessonClientDetailResponse toLessonClientDetailResponse(Lesson lesson);
}
