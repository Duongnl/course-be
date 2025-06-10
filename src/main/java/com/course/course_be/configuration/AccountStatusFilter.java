package com.course.course_be.configuration;

import com.course.course_be.entity.Account;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.AuthErrorCode;
import com.course.course_be.repository.AccountRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class AccountStatusFilter extends OncePerRequestFilter {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("authentication >>> " + authentication);
        if (authentication != null && authentication.isAuthenticated()) {
            String accountId = authentication.getName();
            if (accountId != null) {
                Optional<Account> account = accountRepository.findById(accountId);
                if (account.isPresent()) {
                    if (!account.get().getStatus().equals("active")) {
                        jwtAuthenticationEntryPoint.commence(request, response,
                                new AuthenticationException("Account is not active") {
                                });
                        return; // dừng filter chain, trả về lỗi ngay
                    }
                } else {
                    jwtAuthenticationEntryPoint.commence(request, response,
                            new AuthenticationException("Account is not active") {
                            });
                    return; // dừng filter chain, trả về lỗi ngay
                }

            } else {
                jwtAuthenticationEntryPoint.commence(request, response,
                        new AuthenticationException("Account is not active") {
                        });
                return; // dừng filter chain, trả về lỗi ngay
            }
        }
        filterChain.doFilter(request, response);
    }
}

