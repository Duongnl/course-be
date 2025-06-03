package com.course.course_be.dto.request.comment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class CommentFilterRequest {
    String authorName;
    String content;
    String lessonName;
    int pageIndex = 0;

    @Setter(AccessLevel.NONE)
    int pageSize = 10;

    @Setter(AccessLevel.NONE)
    String sort = "createdAt";

    @Setter(AccessLevel.NONE)
    String order = "desc";

    static final int MAX_PAGE_SIZE = 100;

    public void setPageSize(int pageSize) {
        this.pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = Math.max(0, pageIndex); // Không cho phép pageIndex < 0
    }

    public void setSort(String sortParam) {
        if (sortParam == null || sortParam.isEmpty()) {
            this.sort = "createdAt";
            this.order = "desc";
            return;
        }

        String[] parts = sortParam.split("\\.");
        if (parts.length != 2) {
            this.sort = "createdAt";
            this.order = "desc";
            return;
        }

        String field = parts[0];
        String order = parts[1].toLowerCase();

        // Validate field
        if ("createdAt".equals(field) ||
                "authorName".equals(field) ||
                "lessonName".equals(field) ||
                "content".equals(field)) {

            // Convert alias if needed
            this.sort = "authorName".equals(field) ? "accountProfileName" : field;
        } else {
            this.sort = "createdAt"; // fallback
        }

        // Validate order
        if ("asc".equals(order) || "desc".equals(order)) {
            this.order = order;
        } else {
            this.order = "desc"; // fallback
        }
    }
}
