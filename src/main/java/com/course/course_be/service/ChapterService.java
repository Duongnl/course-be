package com.course.course_be.service;

import com.course.course_be.dto.request.chapter.CreateChapterRequest;
import com.course.course_be.dto.request.chapter.UpdateChapterRequest;
import com.course.course_be.dto.response.chapter.ChapterResponse;
import com.course.course_be.entity.Chapter;
import com.course.course_be.entity.Course;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.ChapterErrorCode;
import com.course.course_be.exception.CourseErrorCode;
import com.course.course_be.mapper.ChapterMapper;
import com.course.course_be.repository.ChapterRepository;
import com.course.course_be.repository.CourseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterService {
    ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    ChapterMapper chapterMapper;

    public Page<ChapterResponse> getAll(String name, String detail, String status,String courseId, Pageable pageable) {
        courseRepository.findById(courseId).orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
        name = name == null ? "" : name;
        List<String> statusList = new ArrayList<>();
        if (status != null && !status.trim().isEmpty()) {
            statusList = Arrays.asList(status.split("\\."));
        }
        Page<Chapter> page = chapterRepository.searchChapters(name, detail, status, statusList, courseId, pageable);
        return page.map(chapterMapper::toChapterResponse);
    }

    public ChapterResponse createNew(CreateChapterRequest request) {
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(() -> new AppException(CourseErrorCode.COURSE_NOT_FOUND));
        Chapter chapter = Chapter.builder()
                .chapterNumber(request.getChapterNumber())
                .name(request.getName())
                .detail(request.getDetail())
                .course(course)
                .status("active")
                .createdAt(LocalDateTime.now()).build();
        Chapter savedChapter = chapterRepository.save(chapter);
        return chapterMapper.toChapterResponse(savedChapter);
    }

    public ChapterResponse update(int id, UpdateChapterRequest request) {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new AppException(ChapterErrorCode.CHAPTER_NOT_FOUND));
        chapter.setName(request.getName());
        chapter.setChapterNumber(request.getChapterNumber());
        chapter.setDetail(request.getDetail());
        chapter.setStatus(request.getStatus());
        return chapterMapper.toChapterResponse(chapterRepository.save(chapter));
    }

    public void deleteChapter(int id) {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new AppException(ChapterErrorCode.CHAPTER_NOT_FOUND));
        chapter.setStatus("deleted");
        chapterRepository.save(chapter);
    }
}
