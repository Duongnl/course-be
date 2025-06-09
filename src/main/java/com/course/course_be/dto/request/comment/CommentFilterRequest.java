package com.course.course_be.dto.request.comment;

import lombok.Data;


@Data
public class CommentFilterRequest {

    private int pageIndex = 0;
    private int pageSize = 10;
    private String sort = "createdAt"; // tên cột cần sắp xếp
    private String order = "desc";     // asc | desc

    public void setPageSize(int pageSize) {
        this.pageSize = Math.min(pageSize, 100); // giới hạn
    }
}
