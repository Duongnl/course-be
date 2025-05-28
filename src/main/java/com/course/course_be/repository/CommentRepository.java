package com.course.course_be.repository;

import com.course.course_be.entity.Comment;
import com.course.course_be.entity.RefreshToken;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
        // Tìm theo cả 3 trường, status không phải 'deleted' (nếu cần)
    List<Comment> findByAccount_Profile_NameContainingIgnoreCaseAndContentContainingIgnoreCaseAndLesson_NameContainingIgnoreCaseAndStatusNot(
            String accountName, String content, String lessonName, String status, Pageable pageable);
}
