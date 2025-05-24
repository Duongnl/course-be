package com.course.course_be.service;

import com.course.course_be.dto.request.auth.AccessTokenRequest;
import com.course.course_be.dto.request.auth.AuthenticationRequest;
import com.course.course_be.dto.request.auth.LoginRequest;
import com.course.course_be.dto.request.auth.RefreshTokenRequest;
import com.course.course_be.dto.response.auth.AuthenticationResponse;
import com.course.course_be.entity.Account;
import com.course.course_be.entity.Profile;
import com.course.course_be.entity.RefreshToken;
import com.course.course_be.exception.AccountErrorCode;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.AuthErrorCode;
import com.course.course_be.repository.AccountRepository;
import com.course.course_be.repository.ProfileRepository;
import com.course.course_be.repository.RefreshTokenRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

     ProfileRepository profileRepository;
     RefreshTokenRepository refreshTokenRepository;
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
    RefreshTokenService refreshTokenService;


    public AuthenticationResponse loginWithGoogle(AuthenticationRequest authenticationRequest) {
        try {
            String tokenUrl = "https://oauth2.googleapis.com/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", authenticationRequest.getCode());
            params.add("client_id", GOOGLE_CLIENT_ID);
            params.add("client_secret", GOOGLE_CLIENT_SECRET);
            params.add("redirect_uri", GOOGLE_REDIRECT_URI);
            params.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            Map<String, Object> tokenData = response.getBody();

            String accessTokenGG = (String) tokenData.get("access_token");
            String idToken = (String) tokenData.get("id_token");

            // Option 1: Lấy user info bằng access token (như bạn làm)
            String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + accessTokenGG;
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


            Account account = accountRepository.findByGoogleId(googleId);
            System.out.println("account >>> " + account);
//            tk moi
            if (account == null) {
                account   =   handleSaveNewAccount(userInfo);
                System.out.println("account >>> " + account);
            }
//            tk cu bi khoa
            else if (account.equals("inactive")) {
                    throw new AppException(AuthErrorCode.ACCOUNT_LOCKED);
            }


            // Tạo token của hệ thống bạn
            var refreshToken = generateRefreshToken(account);
            var accessToken = generateAccessToken(account);

            RefreshToken refreshTokenNew = new RefreshToken();
            refreshTokenNew.setRefreshToken(refreshToken);
            refreshTokenNew.setAccount(account);
            refreshTokenNew.setExpireDate(getExpireDateFromToken(refreshToken));
            refreshTokenRepository.save(refreshTokenNew);


            return AuthenticationResponse.builder()
                    .refreshToken(refreshToken)
                    .accessToken(accessToken)
                    .authenticated(true)
                    .build();
        } catch (Exception e) {
            // Các lỗi khác

            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }

    }



    public AuthenticationResponse login(LoginRequest request) {
        Account account = accountRepository.findByUsername(request.getUsername());

        if ( account == null) {
            throw new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND);
        }
        if (account.getPassword().equals(request.getPassword().trim())) {
            var refreshToken = generateRefreshToken(account);
            var accessToken = generateAccessToken(account);

            RefreshToken refreshTokenNew = new RefreshToken();
            refreshTokenNew.setRefreshToken(refreshToken);
            refreshTokenNew.setAccount(account);
            try {
                refreshTokenNew.setExpireDate(getExpireDateFromToken(refreshToken));
                refreshTokenRepository.save(refreshTokenNew);
            }catch (Exception e) {
                throw new AppException(AuthErrorCode.UNAUTHENTICATED);
            }
            return AuthenticationResponse.builder()
                    .refreshToken(refreshToken)
                    .accessToken(accessToken)
                    .authenticated(true)
                    .build();
        } else {
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }
    }


    public Account handleSaveNewAccount (Map<String, Object> userInfo ) {
        String email = (String) userInfo.get("email");
        String googleId = (String) userInfo.get("sub");
        String fullName = (String) userInfo.get("name");            // Tên đầy đủ
        String pictureUrl = (String) userInfo.get("picture");

        Account account = new Account();
        account.setGoogleId(googleId);
        account.setEmail(email);
        account.setRole("CLIENT");
        account.setStatus("active");
        account.setCreatedAt(LocalDateTime.now());

        Profile profile = new Profile();
        profile.setName(fullName);
        profile.setAvatarUrl(pictureUrl);
        account.setProfile(profile);
        profile.setAccount(account);

        try {

            profileRepository.save(profile);
        } catch (Exception e) {
            throw new AppException(AccountErrorCode.SAVE_USER_FAIL);
        }

        return account;
    }

    public AuthenticationResponse refreshToken (RefreshTokenRequest refreshTokenRequest)  {
        try {
            if (introspectRefreshToken(refreshTokenRequest)){
                String googleId = getIdFromToken(refreshTokenRequest.getRefreshToken());

                Account account = accountRepository.findById(googleId).orElseThrow(
                        () -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND)
                );

               if (account.getStatus().equals("inactive")) {
                    throw new AppException(AuthErrorCode.ACCOUNT_LOCKED);
                }

                String accessToken = generateAccessToken(account);
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

//  Ham lay username tu token
    public String getIdFromToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getSubject(); // Đây là username
    }





    public LocalDate getExpireDateFromToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Chuyển từ java.util.Date sang java.time.LocalDate
        return expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // kiem tra xem refresh token co hop le khong
    public boolean introspectRefreshToken(RefreshTokenRequest refreshTokenRequest)
          {
        var token = refreshTokenRequest.getRefreshToken();

//        neu khong co trong db thi da dang xuat
        if (!refreshTokenRepository.existsByRefreshToken(token)) {
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }

        try {

            JWSVerifier verifier = new MACVerifier(REFRESH_TOKEN_SECRET.getBytes());

//        token cua  ng dung
            SignedJWT signedJWT = SignedJWT.parse(token);

//        lay ra han cua token
            Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

//        tra ve true hoac flase
            var verified = signedJWT.verify(verifier);
//            Date now = new SimpleDateFormat("yyyy-MM-dd").parse("2025-06-22");
            if (!verified) {

                throw new AppException(AuthErrorCode.UNAUTHENTICATED);
            } else if ( !expityTime.after(new Date()))  {

                refreshTokenService.deleteRefreshToken(refreshTokenRequest);

                throw new AppException(AuthErrorCode.UNAUTHENTICATED);
            }
            else {
             return  true;
            }
        } catch (Exception e) {
            System.out.println("error >>> " + e.getMessage());
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }
    }

    // kiem tra xem refresh token co hop le khong
    public boolean introspectAccessToken(AccessTokenRequest accessTokenRequest)
    {
        var token = accessTokenRequest.getAccessToken();

//        neu khong co trong db thi da dang xuat


        try {

            JWSVerifier verifier = new MACVerifier(ACCESS_TOKEN_SECRET.getBytes());

//        token cua  ng dung
            SignedJWT signedJWT = SignedJWT.parse(token);

//        lay ra han cua token
            Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

//        tra ve true hoac flase
            var verified = signedJWT.verify(verifier);
            if (!verified || !expityTime.after(new Date())) {
                throw new AppException(AuthErrorCode.UNAUTHENTICATED);
            }
            else {
                return  true;
            }
        } catch (Exception e) {
            System.out.println("error >>> " + e.getMessage());
            throw new AppException(AuthErrorCode.UNAUTHENTICATED);
        }
    }




    public Account getMyAccountCurrent() {
        var context = SecurityContextHolder.getContext();
        String Id = context.getAuthentication().getName();
        return accountRepository.findById(Id).orElseThrow(
                () -> new AppException(AccountErrorCode.ACCOUNT_NOT_FOUND));
    }



    //    tao refresh token
    public String generateRefreshToken(Account account) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);// tao header
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder() // tao body
                .subject(account.getId())
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

    public String generateAccessToken(Account account) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);// tao header
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder() // tao body
                .subject(account.getId())
                .issuer("course.com") // token nay dc issuer tu ai
                .issueTime(new Date()) // thoi diem hien tai
                .expirationTime(new Date(
                        Instant.now().plus(15, ChronoUnit.MINUTES) // 15 phút là chuẩn an toàn
                                .toEpochMilli()))
                .claim("scope",account.getRole() )
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
