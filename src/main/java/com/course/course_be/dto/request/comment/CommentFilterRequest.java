package com.course.course_be.dto.request.comment;

import lombok.Data;

@Data
public class CommentFilterRequest {
    private String keywordAuthorName;
    private String keywordContent;
    private String keywordLessonName;
    private int pageIndex = 0;
    private int pageSize = 10;
    private String sort = "createdAt";
    private String order = "desc";

    private static final int MAX_PAGE_SIZE = 100;

    public void setPageSize(int pageSize) {
        this.pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
    }

    public void setSort(String sort) {
        if ("createdAt".equals(sort) || "authorName".equals(sort) || "lessonName".equals(sort) || "content".equals(sort)) {
            if ("authorName".equals(sort))
                this.sort = "accountProfileName";
            else
                this.sort = sort;
        } else {
            this.sort = "createdAt"; // fallback về mặc định
        }
    }

    public void setOrder(String order) {
        if (!"asc".equals(order)) {
            this.order = "desc";
        }
        else this.order = "asc";
    }
}
