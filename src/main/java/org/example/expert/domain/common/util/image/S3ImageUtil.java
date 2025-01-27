package org.example.expert.domain.common.util.image;

import static org.example.expert.domain.common.exception.ExceptionType.SERVER_IMAGE_DELETE_FAILED;
import static org.example.expert.domain.common.exception.ExceptionType.SERVER_IMAGE_UPLOAD_FAILED;

import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Primary
@Component
@RequiredArgsConstructor
public class S3ImageUtil implements ImageUtil {

    private final S3Client s3Client;

    @Value("${image.s3.bucket}")
    private String bucketName;

    @Value("${image.s3.endpoint}")
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
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key("public/" + filename)
                .contentType(image.getContentType())
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(image.getBytes()));

            System.out.println(getImageUrl(filename));
            return filename;
        } catch (IOException e) {
            throw new CustomException(SERVER_IMAGE_UPLOAD_FAILED);
        }
    }

    @Override
    public void delete(String filename) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key("public/" + filename)
                .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new CustomException(SERVER_IMAGE_DELETE_FAILED);
        }
    }

    @Override
    public String getImageUrl(String filename) {
        return endpoint + "/" + filename;
    }
}
