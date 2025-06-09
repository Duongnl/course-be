package com.course.course_be.service;

import com.course.course_be.dto.request.comment.ChangeStatusRequest;
import com.course.course_be.dto.request.comment.CommentCreateRequest;
import com.course.course_be.dto.request.comment.CommentFilterRequest;
import com.course.course_be.dto.request.comment.CommentAdminFilterRequest;
import com.course.course_be.dto.response.comment.CommentAdminResponse;
import com.course.course_be.dto.response.comment.CommentResponse;
import com.course.course_be.entity.Comment;
import com.course.course_be.entity.Course;
import com.course.course_be.entity.Lesson;
import com.course.course_be.exception.AppException;
import com.course.course_be.mapper.CommentMapper;
import com.course.course_be.repository.CommentRepository;
import com.course.course_be.repository.CourseRepository;
import com.course.course_be.repository.LessonRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.course.course_be.exception.CommentErrorCode.COMMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;
    CourseRepository courseRepository;
    AuthenticationService authenticationService;
    CommentMapper commentMapper;
    private final LessonRepository lessonRepository;

    public Page<CommentAdminResponse> getListComment(CommentAdminFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPageIndex(), request.getPageSize(),
                Sort.by(Sort.Direction.fromString(request.getOrder()), request.getSort()));

        String accountName = Optional.ofNullable(request.getAuthorName()).orElse("");
        String content = Optional.ofNullable(request.getContent()).orElse("");
        String lessonName = Optional.ofNullable(request.getLessonName()).orElse("");

        Page<Comment> commentPage = commentRepository
                .findByAccount_Profile_NameContainingIgnoreCaseAndContentContainingIgnoreCaseAndLesson_NameContainingIgnoreCaseAndStatusNot(
                        accountName, content, lessonName, "inactive", pageable);

        return commentPage.map(comment -> {
            CommentAdminResponse response = commentMapper.toCommentAdminResponse(comment);
            response.setReplyCount(comment.getCommentReplies().size());
            return response;
        });
    }

    @Transactional
    public CommentAdminResponse changeStatus(ChangeStatusRequest request) {
        Comment comment = commentRepository
                .findById(request.getCommentId())
                .orElseThrow(() -> new AppException(COMMENT_NOT_FOUND));

        // Cập nhật status
        comment.setStatus(request.getStatus());

        return commentMapper.toCommentAdminResponse(comment);
    }

    public Page<CommentResponse> getCommentsByLesson(CommentFilterRequest request, String lessonIdReq) {
        Pageable pageable = PageRequest.of(request.getPageIndex(), request.getPageSize(),
                Sort.by(Sort.Direction.fromString(request.getOrder()), request.getSort()));

        String lessonId = Optional.ofNullable(lessonIdReq).orElse("");

        if (!lessonId.isEmpty()) {
            Page<Comment> commentPage = commentRepository
                    .findByLesson_IdAndParentCommentIsNullAndStatusNot(lessonId, "inactive", pageable);
            return commentPage.map(comment -> {
                CommentResponse response = commentMapper.toCommentResponse(comment);
                response.setReplyCount(comment.getCommentReplies().size());
                return response;
            });
        } else throw new AppException(COMMENT_NOT_FOUND);

    }

    public Page<CommentResponse> getCommentsByCourse(CommentFilterRequest request, String courseIdReq) {
        Pageable pageable = PageRequest.of(request.getPageIndex(), request.getPageSize(),
                Sort.by(Sort.Direction.fromString(request.getOrder()), request.getSort()));

        String courseId = Optional.ofNullable(courseIdReq).orElse("");

        if (!courseId.isEmpty()) {
            Page<Comment> commentPage = commentRepository
                    .findByCourse_IdAndParentCommentIsNullAndStatusNot(courseId, "inactive", pageable);
            return commentPage.map(comment -> {
                CommentResponse response = commentMapper.toCommentResponse(comment);
                response.setReplyCount(comment.getCommentReplies().size());
                return response;
            });
        } else throw new AppException(COMMENT_NOT_FOUND);

    }

    public Page<CommentResponse> getCommentReply(CommentFilterRequest request, String commentIdReq) {
        Pageable pageable = PageRequest.of(request.getPageIndex(), request.getPageSize(),
                Sort.by(Sort.Direction.fromString(request.getOrder()), request.getSort()));

        String commentId = Optional.ofNullable(commentIdReq).orElse("");

        if (!commentId.isEmpty()) {
            Page<Comment> commentPage = commentRepository
                    .findByParentComment_IdAndStatusNot(Integer.parseInt(commentId), "inactive", pageable);
            return commentPage.map(comment -> {
                CommentResponse response = commentMapper.toCommentResponse(comment);
                response.setReplyCount(comment.getCommentReplies().size());
                return response;
            });
        } else throw new AppException(COMMENT_NOT_FOUND);
    }

    @Transactional
    public CommentResponse createComment(CommentCreateRequest request) {
        Optional<Course> course = Optional.empty();
        if (request.getCourseId() != null) {
            course = courseRepository.findById(request.getCourseId());
        }
        Optional<Lesson> lesson = Optional.empty();
        if (request.getLessonId() != null) {
            lesson = lessonRepository.findById(request.getLessonId());
        }
        Optional<Comment> commentParent = commentRepository.findById(Integer.parseInt(request.getCommentParentId()));

        Comment comment = Comment.builder()
                .createdAt(LocalDateTime.now())
                .parentComment(commentParent.orElse(null))
                .status("active")
                .account(authenticationService.getMyAccountCurrent())
                .content(request.getContent())
                .course(course.orElse(null))
                .lesson(lesson.orElse(null))
                .build();
        commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }
}

