package com.course.course_be.mapper;

import com.course.course_be.dto.response.account.CurrentAccountResponse;
import com.course.course_be.dto.response.submissionadmin.SubmissionAdminResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {

    @Mapping(source = "lesson.chapter.course.name", target="courseName")
    @Mapping(source = "lesson.name", target="lessonName")
    @Mapping(source = "accountSubmitter.email", target="submitterEmail")
    @Mapping(source = "accountSubmitter.profile.name", target="submitterName")
    @Mapping(source = "accountGrader.email", target="graderEmail")
    @Mapping(source = "accountGrader.profile.name", target="graderName")
    SubmissionAdminResponse toSubmissionAdminResponse(Submission submission);
}
