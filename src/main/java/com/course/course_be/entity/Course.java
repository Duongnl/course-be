package com.course.course_be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "course")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
// Bang khóa học
public class Course {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;


//    Ten khoa hoc
    @Column(name = "name", nullable = false)
    String name;

//    Mo ta khoa hoc
    @Column(name = "detail" ,columnDefinition = "TEXT", nullable = true)
    String detail;

//    gia khoa hoc
    @Column(name = "price", precision = 15, scale = 0, nullable = true)
    private BigDecimal price;

    @Column(name = "video_url", nullable = true, length = 500)
    String videoUrl;

    @Column(name = "image_url", nullable = true, length = 500)
    String imageUrl;

//    Thoi gian tao khoa hoc
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;


//    Thoi gian cap nhat khoa hoc
    @Column(name = "updated_at", nullable = true)
    LocalDateTime updatedAt;

//    Trang thai cua khoa hoc
    @Column(name = "status", nullable = false)
    String status;


//    Danh muc cua khoa hoc (co the null)
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true, referencedColumnName = "id")
    @JsonBackReference
    private Category category;

//    Danh sach chuong cua khoa hoc
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonManagedReference
    @OrderBy("chapterNumber ASC")
    private Set<Chapter> chapters;

//    Danh sach hoc vien hoc khoa hoc nay
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<CourseEnrollment> courseEnrollments;

// Comment cua khoa hoc
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Comment> comments;

}
