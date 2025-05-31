package com.course.course_be.service;

import java.util.List;
import java.util.Optional;

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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;

    CommentMapper commentMapper;

    public ApiResponse<List<CommentResponse>> getListComment(CommentFilterRequest filter) {
        ApiResponse<List<CommentResponse>> apiResponse = new ApiResponse<>();

        // loai tru status khong can thiet
        String statusNot = "inactive";

        // Cau hinh phan trang va sap xep
        Pageable pageable = PageRequest.of(filter.getPageIndex(), filter.getPageSize(),
                Sort.by(Sort.Direction.fromString(filter.getOrder()), filter.getSort()));

        // lay gia tri filter
        String accountName = Optional.ofNullable(filter.getKeywordAuthorName()).orElse("");
        String content = Optional.ofNullable(filter.getKeywordContent()).orElse("");
        String lessonName = Optional.ofNullable(filter.getKeywordLessonName()).orElse("");

        // Tối ưu hóa: trả về Page<Comment> co san trong Spring Data JPA lay content va totalPages,totalElements
        Page<Comment> commentPage = commentRepository                .findByAccount_Profile_NameContainingIgnoreCaseAndContentContainingIgnoreCaseAndLesson_NameContainingIgnoreCaseAndStatusNot(
                        accountName, content, lessonName, statusNot, pageable
                );

        // Chuyển thành List<CommentResponse>
        List<CommentResponse> commentResponses = commentPage.getContent().stream().map(comment -> {
            CommentResponse response = commentMapper.toCommentResponse(comment);
            response.setReplyCount(comment.getCommentReplies().size());
            return response;
        }).toList();

        // ✅ Không cần tự đếm nữa
        apiResponse.setResult(commentResponses);
        apiResponse.setCurrentPage(filter.getPageIndex());
        apiResponse.setTotalPages(commentPage.getTotalPages());
        apiResponse.setTotalItems(commentPage.getTotalElements());

        return apiResponse;
    }


}
