package com.course.course_be.repository;

import com.course.course_be.entity.Category;
import com.course.course_be.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
}
