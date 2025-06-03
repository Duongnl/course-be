package com.course.course_be.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {
    public static Pageable createPageable(Integer page, Integer size, String sort) {
        Sort s = Sort.by(Sort.Direction.DESC, "createdAt");

        if (sort != null) {
            String[] sortParams = sort.split("\\.");
            Sort.Direction direction = Sort.Direction.ASC;
            if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
            s = Sort.by(direction, sortParams[0]);
        }

        if (page == null) {
            return Pageable.unpaged(s);
        }
        int pageSize = size !=  null ? size : 10;
        return PageRequest.of(page - 1, pageSize, s);
    }
}
