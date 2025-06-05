package com.course.course_be.service;

import com.course.course_be.dto.response.account.CourseProgressResponse;
import com.course.course_be.dto.response.account.MyCourseResponse;
import com.course.course_be.dto.request.course.CourseEnrollmentAdminFilterRequest;
import com.course.course_be.dto.response.course.CourseEnrollmentAdminResponse;
import com.course.course_be.dto.response.homeclient.CourseEnrollCardResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.CourseEnrollment;
import com.course.course_be.mapper.CourseEnrollmentMapper;
import com.course.course_be.mapper.CourseMapper;
import com.course.course_be.repository.CourseEnrollmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseEnrollmentService {

    AuthenticationService authenticationService;
    CourseEnrollmentMapper courseEnrollmentMapper;
    CourseMapper courseMapper;
    CourseEnrollmentRepository courseEnrollmentRepository;

    public List<CourseEnrollCardResponse> getCourseEnroll() {
        Account account = authenticationService.getMyAccountCurrent();

        List<CourseEnrollment> sortedEnrollments = new ArrayList<>(account.getCourseEnrollments());

        sortedEnrollments.sort((a, b) -> b.getEnrolledAt().compareTo(a.getEnrolledAt())); // mới nhất trước

        return sortedEnrollments.stream()
                .limit(4)
                .map(enrollment -> courseMapper.toCourseStudyingResponse(enrollment.getCourse()))
                .collect(Collectors.toList());
    }


    public Page<CourseProgressResponse> getCourseProgress ( String id,
                                                           Integer page,
                                                           Integer perPage,
                                                          String courseName,
                                                           String status) {

        page = page == null ? 0 : page - 1;
        perPage = perPage == null ? 10 : perPage;
        Pageable pageable = PageRequest.of(page, perPage);

        courseName = courseName == null ? "" : courseName;
        status = status == null ? null : (status.equals("completed.inProgress") || status.equals("inProgress.completed")  || status.isEmpty() ? null : status);
        System.out.println("course name >>> " + courseName);

        return courseEnrollmentRepository.findCourseProgress(id,courseName, status, pageable);
    }

    public Page<MyCourseResponse> getMyCourse (
                                                            Integer page,

                                                            String courseName,
                                                            String status) {

        var context = SecurityContextHolder.getContext();
        String Id = context.getAuthentication().getName();

        page = page == null ? 0 : page - 1;

        Pageable pageable = PageRequest.of(page, 6);

        courseName = courseName == null ? "" : courseName;
        status = status == null ? null : (status.equals("completed.inProgress") || status.equals("inProgress.completed")  || status.isEmpty() ? null : status);
        System.out.println("course name >>> " + courseName);

        return courseEnrollmentRepository.findMyCourse(Id, courseName, status, pageable);
    }

    public Page<CourseEnrollmentAdminResponse> getListCourseEnrollment(CourseEnrollmentAdminFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPageIndex(), request.getPageSize(),
                Sort.by(Sort.Direction.fromString(request.getOrder()), request.getSort()));

        String accountName = Optional.ofNullable(request.getAccountName()).orElse("");
        String courseName = Optional.ofNullable(request.getCourseName()).orElse("");

        Page<CourseEnrollment> courseEnrollmentPage = courseEnrollmentRepository
                .findByAccount_Profile_NameContainingIgnoreCaseAndCourse_NameContainingIgnoreCaseAndStatusNot(
                        accountName, courseName, "inactive", pageable);

        return courseEnrollmentPage.map(
                courseEnrollmentMapper::toCourseEnrollmentAdminResponse);
    }
}
