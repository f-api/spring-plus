package org.example.expert.domain.Attachment.dto;

import lombok.Getter;
import org.example.expert.domain.Attachment.entity.Attachment;

@Getter
public class AttachmentResponseDto {
    private Long id;
    private String url;

    public AttachmentResponseDto(Attachment attachment) {
        this.id = attachment.getId();
        this.url = attachment.getUrl();
    }
}
