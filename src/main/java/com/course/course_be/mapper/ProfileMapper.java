package com.course.course_be.mapper;


import com.course.course_be.dto.request.profile.ProfileUpdateRequest;
import com.course.course_be.dto.response.profile.ProfileResponse;
import com.course.course_be.dto.response.profile.UserProfileResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "avatarUrl", source = "avatarUrl")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "detail", source = "detail")
    @Mapping(target = "birthday", source = "birthday")
    @Mapping(target = "sex", source = "sex")
    Profile toProfile(ProfileUpdateRequest request);

    ProfileResponse toProfileResponse(Profile profile);

    @Mapping(source = "profile.avatarUrl", target = "avatarUrl")
    @Mapping(source = "account.email", target = "email")
    @Mapping(source = "profile.name", target = "name")
    @Mapping(source = "account.phoneNumber", target = "phoneNumber")
    @Mapping(source = "profile.birthday", target = "birthday")
    @Mapping(source = "profile.detail", target = "detail")
    @Mapping(source = "profile.sex", target = "sex")
    UserProfileResponse toUserProfileResponse(Account account, Profile profile);
}