package com.course.course_be.mapper;

import com.course.course_be.dto.response.lesson_progress.LessonProgressResponse;
import com.course.course_be.entity.Lesson;
import com.course.course_be.entity.LessonProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonProgressMapper {
    @Mapping(source = "lesson.id", target = "lessonId")
    @Mapping(source = "account.id", target = "accountId")
    LessonProgressResponse toLessonProgressResponse(LessonProgress lessonProgress);
}
