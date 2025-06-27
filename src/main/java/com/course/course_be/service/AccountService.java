package com.course.course_be.service;

import com.course.course_be.dto.request.account.CreateAccountRequest;
import com.course.course_be.dto.request.account.UpdateAccountRequest;
import com.course.course_be.dto.request.account.UpdatePasswordRequest;
import com.course.course_be.dto.response.account.AccountResponse;
import com.course.course_be.dto.response.account.CurrentAccountResponse;
import com.course.course_be.dto.response.course.CourseResponse;
import com.course.course_be.dto.response.course_enrollment.CourseEnrollmentResponse;
import com.course.course_be.dto.response.statistic.CountResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.CourseEnrollment;
import com.course.course_be.entity.Profile;
import com.course.course_be.exception.AccountErrorCode;
import com.course.course_be.exception.AppException;
import com.course.course_be.mapper.AccountMapper;
import com.course.course_be.mapper.CourseEnrollmentMapper;
import com.course.course_be.mapper.CourseMapper;
import com.course.course_be.repository.AccountRepository;
import com.course.course_be.repository.CourseEnrollmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {

    AccountRepository accountRepository;
    AuthenticationService authenticationService;
    AccountMapper accountMapper;
    CourseEnrollmentRepository courseEnrollmentRepository;
    CourseMapper courseMapper;
    CourseEnrollmentMapper courseEnrollmentMapper;

    public CurrentAccountResponse getCurrentAccount() {
        Account account= authenticationService.getMyAccountCurrent();
        CurrentAccountResponse currentAccountResponse = accountMapper.toCurrentAccountResponse(account);
        return accountMapper.toCurrentAccountResponse(account);

    }

    public Page<AccountResponse> filterAccounts(
            Integer page,
            Integer perPage,
            String name,
            String username,
            String email,
            String sex,
            String role,
            String status
    ) {
        // Handle pagination
        page = (page == null || page < 1) ? 1 : page; // Ensure page is at least 1
        page = page - 1; // Convert 1-based to 0-based
        perPage = (perPage == null || perPage < 1) ? 10 : perPage; // Ensure perPage is at least 1
        Pageable pageable = PageRequest.of(page, perPage);

        // Handle filter parameters
        name = name == null ? "" : name;
        username = username == null ? "" : username;
        email = email == null ? "" : email;
        sex = sex == null ? "" : sex;
        role = role == null ? "" : role;
        status = status == null ? "" : status;

        // Fetch accounts from repository
        Page<Account> accountPage = accountRepository.filterAccounts(
                name, username, email, sex, role, status, pageable
        );

        // Map to DTO
        return accountPage.map(accountMapper::toAccountResponse);
    }

    public AccountResponse createNew(CreateAccountRequest request) {
        // Kiểm tra email và username trùng lặp
        validateEmailAndUsername(request.getEmail(), request.getUsername(), null);

        // Tạo Account
        Account account = Account.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .phoneNumber(request.getPhone())
                .role(request.getRole())
                .status(request.getStatus())
                .createdAt(LocalDateTime.now())
                .build();

        // Tạo Profile
        Profile profile = Profile.builder()
                .name(request.getName())
                .avatarUrl(request.getAvatarUrl())
                .birthday(request.getBirthday() != null && !request.getBirthday().isEmpty()
                        ? LocalDate.parse(request.getBirthday())
                        : null) // Gán null nếu rỗng
                .sex(request.getSex())
                .account(account) // Set account reference
                .build();

        // Set profile cho account
        account.setProfile(profile);

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toAccountResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public AccountResponse findById(String id) {
        Account account = accountRepository.findByIdAndStatusNot(id, "deleted")
                .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        return accountMapper.toAccountResponse(account);
    }

    public AccountResponse update(String id, UpdateAccountRequest request) {
        Account account = accountRepository.findByIdAndStatusNot(id, "deleted")
                .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        // Kiểm tra email và username trùng lặp
        validateEmailAndUsername(request.getEmail(), request.getUsername(), id);

        // Cập nhật Account
        account.setEmail(request.getEmail());
        account.setUsername(request.getUsername());
        account.setPhoneNumber(request.getPhone());
        account.setRole(request.getRole());
        account.setStatus(request.getStatus());

        // Cập nhật password nếu được cung cấp
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            account.setPassword(request.getPassword());
        }

        // Cập nhật Profile
        Profile profile = account.getProfile();
        if (profile != null) {
            profile.setName(request.getName());
            profile.setAvatarUrl(request.getAvatarUrl());
            if(request.getBirthday() != null && !request.getBirthday().trim().isEmpty()){
                profile.setBirthday(LocalDate.parse(request.getBirthday()));
            } else {
                profile.setBirthday(null);
            }

            profile.setSex(request.getSex());
        }

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toAccountResponse(savedAccount);
    }

    public void delete(String id) {
        Account account = accountRepository.findByIdAndStatusNot(id, "deleted")
                .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        account.setStatus("deleted");
        accountRepository.save(account);
    }

    private void validateEmailAndUsername(String email, String username, String excludeId) {
        // Bỏ qua kiểm tra nếu email rỗng hoặc null
        if (email != null && !email.trim().isEmpty()) {
            if (excludeId == null) {
                if (accountRepository.existsByEmail(email)) {
                    throw new AppException(AccountErrorCode.EMAIL_ALREADY_EXISTS);
                }
            } else {
                if (accountRepository.existsByEmailAndIdNot(email, excludeId)) {
                    throw new AppException(AccountErrorCode.EMAIL_ALREADY_EXISTS);
                }
            }
        }

        // Bỏ qua kiểm tra nếu username rỗng hoặc null
        if (username != null && !username.trim().isEmpty()) {
            if (excludeId == null) {
                if (accountRepository.existsByUsername(username)) {
                    throw new AppException(AccountErrorCode.USERNAME_ALREADY_EXISTS);
                }
            } else {
                if (accountRepository.existsByUsernameAndIdNot(username, excludeId)) {
                    throw new AppException(AccountErrorCode.USERNAME_ALREADY_EXISTS);
                }
            }
        }
    }

    public CountResponse countAccount  () {
        return CountResponse.builder()
                .name("user")
                .value(accountRepository.countByStatus("active"))
                .total(accountRepository.countByStatusNot("deleted"))
                .build();
    }

    public CountResponse countAccountEnroll () {
        return CountResponse.builder()
                .name("enroll")
                .value(courseEnrollmentRepository.countActiveAccountsEnrolled())
                .total(accountRepository.countByStatus("active"))
                .build();
    }

    public Page<CourseResponse> getCurrentAccountCourseEnrollments(
            Integer page,
            Integer perPage,
            String courseName) {

        Account account = authenticationService.getMyAccountCurrent();
        String accountId = account.getId();

        // Xử lý pagination parameters
        page = page == null ? 0 : page - 1;
        perPage = perPage == null ? 6 : perPage;
        Pageable pageable = PageRequest.of(page, perPage);

        // Xử lý tên course để tìm kiếm
        courseName = courseName == null ? "" : courseName;
        if (courseName.isEmpty()) {
            pageable = PageRequest.of(page, perPage, Sort.by("enrolledAt").descending());
        }

        // Gọi repository method để lấy data với status = "pending"
        Page<CourseEnrollment> enrollmentPage = courseEnrollmentRepository
                .findPendingCourseEnrollments(accountId, courseName, pageable);

        // Chuyển đổi từ Page<CourseEnrollment> sang Page<CourseResponse>
        return enrollmentPage.map(courseEnrollmentMapper::toCourseResponse);
    }

    @Transactional
    public void changePassword(UpdatePasswordRequest request) {
        // Lấy account hiện tại
        Account account = authenticationService.getMyAccountCurrent();

        // Kiểm tra mật khẩu cũ
        if (!request.getOldPassword().equals(account.getPassword())) {
            throw new AppException(AccountErrorCode.OLD_PASSWORD_INCORRECT);
        }

        // Kiểm tra nếu mật khẩu cũ và mật khẩu mới giống nhau
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new AppException(AccountErrorCode.NEW_PASSWORD_SAME_AS_OLD);
        }

        // Cập nhật mật khẩu mới
        account.setPassword(request.getNewPassword());

        // Lưu thay đổi vào cơ sở dữ liệu
        try {
            accountRepository.save(account);
        } catch (Exception e) {
            throw new AppException(AccountErrorCode.SAVE_PASSWORD_FAIL);
        }
    }
}
