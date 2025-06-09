package com.course.course_be.dto.request.comment;

import lombok.Data;

@Data
public class CommentCreateRequest {
    private String courseId;
    private String lessonId;
    private String commentParentId;
    private String content;
}
