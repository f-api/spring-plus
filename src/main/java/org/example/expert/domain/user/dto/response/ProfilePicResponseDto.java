package org.example.expert.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfilePicResponseDto {
    String keyName;
    String presignedUrl;
}
