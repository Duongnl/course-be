package com.course.course_be.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentService {

    public String saveDocument(MultipartFile file) {
        try {
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String baseName = FilenameUtils.getBaseName(originalFileName);
            String extension = FilenameUtils.getExtension(originalFileName);

            Path storageDir = Paths.get("documents").toAbsolutePath().normalize();
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



}
