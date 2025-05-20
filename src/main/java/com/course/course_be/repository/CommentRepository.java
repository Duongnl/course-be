package com.course.course_be.repository;

import com.course.course_be.entity.Comment;
import com.course.course_be.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
