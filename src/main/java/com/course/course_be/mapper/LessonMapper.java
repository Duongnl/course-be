package com.course.course_be.mapper;

import com.course.course_be.dto.response.courseclient.LessonClientResponse;
import com.course.course_be.dto.response.lesson.LessonResponse;
import com.course.course_be.dto.response.lessonclient.LessonClientDetailResponse;
import com.course.course_be.dto.response.lessonclient.LessonSidebarResponse;
import com.course.course_be.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    LessonSidebarResponse toLessonSidebarResponse(Lesson lesson);
    public LessonClientDetailResponse toLessonClientDetailResponse(Lesson lesson);
    @Mapping(source = "chapter.id", target = "chapterId")
    LessonResponse toLessonResponse(Lesson lesson);
}
