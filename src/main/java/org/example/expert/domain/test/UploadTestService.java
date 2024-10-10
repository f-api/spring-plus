package org.example.expert.domain.test;

import lombok.RequiredArgsConstructor;
import org.example.expert.util.S3Util;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadTestService {
    private final S3Util s3Util;


}
