package com.course.course_be.mapper;


import com.course.course_be.dto.response.lessonclient.ChapterSidebarResponse;
import com.course.course_be.entity.Chapter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChapterMapper {
    public ChapterSidebarResponse toChapterSidebarResponse(Chapter chapter);
}
