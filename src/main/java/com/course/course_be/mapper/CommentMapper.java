package com.course.course_be.mapper;

import com.course.course_be.dto.response.comment.CommentAdminResponse;
import com.course.course_be.dto.response.comment.CommentResponse;
import com.course.course_be.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source="account.id", target ="authorId")
    @Mapping(source="account.profile.name", target ="authorName")
    @Mapping(source="lesson.id", target ="lessonId")
    @Mapping(source="lesson.name", target ="lessonName")
    public CommentAdminResponse toCommentAdminResponse(Comment photo);

    @Mapping(source="account.id", target ="authorId")
    @Mapping(source="account.profile.name", target ="authorName")
    @Mapping(source="account.profile.avatarUrl", target ="authorAvatar")
    public CommentResponse toCommentResponse(Comment photo);

}
