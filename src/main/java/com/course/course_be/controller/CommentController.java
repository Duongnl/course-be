package com.course.course_be.controller;

import com.course.course_be.dto.request.comment.CommentFilterRequest;
import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.dto.response.comment.CommentResponse;
import com.course.course_be.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @GetMapping("/admin/comment")
    public ApiResponse<List<CommentResponse>> getAllComments(@ModelAttribute CommentFilterRequest commentFilterRequest) {
        return commentService.getListComment(commentFilterRequest);
    }


}
