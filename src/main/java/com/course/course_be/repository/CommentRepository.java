package com.course.course_be.repository;

import com.course.course_be.entity.Comment;
import com.course.course_be.entity.Lesson;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    /**
     * Tìm danh sách bình luận cho trang quản trị với điều kiện:
     * - Tên người viết bình luận (tìm gần đúng, không phân biệt hoa thường)
     * - Nội dung bình luận
     * - Tên bài học liên quan
     * - Trạng thái khác 'inactive'
     *
     * @param accountName   tên người dùng
     * @param content       nội dung bình luận
     * @param lessonName    tên bài học
     * @param courseName    tên khóa học
     * @param statusExclude trạng thái loại trừ (thường là 'inactive')
     * @param pageable      thông tin phân trang
     */
//    Page<Comment> findByAccount_Profile_NameContainingIgnoreCaseAndContentContainingIgnoreCaseAndLesson_NameContainingIgnoreCaseAndCourse_NameContainingIgnoreCaseAndStatusNot(
//            String accountName,
//            String content,
//            String lessonName,
//            String courseName,
//            String status,
//            Pageable pageable
//    );
    @Query("""
                SELECT c FROM Comment c
                LEFT JOIN c.lesson l    
                LEFT JOIN c.course cr
                LEFT JOIN c.account acc
                LEFT JOIN acc.profile p
                WHERE
                    (:accountName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :accountName, '%')))
                    AND (:content IS NULL OR LOWER(c.content) LIKE LOWER(CONCAT('%', :content, '%')))
                    AND (:lessonName IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', :lessonName, '%')))
                    AND (:courseName IS NULL OR LOWER(cr.name) LIKE LOWER(CONCAT('%', :courseName, '%')))
                    AND c.status <> :statusExclude
            """)
    Page<Comment> findCommentsForAdmin(
            @Param("accountName") String accountName,
            @Param("content") String content,
            @Param("lessonName") String lessonName,
            @Param("courseName") String courseName,
            @Param("statusExclude") String statusExclude,
            Pageable pageable
    );


    /**
     * Lấy bình luận cha theo khóa học (course), loại trừ các bình luận đã bị ẩn (status)
     */
    Page<Comment> findByCourse_IdAndParentCommentIsNullAndStatusNot(
            String courseId,
            String status,
            Pageable pageable
    );

    /**
     * Lấy bình luận cha theo bài học (lesson), loại trừ các bình luận đã bị ẩn
     */
    Page<Comment> findByLessonAndParentCommentIsNullAndStatusNot(
            Lesson lesson,
            String status,
            Pageable pageable
    );

    /**
     * Lấy các phản hồi (bình luận con) của một bình luận cụ thể
     */
    Page<Comment> findByParentCommentAndStatusNot(
            Comment parentComment,
            String status,
            Pageable pageable
    );

    /**
     * Truy vấn dạng cursor pagination:
     * - Lấy danh sách bình luận của một khóa học
     * - Có điều kiện nhỏ hơn thời gian `beforeCreatedAt` nếu được truyền vào
     * - Loại trừ bình luận có trạng thái bị ẩn
     * - Sắp xếp theo thời gian tạo mới nhất
     *
     * @param courseId        ID của khóa học
     * @param beforeCreatedAt (tuỳ chọn) giới hạn thời gian lấy comment trước đó
     * @param excludedStatus  trạng thái cần loại trừ (ví dụ: "inactive")
     * @param pageable        thông tin phân trang
     */
    @Query("""
                SELECT c FROM Comment c
                WHERE c.course.id = :courseId
                  AND c.status <> :excludedStatus
                  AND (:beforeCreatedAt IS NULL OR c.createdAt < :beforeCreatedAt)
                  AND c.parentComment.id IS NULL
                ORDER BY c.createdAt DESC, c.id DESC
            """)
    Page<Comment> findCommentsByCourseWithCursor(
            @Param("courseId") String courseId,
            @Param("beforeCreatedAt") LocalDateTime beforeCreatedAt,
            @Param("excludedStatus") String excludedStatus,
            Pageable pageable
    );
}
