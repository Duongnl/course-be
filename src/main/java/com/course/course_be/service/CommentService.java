package com.course.course_be.service;

import com.course.course_be.dto.request.comment.CommentFilterRequest;
import com.course.course_be.dto.response.comment.CommentResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.Comment;
import com.course.course_be.mapper.CommentMapper;
import com.course.course_be.repository.CommentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    CommentRepository commentRepository;

    CommentMapper commentMapper;

    public List<CommentResponse> getListComment(CommentFilterRequest filter) {
        String statusNot ="deleted";
        Pageable pageable = PageRequest.of(filter.getPageIndex(), filter.getPageSize(),
                Sort.by(Sort.Direction.fromString(filter.getOrder()), filter.getSort()));
        String accountName = Optional.ofNullable(filter.getKeywordAccountName()).orElse("");
        String content = Optional.ofNullable(filter.getKeywordContent()).orElse("");
        String lessonName = Optional.ofNullable(filter.getKeywordLessonName()).orElse("");

        List<Comment> comments =commentRepository.findByAccount_Profile_NameContainingIgnoreCaseAndContentContainingIgnoreCaseAndLesson_NameContainingIgnoreCaseAndStatusNot(
                accountName, content, lessonName, statusNot, pageable);

        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : comments) {

            CommentResponse commentResponse = commentMapper.toCommentResponse(comment);
            commentResponse.setReplyCount(comment.getCommentReplies().size());
            commentResponses.add(commentResponse);
        }
        return commentResponses;
    }

}
