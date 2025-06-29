package com.course.course_be.controller;

import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.service.VideoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class VideoController {

    VideoService videoService;

    @PostMapping("/upload")
    public ApiResponse<String> uploadVideo(@RequestParam("file") MultipartFile file) {
    
        String fileName = videoService.saveVideo(file);
        return ApiResponse.<String>builder()
        .result(fileName).
        build();
    }

    @GetMapping("/stream/{fileName}")
    public void streamVideo(
            @PathVariable String fileName,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            HttpServletResponse response
    ) throws IOException {
        videoService.streamVideo(fileName, rangeHeader, response);
    }


    @GetMapping("/trailer/{fileName}")
    public void trailerVideo(
            @PathVariable String fileName,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            HttpServletResponse response
    ) throws IOException {

        videoService.streamTrailer(fileName, rangeHeader, response);
    }
}
