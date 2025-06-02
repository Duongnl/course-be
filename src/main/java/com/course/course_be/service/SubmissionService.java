package com.course.course_be.service;

import com.course.course_be.dto.request.submissionclient.SubmissionClientRequest;
import com.course.course_be.dto.response.submissionadmin.SubmissionAdminResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.Lesson;
import com.course.course_be.entity.Submission;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.LessonErrorCode;
import com.course.course_be.exception.SubmissionErrorCode;
import com.course.course_be.mapper.SubmissionMapper;
import com.course.course_be.repository.CourseRepository;
import com.course.course_be.repository.LessonRepository;
import com.course.course_be.repository.SubmissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionService {

    CourseRepository courseRepository;
    SubmissionRepository submissionRepository;
    LessonRepository lessonRepository;
    AuthenticationService authenticationService;
    SubmissionMapper submissionMapper;


    public void submission(SubmissionClientRequest request) {
        Account account  = authenticationService.getMyAccountCurrent();

        Lesson lesson = lessonRepository.findAuthorizedLessonById(request.getLessonId(),account.getId())
                .orElseThrow(() -> new AppException(LessonErrorCode.LESSON_NOT_FOUND));

        if (lesson.getAssignmentUrl() == null) {
            throw  new AppException(SubmissionErrorCode.SUBMISSION_INVALID);
        }

        boolean submission = submissionRepository.existsByLessonIdAndAccountSubmitterId(request.getLessonId(),account.getId());
        if (submission) {
            throw  new AppException(SubmissionErrorCode.SUBMISSION_EXIST);
        }



        Submission submissionNew = new Submission();
        submissionNew.setLesson(lesson);
        submissionNew.setSubmissionUrl(request.getSubmissionUrl());
        submissionNew.setSubmittedAt(LocalDateTime.now());
        submissionNew.setStatus("submitted");
        submissionNew.setAccountSubmitter(account);
        try {

        submissionRepository.save(submissionNew);
        }catch (Exception e) {
            throw new AppException(SubmissionErrorCode.SAVE_SUBMISSION_FAIL);
        }

    }

    public Page<Submission> filterSubmission ( Integer page,
                                                            Integer perPage,
                                                            String courseName,
                                                            String lessonName,
                                                            String submitterName,
                                                            String submitterEmail,
                                                            String status,
                                                            String from,
                                                            String to
    ) {

        page = page - 1;
        perPage =  perPage  == null ? 10 : perPage;
        Pageable pageable = PageRequest.of(page, perPage);

        courseName = courseName == null ? "" : courseName;
        lessonName = lessonName == null ? "" : lessonName;
        submitterName = submitterName == null ? "" : submitterName;
        submitterEmail = submitterEmail == null ? "" : submitterEmail;
        status = status == null ? "" :
                (status.equals("submitted.graded") || status.equals("graded.submitted") ? "" : status);


        LocalDateTime localDateTimeFrom = null;
        LocalDateTime localDateTimeTo = null;
        boolean idFilterDay = true;
//        Truong hop ca 2 deu co du lieu
        if (from != null && to != null) {
            if (from.equals(to)) {
                LocalDate localDateFrom = LocalDate.parse(from);
                localDateTimeFrom = localDateFrom.atStartOfDay();
                localDateTimeTo = localDateFrom.atTime(LocalTime.MAX);
            } else {
                LocalDate localDateFrom = LocalDate.parse(from);
                localDateTimeFrom = localDateFrom.atStartOfDay();
                LocalDate localDateTo = LocalDate.parse(to);
                localDateTimeTo = localDateTo.atTime(LocalTime.MAX);
            }


        }
//        truong hop chi loc trong 1 ngay
        else if ((from != null && to == null)) {
            LocalDate localDateFrom = LocalDate.parse(from);
            localDateTimeFrom = localDateFrom.atStartOfDay();
            localDateTimeTo = localDateFrom.atTime(LocalTime.MAX); // 2024-06-02T00:00:00
        }

//        truong hop ca 2 deu khong co du lieu
        Page<Submission> submissionPage =  submissionRepository.filterSubmissions(
                courseName,
                lessonName,
                submitterEmail,
                submitterName,
                status,
                localDateTimeFrom,
                localDateTimeTo,
                pageable
        );;


//        System.out.println("page >>> " + page);
//        System.out.println("perPage >>> " + perPage);
//        System.out.println("courseName >>> " + courseName);
//        System.out.println("lessonName >>> " + lessonName);
//        System.out.println("submitterName >>> " + submitterName);
//        System.out.println("submitterEmail >>> " + submitterEmail);
//        System.out.println("status >>> " + status);
//        System.out.println("from >>> " + localDateTimeFrom);
//        System.out.println("to >>> " + localDateTimeTo);
//        System.out.println("-----------------------------------------------------------------");

        return submissionPage;
    }

    public List<SubmissionAdminResponse> convertFilterSubmission (Page<Submission> submissionPage) {
        List<SubmissionAdminResponse> responseList = new ArrayList<>();
        for (Submission submission : submissionPage.getContent()) {
            SubmissionAdminResponse res = submissionMapper.toSubmissionAdminResponse(submission);

            responseList.add(submissionMapper.toSubmissionAdminResponse(submission));
        }
        return responseList;
    }



}
