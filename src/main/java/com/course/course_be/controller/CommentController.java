package com.course.course_be.controller;

import com.course.course_be.dto.request.comment.ChangeStatusRequest;
import com.course.course_be.dto.request.comment.CommentFilterRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.comment.CommentResponse;
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
    public ApiResponse<List<CommentResponse>> getComments(@ModelAttribute CommentFilterRequest commentFilterRequest) {
        Page<CommentResponse> commentPage = commentService.getListComment(commentFilterRequest);
        return ApiResponse.<List<CommentResponse>>builder()
                .result(commentPage.getContent())
                .totalItems(commentPage.getTotalElements())
                .currentPage(commentPage.getNumber())
                .totalPages(commentPage.getTotalPages())
                .build();
    }

    @PutMapping("/change-status-admin")
    public ApiResponse<CommentResponse> changeStatus(@RequestBody ChangeStatusRequest changeStatusRequest) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentService.changeStatus(changeStatusRequest))
                .build();
    }
//    @DeleteMapping("/")

}
