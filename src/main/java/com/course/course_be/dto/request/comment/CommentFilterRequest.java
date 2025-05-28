package com.course.course_be.dto.request.comment;

import lombok.Data;

@Data
public class CommentFilterRequest {
    private String keywordAccountName;
    private String keywordContent;
    private String keywordLessonName;
    private int pageIndex = 0;
    private int pageSize = 10;
    private String sort = "createdAt";
    private String order = "desc";
}