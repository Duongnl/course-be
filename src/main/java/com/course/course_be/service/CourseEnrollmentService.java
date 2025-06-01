package com.course.course_be.service;

import com.course.course_be.dto.response.homeclient.CourseStudyingResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.Course;
import com.course.course_be.entity.CourseEnrollment;
import com.course.course_be.mapper.CourseMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseEnrollmentService {

    AuthenticationService authenticationService;
    CourseMapper courseMapper;

    public List<CourseStudyingResponse> getCourseEnroll() {
        Account account = authenticationService.getMyAccountCurrent();

        List<CourseEnrollment> sortedEnrollments = new ArrayList<>(account.getCourseEnrollments());

        sortedEnrollments.sort((a, b) -> b.getEnrolledAt().compareTo(a.getEnrolledAt())); // mới nhất trước

        return sortedEnrollments.stream()
                .limit(4)
                .map(enrollment -> courseMapper.toCourseStudyingResponse(enrollment.getCourse()))
                .collect(Collectors.toList());
    }

}
