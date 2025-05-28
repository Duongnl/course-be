package com.course.course_be.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {
    public static Pageable createPageable(Integer page, Integer size, String sort) {
        if (page == null || size == null) {
            return Pageable.unpaged();
        }
        Sort s = Sort.by(Sort.Direction.DESC, "createdAt");

        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size,s);
        }
        String[] sortParams = sort.split("\\.");
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        return PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
    }
}
