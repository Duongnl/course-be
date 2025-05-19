package com.course.course_be.service;

import com.course.course_be.dto.request.auth.AuthenticationRequest;
import com.course.course_be.dto.request.auth.RefreshTokenRequest;
import com.course.course_be.dto.response.auth.AuthenticationResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.AuthErrorCode;
import com.course.course_be.repository.AccountRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    // de no kh inject vao contructe cua lombok
    //    key refresh token
    @NonFinal
    @Value("${jwt.refreshTokenSecret}")
    private String REFRESH_TOKEN_SECRET;

    //    key access token
    @NonFinal
    @Value("${jwt.accessTokenSecret}")
    private String ACCESS_TOKEN_SECRET;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;



    AccountRepository accountRepository;


    public AuthenticationResponse loginWithGoogle(AuthenticationRequest authenticationRequest) {
        try {
            String tokenUrl = "https://oauth2.googleapis.com/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", authenticationRequest.getAccessToken());
            params.add("client_id", GOOGLE_CLIENT_ID);
            params.add("client_secret", GOOGLE_CLIENT_SECRET);
            params.add("redirect_uri", GOOGLE_REDIRECT_URI);
            params.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            Map<String, Object> tokenData = response.getBody();

            String accessToken = (String) tokenData.get("access_token");
            String idToken = (String) tokenData.get("id_token");

            // Option 1: Lấy user info bằng access token (như bạn làm)
            String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + accessToken;
            ResponseEntity<Map> userResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
            Map<String, Object> userInfo = userResponse.getBody();

            String email = (String) userInfo.get("email");
            String googleId = (String) userInfo.get("sub");
            String fullName = (String) userInfo.get("name");            // Tên đầy đủ
            String pictureUrl = (String) userInfo.get("picture");

            System.out.println("email >>> " + email);
            System.out.println("googleId >>> " + googleId);
            System.out.println("fullName >>> " + fullName);
            System.out.println("pictureUrl >>> " + pictureUrl);


            // Ảnh đại diện
//            Optional<Account> account = accountRepository.findByFacebookId(id);
//
////            tk moi
//            if (account.isEmpty()) {
//
//            }
////            tk cu
//            else {
////                tk dang hoat dong
//                if (account.get().getStatus().equals("active")) {
//
//                }
////                tk bị khóa
//                else {
//                    throw new AppException(AuthErrorCode.ACCOUNT_LOCKED);
//                }
//            }


            // Tạo token của hệ thống bạn
//            var refreshToken = generateRefreshToken(id);
//            var accessToken = generateAccessToken(id);

            return AuthenticationResponse.builder()
//                    .refreshToken(refreshToken)
//                    .accessToken(accessToken)
                    .authenticated(true)
                    .build();
        } catch (Exception e) {
            // Các lỗi khác
            System.err.println("Lỗi không xác định: " + e.getMessage());
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }

    }


    public void handleSaveNewAccount (String idFb) {

    }

    public AuthenticationResponse refreshToken (RefreshTokenRequest refreshTokenRequest)  {
        try {
            if (introspectRefreshToken(refreshTokenRequest)){
                String username = getUsernameFromToken(refreshTokenRequest.getRefreshToken());
                String accessToken = generateAccessToken(username);
                return  AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .authenticated(true)
                        .build();

            } else {
                throw new AppException(AuthErrorCode.UNAUTHENTICATED);
            }
        } catch (Exception e) {
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }

     }

//     Ham lay username tu token
    public String getUsernameFromToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getSubject(); // Đây là username
    }


    // kiem tra xem refresh token co hop le khong
    public boolean introspectRefreshToken(RefreshTokenRequest refreshTokenRequest)
            throws JOSEException, ParseException {
        var token = refreshTokenRequest.getRefreshToken();

        try {

            JWSVerifier verifier = new MACVerifier(REFRESH_TOKEN_SECRET.getBytes());

//        token cua  ng dung
            SignedJWT signedJWT = SignedJWT.parse(token);

//        lay ra han cua token
            Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

//        tra ve true hoac flase
            var verified = signedJWT.verify(verifier);
            if (!verified || !expityTime.after(new Date())) {
                throw new AppException(AuthErrorCode.UNAUTHENTICATED);
            } else {
             return  true;
            }
        } catch (Exception e) {
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }
    }



    //    tao refresh token
    public String generateRefreshToken(String username) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);// tao header
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder() // tao body
                .subject(username)
                .issuer("course.com") // token nay dc issuer tu ai
                .issueTime(new Date()) // thoi diem hien tai
                .expirationTime(new Date(
                        Instant.now().plus(30, ChronoUnit.DAYS)  // 30 ngày
                                .toEpochMilli()))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject()); // tao pay load
        JWSObject jwsObject = new JWSObject(header, payload);// built thong tin token
        try {
            jwsObject.sign(new MACSigner(REFRESH_TOKEN_SECRET.getBytes())); // ky token
            return jwsObject.serialize(); // tra ve kieu string
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    //    tao access token
    public String generateAccessToken(String username) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);// tao header
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder() // tao body
                .subject(username)
                .issuer("course.com") // token nay dc issuer tu ai
                .issueTime(new Date()) // thoi diem hien tai
                .expirationTime(new Date(
                        Instant.now().plus(15, ChronoUnit.MINUTES) // 15 phút là chuẩn an toàn
                                .toEpochMilli()))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject()); // tao pay load
        JWSObject jwsObject = new JWSObject(header, payload);// built thong tin token
        try {
            jwsObject.sign(new MACSigner(ACCESS_TOKEN_SECRET.getBytes())); // ky token
            return jwsObject.serialize(); // tra ve kieu string
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }



}
