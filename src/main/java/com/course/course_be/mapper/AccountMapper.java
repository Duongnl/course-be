package com.course.course_be.mapper;

import com.course.course_be.dto.response.account.AccountResponse;
import com.course.course_be.dto.response.account.CurrentAccountResponse;
import com.course.course_be.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface AccountMapper
{
    @Mapping(source = "profile.name", target="name")
    @Mapping(source = "profile.avatarUrl", target="avatarUrl")
    CurrentAccountResponse toCurrentAccountResponse(Account account);

    @Mapping(target = "name", source = "profile.name")
    @Mapping(target = "sex", source = "profile.sex")
    @Mapping(target = "birthday", source = "profile.birthday")
    AccountResponse toAccountResponse(Account account);
}
