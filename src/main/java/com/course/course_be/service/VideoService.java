package com.course.course_be.service;


import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VideoService {

    @NonFinal
    @Value("${video.storage-path}")
    private String STORAGE_PATH;


    public String saveVideo(MultipartFile file) {
        try {
            // Làm sạch tên file
            String originalFileName = StringUtils.cleanPath(
                    Objects.requireNonNull(file.getOriginalFilename())
            );

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

            // Tạo tên file mới nếu trùng
            String finalFileName = baseName + extension;
            Path targetPath = storageDir.resolve(finalFileName);
            int counter = 1;

            while (Files.exists(targetPath)) {
                finalFileName = baseName + "_" + counter + extension;
                targetPath = storageDir.resolve(finalFileName);
                counter++;
            }

            // Ghi file
            Files.copy(file.getInputStream(), targetPath);

            System.out.println("Đã lưu file: " + finalFileName);
            return finalFileName;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu video: " + e.getMessage(), e);
        }
    }



    private static final long CHUNK_SIZE = 1024 * 1024L; // 1MB mỗi chunk

    public void streamVideo(String fileName, String rangeHeader, HttpServletResponse response) throws IOException {
        Path videoPath = Paths.get(STORAGE_PATH).resolve(fileName).normalize();
        System.out.println("Serving video file from path: " + videoPath.toAbsolutePath());


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

        System.out.println("Sending video bytes: start = " + start + ", end = " + end + ", total = " + fileSize);

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
