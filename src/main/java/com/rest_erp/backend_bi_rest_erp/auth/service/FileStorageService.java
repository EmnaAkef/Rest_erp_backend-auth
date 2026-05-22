package com.rest_erp.backend_bi_rest_erp.auth.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath = Paths.get("uploads/users");

    private final Set<String> allowedExtensions = Set.of(".jpg", ".jpeg", ".png", ".webp");

    public String saveUserPhoto(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }

            String originalFileName = file.getOriginalFilename();

            if (originalFileName == null || !originalFileName.contains(".")) {
                throw new RuntimeException("Invalid image file");
            }

            String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();

            if (!allowedExtensions.contains(extension)) {
                throw new RuntimeException("Only JPG, JPEG, PNG and WEBP files are allowed");
            }

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/users/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Could not save user photo", e);
        }
    }
}
