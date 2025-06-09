package com.course.course_be.mapper;

import com.course.course_be.dto.request.account.CreateAccountRequest;
import com.course.course_be.dto.request.account.UpdateAccountRequest;
import com.course.course_be.dto.response.account.AccountResponse;
import com.course.course_be.dto.response.account.CurrentAccountResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;


@Mapper(componentModel = "spring")
public interface AccountMapper
{
    @Mapping(source = "profile.name", target="name")
    @Mapping(source = "profile.avatarUrl", target="avatarUrl")
    CurrentAccountResponse toCurrentAccountResponse(Account account);

    @Mapping(source = "phoneNumber", target = "phone")
    @Mapping(source = "profile.name", target = "name")
    @Mapping(source = "profile.avatarUrl", target = "avatarUrl")
    @Mapping(source = "profile.sex", target = "sex")
    @Mapping(source = "profile.birthday", target = "birthday", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    AccountResponse toAccountResponse(Account account);

    List<AccountResponse> toAccountResponseList(List<Account> accounts);

}
