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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

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

    AccountRepository accountRepository;


    //    login
    public AuthenticationResponse loginWithFacebook(AuthenticationRequest request) {
        String accessFBToken = request.getAccessToken();

        // Gọi Facebook Graph API để lấy thông tin người dùng
        String url = "https://graph.facebook.com/me?fields=id,name,picture,email&access_token=" + accessFBToken;
        System.out.println("url >>> " +url);
        RestTemplate restTemplate = new RestTemplate();
        try {
            Map<String, Object> fbUser = restTemplate.getForObject(url, Map.class);

            // Du lieu nguoi dung
            String id = (String) fbUser.get("id");
            String email = (String) fbUser.get("email");
            String name = (String) fbUser.get("name");
            Map<String, Object> picture = (Map<String, Object>) fbUser.get("picture");
            String pictureUrl = (String) ((Map<String, Object>) picture.get("data")).get("url");

            System.out.println("id >>> "+ id);
            System.out.println("email >>> "+ email);
            System.out.println("name >>> "+ name);
            System.out.println("pictureUrl >>> "+ pictureUrl);


//            Tao ra token
            var refreshToken = generateRefreshToken(id);
            var accessToken = generateAccessToken(id);

          return  AuthenticationResponse.builder()
                            .refreshToken(refreshToken)
                            .accessToken(accessToken)
                            .authenticated(true)
                            .build();
        } catch (Exception e) {
            System.out.println("loi khi tao token");
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }
    }

    public void handleSaveAccount (String idFb) {
        if (!accountRepository.existsByFacebookId(idFb)) {
            Account account = new Account();
            account.setFacebookId(idFb);

        }
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
