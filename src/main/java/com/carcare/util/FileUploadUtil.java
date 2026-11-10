package com.carcare.util;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Tiện ích xử lý tải lên hình ảnh tiếp nhận xe (Sprint 5)
 */
public class FileUploadUtil {

    public static String saveFile(Part part, String uploadPath) throws IOException {
        String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = fileName.substring(dotIndex);
        }

        String uniqueFileName = UUID.randomUUID().toString() + extension;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        part.write(uploadPath + File.separator + uniqueFileName);
        return uniqueFileName;
    }
}
