package com.course.course_be.controller;

import com.course.course_be.dto.request.comment.ChangeStatusRequest;
import com.course.course_be.dto.request.comment.CommentCreateRequest;
import com.course.course_be.dto.request.comment.CommentFilterRequest;
import com.course.course_be.dto.request.comment.CommentAdminFilterRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.comment.CommentResponse;
import com.course.course_be.dto.response.comment.CommentAdminResponse;
import com.course.course_be.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @GetMapping("/list-admin")
    public ApiResponse<List<CommentAdminResponse>> getCommentsAdmin(@ModelAttribute CommentAdminFilterRequest commentAdminFilterRequest) {
        Page<CommentAdminResponse> commentPage = commentService.getListComment(commentAdminFilterRequest);
        return ApiResponse.<List<CommentAdminResponse>>builder()
                .result(commentPage.getContent())
                .totalItems(commentPage.getTotalElements())
                .currentPage(commentPage.getNumber())
                .totalPages(commentPage.getTotalPages())
                .build();
    }

    @GetMapping("/lesson/{lessonId}")
    public ApiResponse<List<CommentResponse>> getCommentsByLesson(
            @PathVariable String lessonId,
            @ModelAttribute CommentFilterRequest filterRequest
    ) {
        Page<CommentResponse> page = commentService.getCommentsByLesson(filterRequest,lessonId);

        return ApiResponse.<List<CommentResponse>>builder()
                .result(page.getContent())
                .totalItems(page.getTotalElements())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .build();
    }

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<CommentResponse>> getCommentsByCourse(
            @PathVariable String courseId,
            @ModelAttribute CommentFilterRequest commentFilterRequest
    ) {
        // dùng id để filter comment theo bài học, hoặc lesson chẳng hạn
        Page<CommentResponse> commentPage = commentService.getCommentsByCourse(commentFilterRequest,courseId);
        return ApiResponse.<List<CommentResponse>>builder()
                .result(commentPage.getContent())
                .totalItems(commentPage.getTotalElements())
                .currentPage(commentPage.getNumber())
                .totalPages(commentPage.getTotalPages())
                .build();
    }

    @GetMapping("/reply/{commentId}")
    public ApiResponse<List<CommentResponse>> getCommentReply(
            @PathVariable String commentId,
            @ModelAttribute CommentFilterRequest commentFilterRequest
    ) {
        Page<CommentResponse> commentPage = commentService.getCommentReply(commentFilterRequest,commentId);
        return ApiResponse.<List<CommentResponse>>builder()
                .result(commentPage.getContent())
                .totalItems(commentPage.getTotalElements())
                .currentPage(commentPage.getNumber())
                .totalPages(commentPage.getTotalPages())
                .build();
    }


    @PutMapping("/change-status-admin")
    public ApiResponse<CommentAdminResponse> changeStatus(@RequestBody ChangeStatusRequest changeStatusRequest) {
        return ApiResponse.<CommentAdminResponse>builder()
                .result(commentService.changeStatus(changeStatusRequest))
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<CommentResponse> createComment(@RequestBody CommentCreateRequest request){
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.createComment(request))
                .build();
    }


}
