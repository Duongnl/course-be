package com.course.course_be.mapper;

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
}
