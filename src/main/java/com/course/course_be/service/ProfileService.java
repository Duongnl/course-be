package com.course.course_be.service;

import com.course.course_be.dto.request.profile.ProfileUpdateRequest;
import com.course.course_be.dto.response.profile.UserProfileResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.Profile;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.ProfileErrorCode;
import com.course.course_be.mapper.ProfileMapper;
import com.course.course_be.repository.AccountRepository;
import com.course.course_be.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {

    ProfileRepository profileRepository;
    AuthenticationService authenticationService;
    ProfileMapper profileMapper;
    AccountRepository accountRepository;

    public UserProfileResponse getCurrentProfile() {
        // Lấy account hiện tại
        Account account = authenticationService.getMyAccountCurrent();
        // Tìm profile dựa trên account_id
        Profile profile = account.getProfile();
        if (profile == null)
            throw new AppException(ProfileErrorCode.PROFILE_NOT_FOUND);
        // Ánh xạ sang ProfileResponse
        return profileMapper.toUserProfileResponse(account, profile);
    }

    public UserProfileResponse updateProfile(ProfileUpdateRequest request) {
        Account account = authenticationService.getMyAccountCurrent(); //Lấy account hiện tại
        Profile profile = profileRepository.findByAccountId(account.getId());// lấy profile dựa trên account_id

        account.setPhoneNumber(request.getPhoneNumber());

        // Ánh xạ từ ProfileResponse sang Profile
        Profile updatedProfile = profileMapper.toProfile(request);

        // Cập nhật các trường, giữ nguyên id và account
        profile.setName(updatedProfile.getName());
        profile.setDetail(updatedProfile.getDetail());
        profile.setBirthday(updatedProfile.getBirthday());
        profile.setSex(updatedProfile.getSex());

        // Lưu profile
        accountRepository.save(account);
        profile = profileRepository.save(profile);

        // Ánh xạ trở lại ProfileResponse
        return profileMapper.toUserProfileResponse(account, profile);
    }
}
