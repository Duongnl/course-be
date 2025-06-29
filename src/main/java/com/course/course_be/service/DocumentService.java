package com.course.course_be.service;


import com.course.course_be.entity.Account;
import com.course.course_be.exception.AppException;
import com.course.course_be.exception.InValidErrorCode;
import com.course.course_be.repository.LessonRepository;
import com.course.course_be.repository.SubmissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentService {

    @NonFinal
    @Value("${document.document-path}")
    private String DOCUMENT_PATH;

    SubmissionRepository submissionRepository;
    AuthenticationService authenticationService;
    LessonRepository lessonRepository;

    public String saveDocument(MultipartFile file) {
        try {
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            originalFileName = new String(originalFileName.getBytes("ISO-8859-1"), StandardCharsets.UTF_8);
            String baseName = FilenameUtils.getBaseName(originalFileName);
            String extension = FilenameUtils.getExtension(originalFileName);

            // Dùng path được cấu hình
            Path storageDir = Paths.get(DOCUMENT_PATH).toAbsolutePath().normalize();
            Files.createDirectories(storageDir);

            String finalFileName = baseName + "." + extension;
            Path targetPath = storageDir.resolve(finalFileName);
            int counter = 1;
            while (Files.exists(targetPath)) {
                finalFileName = baseName + "_" + counter + "." + extension;
                targetPath = storageDir.resolve(finalFileName);
                counter++;
            }

            Files.copy(file.getInputStream(), targetPath);
            return finalFileName;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu tài liệu: " + e.getMessage(), e);
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    public Resource loadFileAsResource(String fileName) {
        try {
            if (fileName.contains("..")) {
                throw new AppException(InValidErrorCode.JSON_INVALID);
            }

            Path filePath = Paths.get(DOCUMENT_PATH).toAbsolutePath().normalize().resolve(fileName);
            if (!Files.exists(filePath)) {
                throw new AppException(InValidErrorCode.JSON_INVALID);
            }

            return new UrlResource(filePath.toUri());
        } catch (Exception e) {
            throw new AppException(InValidErrorCode.JSON_INVALID);
        }
    }


    public Resource loadDocumentAsResource(String fileName) {
        Account account = authenticationService.getMyAccountCurrent();

        if (!lessonRepository.existsAuthorizedLessonByDocumentUrl(fileName, account.getId() )) {
            throw new AppException(InValidErrorCode.JSON_INVALID);
        }


        try {
            if (fileName.contains("..")) {
                throw new AppException(InValidErrorCode.JSON_INVALID);
            }

            Path filePath = Paths.get(DOCUMENT_PATH).toAbsolutePath().normalize().resolve(fileName);
            if (!Files.exists(filePath)) {
                throw new AppException(InValidErrorCode.JSON_INVALID);
            }

            return new UrlResource(filePath.toUri());
        } catch (Exception e) {
            throw new AppException(InValidErrorCode.JSON_INVALID);
        }
    }


    public Resource loadSubmissionAsResource(String fileName) {
        Account account = authenticationService.getMyAccountCurrent();

            if (!submissionRepository.existsByAccountAndSubmissionUrlAndStatusNot(account.getId(), fileName,"deleted")) {
                throw new AppException(InValidErrorCode.JSON_INVALID);
            }


        try {
            if (fileName.contains("..")) {
                throw new AppException(InValidErrorCode.JSON_INVALID);
            }

            Path filePath = Paths.get(DOCUMENT_PATH).toAbsolutePath().normalize().resolve(fileName);
            if (!Files.exists(filePath)) {
                throw new AppException(InValidErrorCode.JSON_INVALID);
            }

            return new UrlResource(filePath.toUri());
        } catch (Exception e) {
            throw new AppException(InValidErrorCode.JSON_INVALID);
        }
    }


    public Resource loadAssignmentAsResource(String fileName) {
        Account account = authenticationService.getMyAccountCurrent();

            if (!lessonRepository.existsAuthorizedLessonByAssignmentUrl(fileName, account.getId() )) {
                throw new AppException(InValidErrorCode.JSON_INVALID);
            }


        try {
            if (fileName.contains("..")) {
                throw new AppException(InValidErrorCode.JSON_INVALID);
            }

            Path filePath = Paths.get(DOCUMENT_PATH).toAbsolutePath().normalize().resolve(fileName);
            if (!Files.exists(filePath)) {
                throw new AppException(InValidErrorCode.JSON_INVALID);
            }

            return new UrlResource(filePath.toUri());
        } catch (Exception e) {
            throw new AppException(InValidErrorCode.JSON_INVALID);
        }
    }


}
