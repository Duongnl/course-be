package com.course.course_be.service;

import com.course.course_be.dto.request.course_enrollment.ChangeStatusRequest;
import com.course.course_be.dto.request.course_enrollment.CourseEnrollmentAdminCreateRequest;
import com.course.course_be.dto.request.course_enrollment.CourseEnrollmentAdminFilterRequest;
import com.course.course_be.dto.request.course_enrollment.RegisterCourseClient;
import com.course.course_be.dto.response.account.CourseProgressResponse;
import com.course.course_be.dto.response.account.MyCourseResponse;
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
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseEnrollmentService {

    AuthenticationService authenticationService;
    CourseEnrollmentMapper courseEnrollmentMapper;
    CourseMapper courseMapper;
    CourseEnrollmentRepository courseEnrollmentRepository;
    CourseRepository courseRepository;
    AccountRepository accountRepository;

    /**
     * Lấy tối đa 4 khóa học gần nhất mà người dùng đang học
     */
    public List<CourseEnrollCardResponse> getCourseEnroll() {
        Account account = authenticationService.getMyAccountCurrent();

        List<CourseEnrollment> sortedEnrollments = new ArrayList<>(account.getCourseEnrollments());

        sortedEnrollments.sort((a, b) -> b.getEnrolledAt().compareTo(a.getEnrolledAt())); // mới nhất trước

        return sortedEnrollments.stream().limit(4).map(enrollment -> courseMapper.toCourseStudyingResponse(enrollment.getCourse())).collect(Collectors.toList());
    }

    /**
     * Admin thay đổi trạng thái của một lượt đăng ký học
     */
    @Transactional
    public CourseEnrollmentAdminResponse changeStatus(ChangeStatusRequest request) {
        CourseEnrollment courseEnrollment = courseEnrollmentRepository.findById(request.getCourseEnrollmentId()).orElseThrow(() -> new AppException(CourseEnrollmentErrorCode.COURSE_ENROLLMENT_NOT_FOUND));

        if (!Objects.equals(courseEnrollment.getStatus(), request.getStatus())) {
            courseEnrollment.setStatus(request.getStatus());
        }

        return courseEnrollmentMapper.toCourseEnrollmentAdminResponse(courseEnrollment);
    }

    /**
     * Lấy tiến độ khóa học của 1 người dùng
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Page<CourseProgressResponse> getCourseProgress(String accountId, Integer page, Integer perPage, String courseName, String status) {
        page = (page == null) ? 0 : page - 1;
        perPage = (perPage == null) ? 10 : perPage;
        Pageable pageable = PageRequest.of(page, perPage);

        courseName = Optional.ofNullable(courseName).orElse("");
        status = (status == null || status.isEmpty() || status.matches("inProgress.completed|completed.inProgress")) ? null : status;

        return courseEnrollmentRepository.findCourseProgress(accountId, courseName, status, pageable);
    }

    /**
     * Người dùng xem danh sách khóa học đã đăng ký
     */
    public Page<MyCourseResponse> getMyCourse(Integer page, Integer perPage, String courseName, String status) {
        String accountId = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new AppException(AuthErrorCode.UNAUTHENTICATED));

        page = (page == null) ? 0 : page - 1;
        perPage = (perPage == null) ? 6 : perPage;

        courseName = Optional.ofNullable(courseName).orElse("");
        status = (status == null || status.isEmpty() || status.matches("inProgress.completed|completed.inProgress")) ? null : status;

        Pageable pageable = courseName.isEmpty() ? PageRequest.of(page, perPage, Sort.by("enrolledAt").descending()) : PageRequest.of(page, perPage);

        return courseEnrollmentRepository.findMyCourse(accountId, courseName, status, pageable);
    }

    /**
     * Admin xem danh sách lượt đăng ký học (theo filter)
     */
    public Page<CourseEnrollmentAdminResponse> getListCourseEnrollment(CourseEnrollmentAdminFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPerPage(), Sort.by(Sort.Direction.fromString(request.getOrder()), request.getSort()));

        String accountName = Optional.ofNullable(request.getAccountName()).orElse("");
        String courseName = Optional.ofNullable(request.getCourseName()).orElse("");

        Page<CourseEnrollment> courseEnrollmentPage;

        if (request.getStatus() == null || request.getStatus().isEmpty()) {
            courseEnrollmentPage = courseEnrollmentRepository.findByAccount_Profile_NameContainingIgnoreCaseAndCourse_NameContainingIgnoreCase(accountName, courseName, pageable);
        } else {
            List<String> statusList = Arrays.asList(request.getStatus().split("\\."));
            courseEnrollmentPage = courseEnrollmentRepository.findByAccount_Profile_NameContainingIgnoreCaseAndCourse_NameContainingIgnoreCaseAndStatusIn(accountName, courseName, statusList, pageable);
        }

        return courseEnrollmentPage.map(courseEnrollmentMapper::toCourseEnrollmentAdminResponse);
    }

    /**
     * Người dùng kiểm tra trạng thái đăng ký của mình đối với một khóa học
     */
    public CourseEnrollmentResponse getCourseEnrollmentRegister(String courseId) {
        String accountId = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new AppException(AuthErrorCode.UNAUTHENTICATED));

        courseRepository.findById(courseId).orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));

        CourseEnrollment courseEnrollment = courseEnrollmentRepository.findByAccountIdAndCourseId(accountId, courseId).orElse(null); // trả về null nếu chưa đăng ký

        return courseEnrollmentMapper.toCourseEnrollmentResponse(courseEnrollment);
    }

    /**
     * Người dùng đăng ký học một khóa học
     */
    public CourseEnrollmentResponse registerCourse(RegisterCourseClient registerCourseClient) {
        String accountId = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new AppException(AuthErrorCode.UNAUTHENTICATED));

        String courseId = registerCourseClient.getCourseId();

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AppException(AuthErrorCode.UNAUTHENTICATED));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));

        CourseEnrollment existing = courseEnrollmentRepository.findByAccountIdAndCourseId(accountId, courseId).orElse(null);

        if (existing != null && !"deleted".equals(existing.getStatus())) {
            throw new AppException(CourseEnrollmentErrorCode.COURSE_ENROLLMENT_IS_EXIST);
        }

        if (existing != null) {
            existing.setStatus("pending");
            existing.setEnrolledAt(LocalDateTime.now());
            courseEnrollmentRepository.save(existing);
            return courseEnrollmentMapper.toCourseEnrollmentResponse(existing);
        }

        CourseEnrollment newEnrollment = CourseEnrollment.builder().account(account).course(course).enrolledAt(LocalDateTime.now()).status("pending").build();

        CourseEnrollment saved = courseEnrollmentRepository.save(newEnrollment);
        return courseEnrollmentMapper.toCourseEnrollmentResponse(saved);
    }

    /**
     * Hủy đăng ký khóa học (chỉ nếu đang ở trạng thái pending)
     */
    public void cancelRegisterCourse(String courseId) {
        String accountId = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new AppException(AuthErrorCode.UNAUTHENTICATED));

        courseRepository.findById(courseId).orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));

        CourseEnrollment enrollment = courseEnrollmentRepository.findByAccountIdAndCourseId(accountId, courseId).orElseThrow(() -> new AppException(CourseEnrollmentErrorCode.COURSE_ENROLLMENT_NOT_FOUND));

        if ("pending".equals(enrollment.getStatus())) {
            enrollment.setStatus("deleted");
            courseEnrollmentRepository.save(enrollment);
        }
    }

    /**
     * Admin tạo mới một lượt đăng ký học
     */
    @Transactional
    public CourseEnrollmentAdminResponse create(CourseEnrollmentAdminCreateRequest request) {
        // 1. Tìm khóa học, nếu không có thì throw lỗi
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));

        // 2. Tìm tài khoản, nếu không có thì throw lỗi
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AppException(AuthErrorCode.UNAUTHENTICATED));

        // 3. Kiểm tra nếu đã đăng ký rồi thì trả về bản ghi cũ (không tạo lại)
        Optional<CourseEnrollment> existOpt = courseEnrollmentRepository.findByAccountIdAndCourseId(account.getId(), course.getId());
        if (existOpt.isPresent()) {
            CourseEnrollment exist = existOpt.get();

            // Nếu đang active thì trả về luôn
            if (!"deleted".equals(exist.getStatus())) {
                return courseEnrollmentMapper.toCourseEnrollmentAdminResponse(exist);
            }

            // Nếu trạng thái đã bị xóa, thì phục hồi lại
            exist.setStatus("active");
            exist.setEnrolledAt(LocalDateTime.now());
            courseEnrollmentRepository.save(exist);
            return courseEnrollmentMapper.toCourseEnrollmentAdminResponse(exist);
        }

        // 4. Nếu chưa tồn tại -> tạo mới
        CourseEnrollment courseEnrollment = CourseEnrollment.builder()
                .account(account)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .status("active")
                .build();

        courseEnrollmentRepository.save(courseEnrollment);

        // 5. Trả về response
        return courseEnrollmentMapper.toCourseEnrollmentAdminResponse(courseEnrollment);
    }
}
