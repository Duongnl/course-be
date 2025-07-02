package com.course.course_be.dto.request.course_enrollment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.Set;

@Data
public class CourseEnrollmentAdminFilterRequest {

    private String accountName;
    private String courseName;
    private String status;

    private int page = 0;

    @Setter(AccessLevel.NONE)
    private int perPage = 10;

    @Setter(AccessLevel.NONE)
    private String sort = "enrolledAt";

    @Setter(AccessLevel.NONE)
    private String order = "desc";

    private static final int MAX_PAGE_SIZE = 100;

    // Trường hợp muốn kiểm soát sort field an toàn
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "enrolledAt", "accountName", "courseName"
    );

    public void setPerPage(int perPage) {
        this.perPage = (perPage < 1) ? 10 : Math.min(perPage, MAX_PAGE_SIZE);
    }

    public void setPage(int page) {
        this.page = Math.max(0, page - 1); // UI gửi 1 thì coi như page 0
    }

    public void setSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            resetDefaultSort();
            return;
        }

        String[] parts = sortParam.split("\\.");
        if (parts.length != 2) {
            resetDefaultSort();
            return;
        }

        String field = parts[0];
        String sortOrder = parts[1].toLowerCase();

        // Validate field
        if (ALLOWED_SORT_FIELDS.contains(field)) {
            this.sort = "accountName".equals(field) ? "accountProfileName" : field;
        } else {
            resetDefaultSort();
            return;
        }

        // Validate order
        if ("asc".equals(sortOrder) || "desc".equals(sortOrder)) {
            this.order = sortOrder;
        } else {
            this.order = "desc";
        }
    }

    private void resetDefaultSort() {
        this.sort = "enrolledAt";
        this.order = "desc";
    }
}
