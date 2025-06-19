package com.course.course_be.service;

import com.course.course_be.dto.request.course_enrollment.RegisterCourseClient;
import com.course.course_be.dto.request.course_enrollment.CourseEnrollmentAdminCreateRequest;
import com.course.course_be.dto.response.account.CourseProgressResponse;
import com.course.course_be.dto.response.account.MyCourseResponse;
import com.course.course_be.dto.request.course_enrollment.CourseEnrollmentAdminFilterRequest;
import com.course.course_be.dto.response.course.CourseEnrollmentAdminResponse;
import com.course.course_be.dto.response.course_enrollment.CourseEnrollmentResponse;
import com.course.course_be.dto.response.homeclient.CourseEnrollCardResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.Course;
import com.course.course_be.entity.CourseEnrollment;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.AuthErrorCode;
import com.course.course_be.exception.CourseEnrollmentErrorCode;
import com.course.course_be.exception.CourseErrorCode;
import com.course.course_be.mapper.CourseEnrollmentMapper;
import com.course.course_be.mapper.CourseMapper;
import com.course.course_be.repository.AccountRepository;
import com.course.course_be.repository.CourseEnrollmentRepository;
import com.course.course_be.repository.CourseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final CourseRepository courseRepository;
    private final AccountRepository accountRepository;

    public List<CourseEnrollCardResponse> getCourseEnroll() {
        Account account = authenticationService.getMyAccountCurrent();

        List<CourseEnrollment> sortedEnrollments = new ArrayList<>(account.getCourseEnrollments());

        sortedEnrollments.sort((a, b) -> b.getEnrolledAt().compareTo(a.getEnrolledAt())); // mới nhất trước

        return sortedEnrollments.stream()
                .limit(4)
                .map(enrollment -> courseMapper.toCourseStudyingResponse(enrollment.getCourse()))
                .collect(Collectors.toList());
    }


    public Page<CourseProgressResponse> getCourseProgress(String id,
                                                          Integer page,
                                                          Integer perPage,
                                                          String courseName,
                                                          String status) {

        page = page == null ? 0 : page - 1;
        perPage = perPage == null ? 10 : perPage;
        Pageable pageable = PageRequest.of(page, perPage);

        courseName = courseName == null ? "" : courseName;
        status = status == null ? null : (status.equals("completed.inProgress") || status.equals("inProgress.completed") || status.isEmpty() ? null : status);
        System.out.println("course name >>> " + courseName);

        return courseEnrollmentRepository.findCourseProgress(id, courseName, status, pageable);
    }

    public Page<MyCourseResponse> getMyCourse(
            Integer page,
            Integer perPage,
            String courseName,
            String status) {

        var context = SecurityContextHolder.getContext();
        String Id = context.getAuthentication().getName();

        page = page == null ? 0 : page - 1;
        perPage = perPage == null ? 6 : perPage;
        Pageable pageable = PageRequest.of(page, perPage);

        courseName = courseName == null ? "" : courseName;
        if (courseName.isEmpty()) {
            pageable = PageRequest.of(page, perPage, Sort.by("enrolledAt").descending());
        }
        status = status == null ? null : (status.equals("completed.inProgress") || status.equals("inProgress.completed") || status.isEmpty() ? null : status);


        return courseEnrollmentRepository.findMyCourse(Id, courseName, status, pageable);
    }

    public Page<CourseEnrollmentAdminResponse> getListCourseEnrollment(CourseEnrollmentAdminFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPerPage(),
                Sort.by(Sort.Direction.fromString(request.getOrder()), request.getSort()));

        String accountName = Optional.ofNullable(request.getAccountName()).orElse("");
        String courseName = Optional.ofNullable(request.getCourseName()).orElse("");

        Page<CourseEnrollment> courseEnrollmentPage;

        String StatusString = request.getStatus();
        if (StatusString == null || StatusString.isEmpty()) {
            courseEnrollmentPage = courseEnrollmentRepository
                    .findByAccount_Profile_NameContainingIgnoreCaseAndCourse_NameContainingIgnoreCase(
                            accountName, courseName, pageable);
        } else {
            List<String> statusList = Arrays.asList(StatusString.split("\\."));
            courseEnrollmentPage = courseEnrollmentRepository.findByAccount_Profile_NameContainingIgnoreCaseAndCourse_NameContainingIgnoreCaseAndStatusIn(
                    accountName, courseName, statusList, pageable);
        }

        return courseEnrollmentPage.map(
                courseEnrollmentMapper::toCourseEnrollmentAdminResponse);
    }

    public CourseEnrollmentResponse getCourseEnrollmentRegister(String courseId) {
        var context = SecurityContextHolder.getContext();
        String accountId = context.getAuthentication().getName();
        if (accountId == null) {
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }
        courseRepository.findById(courseId).orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
        CourseEnrollment courseEnrollment = courseEnrollmentRepository.findByAccountIdAndCourseId(accountId, courseId)
                .orElse(null);
        return courseEnrollmentMapper.toCourseEnrollmentResponse(courseEnrollment);
    }

    public CourseEnrollmentResponse registerCourse(RegisterCourseClient registerCourseClient) {
        var context = SecurityContextHolder.getContext();
        String accountId = context.getAuthentication().getName();
        String courseId = registerCourseClient.getCourseId();
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AppException(AuthErrorCode.UNAUTHENTICATED));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
        System.out.println("course id >>> " + course);
        CourseEnrollment existCourseEnrollment = courseEnrollmentRepository.findByAccountIdAndCourseId(accountId,courseId).orElse(null);
        if(existCourseEnrollment != null && !existCourseEnrollment.getStatus().equals("deleted")) throw new AppException(CourseEnrollmentErrorCode.COURSE_ENROLLMENT_IS_EXIST);
        if(existCourseEnrollment != null) {
            existCourseEnrollment.setStatus("pending");
            existCourseEnrollment.setEnrolledAt(LocalDateTime.now());
            courseEnrollmentRepository.save(existCourseEnrollment);
            return  courseEnrollmentMapper.toCourseEnrollmentResponse(existCourseEnrollment);
        }
        CourseEnrollment courseEnrollment = CourseEnrollment.builder()
                .account(account)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .status("pending")
                .build();
        CourseEnrollment savedCourseEnrollment = courseEnrollmentRepository.save(courseEnrollment);
        return courseEnrollmentMapper.toCourseEnrollmentResponse(savedCourseEnrollment);
    }

    public void cancelRegisterCourse(String courseId) {
        var context = SecurityContextHolder.getContext();
        String accountId = context.getAuthentication().getName();
        if (accountId == null) {
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }
        courseRepository.findById(courseId).orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
        CourseEnrollment existCourseEnrollment = courseEnrollmentRepository.findByAccountIdAndCourseId(accountId,courseId).orElseThrow(() -> new AppException(CourseEnrollmentErrorCode.COURSE_ENROLLMENT_NOT_FOUND));
        if(existCourseEnrollment != null && existCourseEnrollment.getStatus().equals("pending")) {
            existCourseEnrollment.setStatus("deleted");
            courseEnrollmentRepository.save(existCourseEnrollment);
        }
    }

    @Transactional
    public CourseEnrollmentAdminResponse create(CourseEnrollmentAdminCreateRequest request) {
        // 1. Tìm khóa học
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));

        // 2. Tìm tài khoản
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));

        // 3. Tạo bản ghi enrollment
        CourseEnrollment courseEnrollment = CourseEnrollment.builder()
                .account(account)
                .course(course)
                .build();

        courseEnrollmentRepository.save(courseEnrollment);

        // 4. Trả response
        return courseEnrollmentMapper.toCourseEnrollmentAdminResponse(courseEnrollment);
    }


}
