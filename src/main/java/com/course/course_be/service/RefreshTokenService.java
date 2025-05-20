package com.course.course_be.service;

import com.course.course_be.dto.request.auth.RefreshTokenRequest;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.RefreshToken;
import com.course.course_be.exception.AccountErrorCode;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.RefreshTokenErrorCode;
import com.course.course_be.repository.AccountRepository;
import com.course.course_be.repository.RefreshTokenRepository;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenService {

    RefreshTokenRepository refreshTokenRepository;

    public void deleteRefreshToken (RefreshTokenRequest refreshTokenRequest ) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenRequest.getRefreshToken());
        if (refreshToken == null) {
            throw  new AppException(RefreshTokenErrorCode.NOT_FOUNT);
        }

        try {
            System.out.println("refresh token >>> "+ refreshToken.getId());
            refreshTokenRepository.delete(refreshToken);
        } catch (Exception e) {
            throw new AppException(RefreshTokenErrorCode.NOT_FOUNT);
        }

    }
}
