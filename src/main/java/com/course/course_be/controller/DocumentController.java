package com.course.course_be.controller;


import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.service.DocumentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor // autowire
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentController {

    DocumentService documentService;

    @PostMapping("/upload")
    public ApiResponse<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        System.out.println("file name >>> " + file.getOriginalFilename());
        String fileName = documentService.saveDocument(file);
        return ApiResponse.<String>builder()
                .result(fileName).
                build();
    }

}
