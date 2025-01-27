package org.example.expert.domain.common.util.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUtil {

    String upload(MultipartFile image);

    void delete(String filename);

    String getImageUrl(String filename);
}
