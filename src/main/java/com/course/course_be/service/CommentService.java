package com.course.course_be.service;

import com.course.course_be.dto.request.comment.*;
import com.course.course_be.dto.response.comment.*;
import com.course.course_be.entity.*;
import com.course.course_be.exception.*;
import com.course.course_be.mapper.CommentMapper;
import com.course.course_be.repository.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {

    CommentRepository commentRepository;
    CourseRepository courseRepository;
    LessonRepository lessonRepository;
    AuthenticationService authenticationService;
    CommentMapper commentMapper;

    /**
     * Lấy danh sách bình luận cho trang quản trị, có phân trang, tìm kiếm
     */
    public Page<CommentAdminResponse> getListComment(CommentAdminFilterRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getPerPage(),
                Sort.by(Sort.Direction.fromString(request.getOrder()), request.getSort())
        );

        Page<Comment> commentPage = commentRepository
                .findByAccount_Profile_NameContainingIgnoreCaseAndContentContainingIgnoreCaseAndLesson_NameContainingIgnoreCaseAndStatusNot(
                        Optional.ofNullable(request.getAuthorName()).orElse(""),
                        Optional.ofNullable(request.getContent()).orElse(""),
                        Optional.ofNullable(request.getLessonName()).orElse(""),
                        "inactive",
                        pageable
                );

        return commentPage.map(comment -> {
            CommentAdminResponse response = commentMapper.toCommentAdminResponse(comment);
            response.setReplyCount(getActiveReplyCount(comment));
            return response;
        });
    }

    /**
     * Thay đổi trạng thái của bình luận (active/inactive)
     */
    @Transactional
    public CommentAdminResponse changeStatus(ChangeStatusRequest request) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new AppException(CommentErrorCode.COMMENT_NOT_FOUND));

        comment.setStatus(request.getStatus());

        CommentAdminResponse response = commentMapper.toCommentAdminResponse(comment);
        response.setReplyCount(getActiveReplyCount(comment));
        return response;
    }

    /**
     * Lấy danh sách bình luận cha theo bài học (lesson), có phân trang
     */
    public Page<CommentResponse> getCommentsByLesson(CommentFilterRequest request, String lessonIdReq) {
        String lessonId = Optional.ofNullable(lessonIdReq)
                .orElseThrow(() -> new AppException(CommentErrorCode.COMMENT_NOT_FOUND));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(CommentErrorCode.COMMENT_NOT_FOUND));

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getPerPage(),
                Sort.by(Sort.Direction.fromString(request.getOrder()), request.getSort())
        );

        Page<Comment> commentPage = commentRepository
                .findByLessonAndParentCommentIsNullAndStatusNot(lesson, "inactive", pageable);

        return commentPage.map(comment -> {
            CommentResponse response = commentMapper.toCommentResponse(comment);
            response.setReplyCount(getActiveReplyCount(comment));
            return response;
        });
    }

    /**
     * Lấy danh sách phản hồi (reply) của một bình luận theo ID
     */
    public Page<CommentResponse> getCommentReply(CommentFilterRequest request, String commentIdReq) {
        Integer commentId = Integer.valueOf(
                Optional.ofNullable(commentIdReq)
                        .orElseThrow(() -> new AppException(CommentErrorCode.COMMENT_NOT_FOUND))
        );

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(CommentErrorCode.COMMENT_NOT_FOUND));

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getPerPage(),
                Sort.by(Sort.Direction.fromString(request.getOrder()), request.getSort())
        );

        Page<Comment> commentPage = commentRepository
                .findByParentCommentAndStatusNot(parentComment, "inactive", pageable);

        return commentPage.map(comment -> {
            CommentResponse response = commentMapper.toCommentResponse(comment);
            response.setReplyCount(getActiveReplyCount(comment));
            return response;
        });
    }

    /**
     * Tạo mới bình luận (có thể là bình luận cha hoặc phản hồi)
     */
    @Transactional
    public CommentResponse createComment(CommentCreateRequest request) {
        Optional<Course> course = Optional.ofNullable(request.getCourseId())
                .flatMap(courseRepository::findById);

        Optional<Lesson> lesson = Optional.ofNullable(request.getLessonId())
                .flatMap(lessonRepository::findById);

        // Nếu có comment cha thì kiểm tra và gán
        Comment parentComment = null;
        if (request.getCommentParentId() != null) {
            parentComment = commentRepository.findById(Integer.parseInt(request.getCommentParentId()))
                    .filter(c -> "active".equals(c.getStatus()))
                    .orElse(null);
        }

        Comment comment = Comment.builder()
                .createdAt(LocalDateTime.now())
                .parentComment(parentComment)
                .status("active")
                .account(authenticationService.getMyAccountCurrent())
                .content(request.getContent())
                .course(course.orElse(null))
                .lesson(lesson.orElse(null))
                .build();

        commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    /**
     * Lấy danh sách bình luận của khóa học theo cursor-based pagination
     */
    public Page<CommentResponse> getCommentPageOfCourse(
            CommentFilterRequest request,
            String courseId,
            LocalDateTime beforeCreatedAt
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));

        Pageable pageable = PageRequest.of(0, request.getPerPage(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Comment> commentPage = commentRepository
                .findCommentsByCourseWithCursor(courseId, beforeCreatedAt, "inactive", pageable);

        return commentPage.map(comment -> {
            CommentResponse response = commentMapper.toCommentResponse(comment);
            response.setReplyCount(getActiveReplyCount(comment));
            return response;
        });
    }

    /**
     * Tính số lượng phản hồi đang hoạt động cho một bình luận
     */
    private int getActiveReplyCount(Comment comment) {
        return (int) comment.getCommentReplies()
                .stream()
                .filter(reply -> !"inactive".equals(reply.getStatus()))
                .count();
    }
}
