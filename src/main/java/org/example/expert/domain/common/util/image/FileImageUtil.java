package org.example.expert.domain.common.util.image;

import static org.example.expert.domain.common.exception.ExceptionType.SERVER_IMAGE_DELETE_FAILED;
import static org.example.expert.domain.common.exception.ExceptionType.SERVER_IMAGE_UPLOAD_FAILED;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import org.example.expert.domain.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileImageUtil implements ImageUtil {

    @Value("${image.file.path}")
    private String uploadPath;

    @Value("${image.file.endpoint}")
    private String endpoint;

    @Override
    public String upload(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }

        String originalFileName = image.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String filename = UUID.randomUUID() + extension;

        try {
            String filePath = uploadPath + "/" + filename;
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Files.write(Paths.get(filePath), image.getBytes());
            return filename;
        } catch (IOException e) {
            throw new CustomException(SERVER_IMAGE_UPLOAD_FAILED);
        }
    }

    @Override
    public void delete(String filename) {
        try {
            File file = new File(uploadPath + "/" + filename);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            throw new CustomException(SERVER_IMAGE_DELETE_FAILED);
        }
    }

    @Override
    public String getImageUrl(String filename) {
        return endpoint + "/" + filename;
    }
}
