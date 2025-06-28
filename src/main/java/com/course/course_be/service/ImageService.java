package com.course.course_be.service;


import com.course.course_be.exception.AppException;
import com.course.course_be.exception.InValidErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.core.io.Resource;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageService {
    @NonFinal
    @Value("${image.image-path}")
    private String IMAGE_PATH;

    public String getPath () {
        return IMAGE_PATH;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String uploadImage(MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            String baseName = FilenameUtils.getBaseName(originalFileName);
            String extension = FilenameUtils.getExtension(originalFileName);

            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(IMAGE_PATH);
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Dùng timestamp để đảm bảo tên file duy nhất
            String fileName = System.currentTimeMillis() + "_" + baseName + "." + extension;
            Path filePath = uploadPath.resolve(fileName);

            // Lưu file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            throw new AppException(InValidErrorCode.JSON_INVALID);
        }
    }

    public Resource loadImageAsResource(String fileName) {
        try {
            Path filePath = Paths.get(IMAGE_PATH).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new AppException(InValidErrorCode.JSON_INVALID);
            }
        } catch (MalformedURLException e) {
            throw new AppException(InValidErrorCode.JSON_INVALID);
        }
    }


}
