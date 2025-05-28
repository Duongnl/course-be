package com.course.course_be.service;

import com.course.course_be.dto.response.account.CurrentAccountResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.mapper.AccountMapper;
import com.course.course_be.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

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

}
