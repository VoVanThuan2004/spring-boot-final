package com.example.final_project.util;

import com.example.final_project.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
public class ImageValidator {
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return; // không bắt buộc phải có ảnh
        }

        // 1. Kích thước
        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException("Image size must not exceed 5MB");
        }

        // 2. Content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only JPEG, PNG, GIF, WEBP images are allowed");
        }

        // 3. Extension (đề phòng content-type bị giả)
        String originalName = file.getOriginalFilename();
        if (originalName == null || !hasAllowedExtension(originalName)) {
            throw new BadRequestException("Invalid image file extension");
        }

        // 4. (Tuỳ chọn, khuyến nghị) kiểm tra magic bytes để chắc chắn là ảnh thật
        if (!isRealImage(file)) {
            throw new BadRequestException("File is not a valid image");
        }
    }

    private boolean hasAllowedExtension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) return false;
        return ALLOWED_EXTENSIONS.contains(name.substring(dot + 1).toLowerCase());
    }

    private boolean isRealImage(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            byte[] header = in.readNBytes(12);
            // JPEG: FF D8 FF
            // PNG : 89 50 4E 47
            // GIF : 47 49 46 38
            // WEBP: "RIFF"...."WEBP"
            if (header.length >= 3
                    && (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8) return true;
            if (header.length >= 8
                    && (header[0] & 0xFF) == 0x89 && header[1] == 'P'
                    && header[2] == 'N' && header[3] == 'G') return true;
            if (header.length >= 4
                    && header[0] == 'G' && header[1] == 'I' && header[2] == 'F') return true;
            if (header.length >= 12
                    && header[0] == 'R' && header[1] == 'I'
                    && header[2] == 'F' && header[3] == 'F'
                    && header[8] == 'W' && header[9] == 'E'
                    && header[10] == 'B' && header[11] == 'P') return true;
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
