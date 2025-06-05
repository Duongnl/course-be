package com.course.course_be.repository;

import com.course.course_be.entity.CourseEnrollment;
import com.course.course_be.entity.RefreshToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Integer> {
    Page<CourseEnrollment> findByAccount_Profile_NameContainingIgnoreCaseAndCourse_NameContainingIgnoreCaseAndStatusNot(
            String accountName, String CourseName, String status, Pageable pageable);

//    Page<CourseEnrollment> findByCourse_IdAndParentCourseEnrollmentIsNullAndStatusNot(String course_id, String status, Pageable pageable);
//
//    Page<CourseEnrollment> findByLesson_IdAndParentCourseEnrollmentIsNullAndStatusNot(String lesson_id, String status, Pageable pageable);
//
//    Page<CourseEnrollment> findByParentCourseEnrollment_IdAndStatusNot(Integer parentCourseEnrollment_id, String status, Pageable pageable);
}
