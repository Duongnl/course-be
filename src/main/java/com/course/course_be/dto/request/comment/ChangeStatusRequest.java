package com.course.course_be.dto.request.comment;

import lombok.Data;

@Data
public class ChangeStatusRequest {
    Integer commentId;
    String status;
}
