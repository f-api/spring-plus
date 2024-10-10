package org.example.expert.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Util {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷

    // S3 파일 업로드
    public String upload(MultipartFile multipartFile) throws IOException {
        // MultipartFile -> File
        File convertFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("file convert error")); // 파일을 변환할 수 없으면 에러

        // S3에 저장할 파일명
        String fileName = "profilePic/" + UUID.randomUUID() + "_" + convertFile.getName();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        // S3에 파일 업로드
        s3Client.putObject(putObjectRequest,convertFile.toPath());

        // 로컬 파일 삭제
        convertFile.delete();

        return fileName;
    }

    // 파일 convert 후 로컬에 업로드
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/upload" + file.getOriginalFilename());
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    //Public에서 사진 볼 수 있게 미리 서명된 URL 만들어서 반환
    public String createPresignedUrl(String keyName) {
        try (S3Presigner presigner = S3Presigner.create()) {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(keyName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toExternalForm();
        }
    }
}
