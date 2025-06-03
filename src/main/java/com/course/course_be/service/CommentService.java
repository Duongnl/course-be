package com.course.course_be.service;

import java.util.List;
import java.util.Optional;

import com.course.course_be.dto.request.comment.ChangeStatusRequest;
import com.course.course_be.exception.AppException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.course.course_be.dto.request.comment.CommentFilterRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.comment.CommentResponse;
import com.course.course_be.entity.Comment;
import com.course.course_be.mapper.CommentMapper;
import com.course.course_be.repository.CommentRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;

import static com.course.course_be.exception.CommentErrorCode.COMMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;

    CommentMapper commentMapper;

    public Page<CommentResponse> getListComment(CommentFilterRequest request) {
        String statusNot = "inactive";
        Pageable pageable = PageRequest.of(request.getPageIndex(), request.getPageSize(),
                Sort.by(Sort.Direction.fromString(request.getOrder()), request.getSort()));

        String accountName = Optional.ofNullable(request.getAuthorName()).orElse("");
        String content = Optional.ofNullable(request.getContent()).orElse("");
        String lessonName = Optional.ofNullable(request.getLessonName()).orElse("");

        Page<Comment> commentPage = commentRepository
                .findByAccount_Profile_NameContainingIgnoreCaseAndContentContainingIgnoreCaseAndLesson_NameContainingIgnoreCaseAndStatusNot(
                        accountName, content, lessonName, statusNot, pageable);

        return commentPage.map(comment -> {
            CommentResponse response = commentMapper.toCommentResponse(comment);
            response.setReplyCount(comment.getCommentReplies().size());
            return response;
        });
    }

    @Transactional
    public CommentResponse changeStatus(ChangeStatusRequest request) {
        Comment comment = commentRepository
                .findById(request.getCommentId())
                .orElseThrow(() -> new AppException(COMMENT_NOT_FOUND));

        // Cập nhật status
        comment.setStatus(request.getStatus());

        return commentMapper.toCommentResponse(comment);
    }
}
