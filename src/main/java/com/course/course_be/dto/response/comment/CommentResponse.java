package com.course.course_be.dto.response.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    String id;
    String authorId;
    String authorName;
    String authorAvatar;
    String content;
    Integer replyCount;
    String createdAt;
}
