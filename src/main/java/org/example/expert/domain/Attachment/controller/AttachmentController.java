package org.example.expert.domain.Attachment.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.Attachment.dto.AttachmentResponseDto;
import org.example.expert.domain.Attachment.service.AttachmentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;
    @PostMapping
    public ResponseEntity<ResponseDto<List<AttachmentResponseDto>>> uploadAttachments(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestPart List<MultipartFile> files
    ) throws IOException {
        List<AttachmentResponseDto> responseDto = attachmentService.uploadAttachments(authUser, files);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.of(HttpStatus.CREATED, "파일 업로드가 완료되었습니다.", responseDto));
    }

    @DeleteMapping("/{attachment_id}")
    public ResponseEntity<ResponseDto<String>> deleteAttachment(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable("attachment_id") Long attachmentId
    ) {
        attachmentService.deleteAttachment(authUser, attachmentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.of(200, "성공적으로 삭제되었습니다."));
    }



}
