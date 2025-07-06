package com.course.course_be.service;

import com.course.course_be.dto.request.course.CreateCourseRequest;
import com.course.course_be.dto.request.course.UpdateCourseRequest;
import com.course.course_be.dto.response.course.CourseResponse;
import com.course.course_be.dto.response.courseclient.CourseClientResponse;
import com.course.course_be.dto.response.lesson.LessonResponse;
import com.course.course_be.dto.response.statistic.CountResponse;
import com.course.course_be.dto.response.homeclient.CourseCardResponse;
import com.course.course_be.entity.*;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.CategoryErrorCode;
import com.course.course_be.exception.ChapterErrorCode;
import com.course.course_be.exception.CourseErrorCode;
import com.course.course_be.mapper.CourseMapper;
import com.course.course_be.mapper.LessonMapper;
import com.course.course_be.repository.CategoryRepository;
import com.course.course_be.repository.ChapterRepository;
import com.course.course_be.repository.CourseRepository;
import com.course.course_be.repository.LessonRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final CategoryRepository categoryRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final AuthenticationService authenticationService;

    @PreAuthorize("hasRole('ADMIN')")
    public Page<CourseResponse> getAll(String name, String detail, String status, Pageable pageable) {
        name = name == null ? "" : name;
        Page<Course> page;
        if (status == null || status.isEmpty()) {
            page = (detail != null && !detail.isEmpty()) ? courseRepository.findByNameContainingAndDetailContainingAndStatusNot(name, detail, "deleted", pageable)
                    : courseRepository.findByNameContainingAndStatusNot(name, "deleted", pageable);
        } else {
            List<String> statusList = Arrays.asList(status.split("\\."));
            page = (detail != null && !detail.isEmpty()) ? courseRepository.findByNameContainingAndDetailContainingAndStatusIn(name, detail, statusList, pageable)
                    : courseRepository.findByNameContainingAndStatusIn(name, statusList, pageable);
        }
        return page.map(courseMapper::toCourseResponse);
    }

    public CourseClientResponse getCourse(String id) {
        var context = SecurityContextHolder.getContext();
        String Id = context.getAuthentication().getName();
        System.out.println("id auth >>> " + Id);

        Course course = new Course();

        if (!Id.equals("anonymousUser")) {
            Account account = authenticationService.getMyAccountCurrent();
            if (account.getRole().equals("ADMIN")) {
                course = courseRepository.findByIdWithChaptersAndLessonsAdmin(id)
                        .orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
            } else {
                course = courseRepository.findByIdWithChaptersAndLessons(id)
                        .orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
            }
        } else {
            course = courseRepository.findByIdWithChaptersAndLessons(id)
                    .orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
        }






        course.setChapters(
                course.getChapters().stream()
                        .filter(ch -> "active".equals(ch.getStatus()))
                        .sorted(Comparator.comparing(Chapter::getChapterNumber))
                        .peek(ch -> ch.setLessons(
                                ch.getLessons().stream()
                                        .filter(lesson -> "active".equals(lesson.getStatus()))
                                        .sorted(Comparator.comparing(Lesson::getLessonNumber))
                                        .collect(Collectors.toCollection(LinkedHashSet::new)) // giữ thứ tự cho Set
                        ))
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );

        // Map sang response DTO
        CourseClientResponse response = courseMapper.toCourseClientResponse(course);

        // Tính tổng số bài học
        int totalLessons = course.getChapters().stream()
                .mapToInt(ch -> ch.getLessons() != null ? ch.getLessons().size() : 0)
                .sum();

        // Tính tổng thời lượng
        int totalDuration = course.getChapters().stream()
                .flatMap(ch -> ch.getLessons() != null ? ch.getLessons().stream() : Stream.empty())
                .filter(Objects::nonNull)
                .mapToInt(lesson -> lesson.getDuration() != null ? lesson.getDuration() : 0)
                .sum();

        response.setTotalLessons(totalLessons);
        response.setTotalDuration(totalDuration);

        return response;
    }

    public Page<CourseCardResponse> searchCoursesByName(String keyword, Pageable pageable) {
        Page<Course> coursesPage = courseRepository.findByNameContainingIgnoreCase(keyword, pageable);
        return coursesPage.map(courseMapper::toCourseCardResponse);
    }

    public Page<CourseCardResponse> filterCourse(String sort, String category, String keyword, Integer page, Integer size) {

        Page<Course> PageCourse = null;
        page = page == null ? 0 : page - 1;
        size = size == null ? 12 : size;
        Pageable pageable = PageRequest.of(page, size);
        if (keyword == null) {
            keyword = "";

        }
        if (sort != null && sort.equals("hot")) {
            PageCourse = courseRepository.searchByKeywordAndCategoryOrderByHot(keyword, category, pageable);
        } else if (sort != null && sort.equals("newest")) {
            PageCourse = courseRepository.searchByKeywordAndCategoryOrderByNewest(keyword, category, pageable);
        } else {
            PageCourse = courseRepository.searchByKeywordAndCategory(keyword, category, pageable);
        }



        return PageCourse.map(course -> {
            CourseCardResponse response = courseMapper.toCourseCardResponse(course);

            int totalLessons = courseRepository.countActiveLessonsByCourseId(course.getId());

            int totalDuration = courseRepository.sumDurationByCourseId(course.getId());

            response.setTotalLessons(totalLessons);
            response.setTotalDuration(totalDuration);

            return response;
        });

    }


    public List<CourseCardResponse> getHotCourse(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<CourseCardResponse> coursesHot = new ArrayList<>();
        List<Course> ListCourse = new ArrayList<Course>();
        ListCourse = courseRepository.getHotCourse(pageable);
        for (Course course : ListCourse) {
            coursesHot.add(courseMapper.toCourseCardResponse(course));
        }
        return coursesHot;
    }

    public List<CourseCardResponse> getNewestCourse(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<CourseCardResponse> coursesNewest = new ArrayList<>();
        List<Course> ListCourse = new ArrayList<Course>();
        ListCourse = courseRepository.getNewestCourse(pageable);
        for (Course course : ListCourse) {
            coursesNewest.add(courseMapper.toCourseCardResponse(course));
        }
        return coursesNewest;

    }

    @PreAuthorize("hasRole('ADMIN')")
    public CourseResponse createNew(CreateCourseRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        Course course = Course.builder()
                .name(request.getName())
                .detail(request.getDetail())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .videoUrl(request.getVideoUrl())
                .status("inactive")
                .createdAt(LocalDateTime.now())
                .category(category)
                .build();
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toCourseResponse(savedCourse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CourseResponse update(String id, UpdateCourseRequest request) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
        Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        course.setName(request.getName());
        course.setDetail(request.getDetail());
        course.setPrice(request.getPrice());
        course.setImageUrl(request.getImageUrl());
        course.setVideoUrl(request.getVideoUrl());
        course.setStatus(request.getStatus());
        course.setCategory(category);
        course.setUpdatedAt(LocalDateTime.now());
        return courseMapper.toCourseResponse(courseRepository.save(course));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
        course.setStatus("deleted");
        courseRepository.save(course);
    }

    public CountResponse countCourse() {
        return CountResponse.builder()
                .name("course")
                .value(courseRepository.countByStatus("active"))
                .total(courseRepository.countByStatusNot("deleted"))
                .build();

    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<LessonResponse> getAllLessonOfChapter(String name, String detail, String status, String courseId, int chapterId, Pageable pageable) {
        chapterRepository.findByIdAndCourseId(chapterId, courseId).orElseThrow(() -> new AppException(ChapterErrorCode.CHAPTER_NOT_FOUND));
        name = name == null ? "" : name;
        List<String> statusList = new ArrayList<>();
        if (status != null && !status.trim().isEmpty()) {
            statusList = Arrays.asList(status.split("\\."));
        }
        Page<Lesson> page = lessonRepository.searchLessons(name, detail, status, statusList, chapterId, pageable);
        return page.map(lessonMapper::toLessonResponse);
    }
}
