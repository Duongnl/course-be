package com.course.course_be.service;

import com.course.course_be.dto.request.account.CreateAccountRequest;
import com.course.course_be.dto.request.account.UpdateAccountRequest;
import com.course.course_be.dto.response.account.AccountResponse;
import com.course.course_be.dto.response.account.CurrentAccountResponse;
import com.course.course_be.dto.response.account.ResultPaginationDTO;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.Profile;
import com.course.course_be.exception.AccountErrorCode;
import com.course.course_be.exception.AppException;
import com.course.course_be.mapper.AccountMapper;
import com.course.course_be.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {

    AccountRepository accountRepository;
    AuthenticationService authenticationService;
    AccountMapper accountMapper;

    public CurrentAccountResponse getCurrentAccount() {
        Account account= authenticationService.getMyAccountCurrent();
        CurrentAccountResponse currentAccountResponse = accountMapper.toCurrentAccountResponse(account);
        return accountMapper.toCurrentAccountResponse(account);

    }

    public ResultPaginationDTO getAllAccounts (Specification<Account> spec, Pageable pageable) {
        // Lấy danh sách phân trang
        Page<Account> pageAccount = accountRepository.findAll(spec, pageable);

        // Tạo ResultPaginationDTO
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        // Thiết lập meta
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageAccount.getTotalPages());
        meta.setTotal(pageAccount.getTotalElements());
        rs.setMeta(meta);

        // Chuyển đổi sang DTO
        List<AccountResponse> listAccount = pageAccount.getContent()
                .stream()
                .map(accountMapper::toAccountResponse)
                .toList();

        rs.setResult(listAccount);
        return rs;
    }

    public Page<AccountResponse> filterAccounts(
            Integer page,
            Integer perPage,
            String name,
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
        email = email == null ? "" : email;
        sex = sex == null ? "" : sex;
        role = role == null ? "" : role;
        status = status == null ? "" : status;

        // Fetch accounts from repository
        Page<Account> accountPage = accountRepository.filterAccounts(
                name, email, sex, role, status, pageable
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
                .birthday(LocalDate.parse(request.getBirthday()))
                .sex(request.getSex())
                .account(account)  // Set account reference
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

    @Transactional(readOnly = true)
    public ResultPaginationDTO findAll(Specification<Account> spec, Pageable pageable) {
        // Thêm điều kiện lọc status khác "deleted"
        Specification<Account> statusSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("status"), "deleted");

        // Kết hợp với specification từ @Filter
        Specification<Account> combinedSpec = spec != null ?
                Specification.where(statusSpec).and(spec) : statusSpec;

        // Lấy danh sách phân trang với specification
        Page<Account> pageAccount = accountRepository.findAll(combinedSpec, pageable);

        // Tạo ResultPaginationDTO
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        // Thiết lập meta
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageAccount.getTotalPages());
        meta.setTotal(pageAccount.getTotalElements());
        rs.setMeta(meta);

        // Chuyển đổi sang DTO
        List<AccountResponse> listAccount = pageAccount.getContent()
                .stream()
                .map(accountMapper::toAccountResponse)
                .toList();

        rs.setResult(listAccount);
        return rs;
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
            profile.setBirthday(LocalDate.parse(request.getBirthday()));
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

//    @Transactional(readOnly = true)
//    public AccountResponse findByEmail(String email) {
//        Account account = accountRepository.findByEmailAndStatusNot(email, "deleted")
//                .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));
//        return accountMapper.toAccountResponse(account);
//    }
//
//    @Transactional(readOnly = true)
//    public AccountResponse findByUsername(String username) {
//        Account account = accountRepository.findByUsernameAndStatusNot(username, "deleted")
//                .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));
//        return accountMapper.toAccountResponse(account);
//    }
//
//    public void changePassword(String id, String newPassword) {
//        Account account = accountRepository.findByIdAndStatusNot(id, "deleted")
//                .orElseThrow(() -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));
//        account.setPassword(newPassword);
//        accountRepository.save(account);
//    }

    private void validateEmailAndUsername(String email, String username, String excludeId) {
        if (excludeId == null) {
            if (accountRepository.existsByEmail(email)) {
                throw new AppException(AccountErrorCode.EMAIL_ALREADY_EXISTS);
            }
            if (accountRepository.existsByUsername(username)) {
                throw new AppException(AccountErrorCode.USERNAME_ALREADY_EXISTS);
            }
        } else {
            if (accountRepository.existsByEmailAndIdNot(email, excludeId)) {
                throw new AppException(AccountErrorCode.EMAIL_ALREADY_EXISTS);
            }
            if (accountRepository.existsByUsernameAndIdNot(username, excludeId)) {
                throw new AppException(AccountErrorCode.USERNAME_ALREADY_EXISTS);
            }
        }
    }
}
