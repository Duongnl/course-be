package com.course.course_be.mapper;


import com.course.course_be.dto.response.chapter.ChapterResponse;
import com.course.course_be.dto.response.lessonclient.ChapterSidebarResponse;
import com.course.course_be.entity.Chapter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChapterMapper {
     ChapterSidebarResponse toChapterSidebarResponse(Chapter chapter);
     @Mapping(source = "course.id", target = "courseId")
     ChapterResponse toChapterResponse(Chapter chapter);
}
