package org.example.expert.domain.Attachment.repository;

import org.example.expert.domain.Attachment.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
