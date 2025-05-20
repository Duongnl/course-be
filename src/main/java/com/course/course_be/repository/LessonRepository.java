package com.course.course_be.repository;

import com.course.course_be.entity.Course;
import com.course.course_be.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, String> {
}
