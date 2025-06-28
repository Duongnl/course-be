package com.course.course_be.service;

import com.course.course_be.dto.response.statistic.CountResponse;
import com.course.course_be.dto.response.statistic.StatCourseEnrollResponse;
import com.course.course_be.entity.CourseEnrollment;
import com.course.course_be.entity.Submission;
import com.course.course_be.repository.CourseEnrollmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticService {
    AccountService accountService;
    CourseService courseService;
    SubmissionService submissionService;
    CourseEnrollmentRepository courseEnrollmentRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public List<CountResponse> count () {
        List<CountResponse> list = new ArrayList<>();

//        user
        list.add(accountService.countAccount());
//        course
        list.add(courseService.countCourse());
//        enroll
        list.add(accountService.countAccountEnroll());
//        submission
        list.add(submissionService.countSubmitted());
        return list;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<StatCourseEnrollResponse> findMostEnrolledCourses (Integer page, Integer  perPage)  {
        page = page == null ? 0 : page - 1;
        perPage = perPage == null ? 5 : perPage;
        Pageable pageable = PageRequest.of(page, perPage);

        return courseEnrollmentRepository.findMostEnrolledCourses(pageable);
    }

}
