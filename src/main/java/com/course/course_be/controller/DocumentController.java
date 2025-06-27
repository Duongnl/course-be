package com.course.course_be.controller;


import com.course.course_be.dto.response.ApiResponse;
import com.course.course_be.service.DocumentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.nio.file.Files;

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

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String fileName) {

        Resource resource = documentService.loadDocumentAsResource(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
