package com.course.course_be.repository;

import com.course.course_be.entity.Comment;
import com.course.course_be.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Integer> {
}
