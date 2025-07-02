package com.course.course_be.repository;

import com.course.course_be.dto.response.account.CourseProgressResponse;
import com.course.course_be.dto.response.account.MyCourseResponse;
import com.course.course_be.dto.response.statistic.StatCourseEnrollResponse;
import com.course.course_be.entity.CourseEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Integer> {

    /**
     * Truy vấn tiến độ học của người dùng (dùng cho trang dashboard)
     * - Tính số bài học đã học / tổng bài học
     * - Trả về trạng thái học: "completed" hoặc "inProgress"
     */
    @Query("""
        SELECT new com.course.course_be.dto.response.account.CourseProgressResponse(
            c.id,
            c.name,
            COUNT(DISTINCT lp.id),
            COUNT(DISTINCT l.id),
            ce.enrolledAt,
            CASE 
                WHEN COUNT(DISTINCT lp.id) = COUNT(DISTINCT l.id) THEN 'completed'
                ELSE 'inProgress'
            END
        )
        FROM CourseEnrollment ce
        JOIN ce.course c
        LEFT JOIN c.chapters ch
        LEFT JOIN ch.lessons l ON l.status = 'active'
        LEFT JOIN l.lessonProgresses lp ON lp.account.id = :accountId
        WHERE ce.account.id = :accountId
          AND ce.status <> 'pending' AND ce.status <> 'deleted'
          AND LOWER(c.name) LIKE LOWER(CONCAT('%', :courseName, '%'))
        GROUP BY c.id, c.name, ce.enrolledAt
        HAVING (
            :status IS NULL
            OR (:status = 'completed' AND COUNT(DISTINCT lp.id) = COUNT(DISTINCT l.id))
            OR (:status = 'inProgress' AND COUNT(DISTINCT lp.id) <> COUNT(DISTINCT l.id))
        )
        ORDER BY ce.enrolledAt DESC
    """)
    Page<CourseProgressResponse> findCourseProgress(
            @Param("accountId") String accountId,
            @Param("courseName") String courseName,
            @Param("status") String status,
            Pageable pageable
    );

    /**
     * Truy vấn các khóa học của user đã đăng ký
     * - Tính số bài học đã học / tổng bài học
     * - Trả về trạng thái học: "completed" hoặc "inProgress"
     * - Có thêm image URL
     */
    @Query("""
        SELECT new com.course.course_be.dto.response.account.MyCourseResponse(
            c.id,
            c.name,
            COUNT(DISTINCT lp.id),
            COUNT(DISTINCT l.id),
            ce.enrolledAt,
            CASE
                WHEN COUNT(DISTINCT lp.id) = COUNT(DISTINCT l.id) THEN 'completed'
                ELSE 'inProgress'
            END,
            COALESCE(c.imageUrl, '')
        )
        FROM CourseEnrollment ce
        JOIN ce.course c
        LEFT JOIN c.chapters ch
        LEFT JOIN ch.lessons l ON l.status = 'active'
        LEFT JOIN l.lessonProgresses lp ON lp.account.id = :accountId
        WHERE ce.account.id = :accountId
          AND ce.status = 'active'
          AND c.status = 'active'
          AND LOWER(c.name) LIKE LOWER(CONCAT('%', :courseName, '%'))
        GROUP BY c.id, c.name, ce.enrolledAt, c.imageUrl
        HAVING (
            :status IS NULL
            OR (:status = 'completed' AND COUNT(DISTINCT lp.id) = COUNT(DISTINCT l.id))
            OR (:status = 'inProgress' AND COUNT(DISTINCT lp.id) <> COUNT(DISTINCT l.id))
        )
    """)
    Page<MyCourseResponse> findMyCourse(
            @Param("accountId") String accountId,
            @Param("courseName") String courseName,
            @Param("status") String status,
            Pageable pageable
    );

    /**
     * Admin tìm theo tên người dùng và tên khóa học (dạng LIKE)
     */
    Page<CourseEnrollment> findByAccount_Profile_NameContainingIgnoreCaseAndCourse_NameContainingIgnoreCaseAndAccount_UsernameContainingIgnoreCaseAndStatusNot(
            String accountName, String courseName,String username,String statusExclude, Pageable pageable
    );

    /**
     * Admin lọc nâng cao hơn (có theo danh sách trạng thái)
     */
    Page<CourseEnrollment> findByAccount_Profile_NameContainingIgnoreCaseAndCourse_NameContainingIgnoreCaseAndAccount_UsernameContainingIgnoreCaseAndStatusIn(
            String accountName, String courseName,String username, List<String> statusList, Pageable pageable
    );

    /**
     * Đếm tổng số tài khoản đã đăng ký học (trừ tài khoản đã xóa)
     */
    @Query("""
         SELECT COUNT(DISTINCT ce.account.id)
        FROM CourseEnrollment ce
        WHERE ce.status = 'active'
          AND ce.account.status <> 'deleted'
    """)
    long countActiveAccountsEnrolled();

    /**
     * Lấy danh sách khóa học có lượt đăng ký nhiều nhất (top chart)
     */
    @Query("""
        SELECT new com.course.course_be.dto.response.statistic.StatCourseEnrollResponse(
            c.name,
            c.imageUrl,
            COUNT(e)
        )
        FROM Course c
        LEFT JOIN c.courseEnrollments e ON e.status <> 'deleted'
        GROUP BY c.id, c.name, c.imageUrl
        ORDER BY COUNT(e) DESC
    """)
    Page<StatCourseEnrollResponse> findMostEnrolledCourses(Pageable pageable);

    /**
     * Kiểm tra người dùng đã đăng ký khóa học chưa (đã active)
     */
    @Query("""
        SELECT COUNT(e) > 0
        FROM CourseEnrollment e
        WHERE e.course.id = :courseId
          AND e.account.id = :accountId
          AND e.status = 'active'
    """)
    boolean checkIsEnrollmentCourse(
            @Param("courseId") String courseId,
            @Param("accountId") String accountId
    );

    /**
     * Tìm bản ghi đăng ký theo accountId và courseId
     */
    Optional<CourseEnrollment> findByAccountIdAndCourseId(String accountId, String courseId);

    /**
     * Tìm danh sách các đăng ký đang chờ duyệt của người dùng
     */
    @Query("""
        SELECT ce
        FROM CourseEnrollment ce
        WHERE ce.account.id = :accountId AND ce.status <> 'active' AND ce.status <> 'deleted'  AND ce.course.status = 'active'
          AND (:courseName = '' OR LOWER(ce.course.name) LIKE LOWER(CONCAT('%', :courseName, '%')))
    """)
    Page<CourseEnrollment> findPendingCourseEnrollments(
            @Param("accountId") String accountId,
            @Param("courseName") String courseName,
            Pageable pageable
    );

    Optional<CourseEnrollment> findByAccountIdAndCourseIdAndStatus(String accountId, String courseId, String status);
    Optional<CourseEnrollment> findByIdAndStatus(Integer id, String status);
}
