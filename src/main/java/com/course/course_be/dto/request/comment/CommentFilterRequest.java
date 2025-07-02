package com.course.course_be.dto.request.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentFilterRequest {

    private int page = 0;
    private int perPage = 10;
    private String sort = "createdAt"; // tên cột cần sắp xếp
    private String order = "desc";     // asc | desc

    private static final int MAX_PAGE_SIZE = 100;

    public void setPage(int page) {
        this.page = Math.max(0, page - 1); // không cho phép page < 0 (page gửi lên là 1-based)
    }

    public void setPerPage(int perPage) {
        if (perPage < 1) {
            this.perPage = 10;
        } else {
            this.perPage = Math.min(perPage, MAX_PAGE_SIZE);
        }
    }

    public void setOrder(String order) {
        if ("asc".equalsIgnoreCase(order) || "desc".equalsIgnoreCase(order)) {
            this.order = order.toLowerCase();
        } else {
            this.order = "desc";
        }
    }

    public void setSort(String sort) {
        if (sort == null || sort.isBlank()) {
            this.sort = "createdAt";
        } else {
            this.sort = sort;
        }
    }
}
