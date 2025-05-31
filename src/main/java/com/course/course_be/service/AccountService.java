package com.course.course_be.service;

import com.course.course_be.dto.response.account.AccountResponse;
import com.course.course_be.dto.response.account.CurrentAccountResponse;
import com.course.course_be.dto.response.account.ResultPaginationDTO;
import com.course.course_be.entity.Account;
import com.course.course_be.mapper.AccountMapper;
import com.course.course_be.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;


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
}
