package com.course.course_be.repository;

import com.course.course_be.entity.Account;
import com.course.course_be.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    Profile findByAccountId(String accountId);
}
