package org.example.expert.domain.test;

import lombok.RequiredArgsConstructor;
import org.example.expert.util.S3Util;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UploadTestController {
    private final S3Util s3Util;

    @PostMapping("/api/test/upload")
    public String fileUpload(@RequestParam("files") MultipartFile multipartFile) throws IOException {
//        return s3Util.upload(multipartFile,"test");
        String keyName = s3Util.upload(multipartFile,"test");
        return "KEYNAME : " + keyName +"\n"+"URL : "+ s3Util.createPresignedUrl(keyName);
    }
}
