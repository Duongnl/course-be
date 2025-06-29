package com.course.course_be.service;


import com.course.course_be.entity.Account;
import com.course.course_be.repository.LessonRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoService {

    @NonFinal
    @Value("${video.storage-path}")
    private String STORAGE_PATH;

    AuthenticationService authenticationService;

    LessonRepository lessonRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public String saveVideo(MultipartFile file) {
        try {
            // Làm sạch tên file
            String originalFileName = StringUtils.cleanPath(
                    Objects.requireNonNull(file.getOriginalFilename())
            );
            originalFileName = new String(originalFileName.getBytes("ISO-8859-1"), StandardCharsets.UTF_8);

            // Tách tên gốc và phần mở rộng
            String baseName = originalFileName;
            String extension = "";

            int dotIndex = originalFileName.lastIndexOf(".");
            if (dotIndex != -1) {
                baseName = originalFileName.substring(0, dotIndex);
                extension = originalFileName.substring(dotIndex); // bao gồm dấu chấm
            }

            // Tạo thư mục nếu chưa tồn tại
            Path storageDir = Paths.get(STORAGE_PATH).toAbsolutePath().normalize();
            Files.createDirectories(storageDir);

            // Tạo tên file duy nhất: baseName_UUID.extension
            String uuid = UUID.randomUUID().toString();
            String finalFileName = baseName + "_" + uuid + extension;
            Path targetPath = storageDir.resolve(finalFileName);

            // Ghi file vào thư mục
            Files.copy(file.getInputStream(), targetPath);

            return finalFileName;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu video: " + e.getMessage(), e);
        }
    }




    private static final long CHUNK_SIZE = 1024 * 1024L; // 1MB mỗi chunk

    public void streamVideo(String fileName, String rangeHeader, HttpServletResponse response) throws IOException {
        Account account = authenticationService.getMyAccountCurrent();
        System.out.println("file name >>> " + fileName);
        System.out.println("account.getId() >>> " + account.getId());

        if (account.getRole().equals("CLIENT")) {
            if (!lessonRepository.existsAuthorizedLessonByVideoUrl(fileName, account.getId())) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        Path videoPath = Paths.get(STORAGE_PATH).resolve(fileName).normalize();
   


        if (!Files.exists(videoPath)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long fileSize = Files.size(videoPath);
        long start = 0;
        long end = fileSize - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            start = Long.parseLong(ranges[0]);
            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                end = Long.parseLong(ranges[1]);
            } else {
                end = Math.min(start + CHUNK_SIZE - 1, fileSize - 1);
            }
        }

        long contentLength = end - start + 1;

        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setHeader("Content-Type", Files.probeContentType(videoPath));
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Length", String.valueOf(contentLength));
        response.setHeader("Content-Range", String.format("bytes %d-%d/%d", start, end, fileSize));

        try (InputStream inputStream = Files.newInputStream(videoPath)) {
            inputStream.skip(start);
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[8192];
            long bytesLeft = contentLength;
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1 && bytesLeft > 0) {
                if (bytesRead > bytesLeft) {
                    bytesRead = (int) bytesLeft;
                }
                outputStream.write(buffer, 0, bytesRead);
                bytesLeft -= bytesRead;
            }
            outputStream.flush();
        }

    }


    public void streamTrailer(String fileName, String rangeHeader, HttpServletResponse response) throws IOException {

        Path videoPath = Paths.get(STORAGE_PATH).resolve(fileName).normalize();

        if (!Files.exists(videoPath)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long fileSize = Files.size(videoPath);
        long start = 0;
        long end = fileSize - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            start = Long.parseLong(ranges[0]);
            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                end = Long.parseLong(ranges[1]);
            } else {
                end = Math.min(start + CHUNK_SIZE - 1, fileSize - 1);
            }
        }

        long contentLength = end - start + 1;

        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setHeader("Content-Type", Files.probeContentType(videoPath));
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Length", String.valueOf(contentLength));
        response.setHeader("Content-Range", String.format("bytes %d-%d/%d", start, end, fileSize));

        try (InputStream inputStream = Files.newInputStream(videoPath)) {
            inputStream.skip(start);
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[8192];
            long bytesLeft = contentLength;
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1 && bytesLeft > 0) {
                if (bytesRead > bytesLeft) {
                    bytesRead = (int) bytesLeft;
                }
                outputStream.write(buffer, 0, bytesRead);
                bytesLeft -= bytesRead;
            }
            outputStream.flush();
        }

    }

}
