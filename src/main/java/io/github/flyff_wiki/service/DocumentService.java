package io.github.flyff_wiki.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.flyff_wiki.domain.ContentFormat;
import io.github.flyff_wiki.domain.Document;
import io.github.flyff_wiki.domain.DocumentHistory;
import io.github.flyff_wiki.repository.DocumentRepository;

@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    private DocumentRepository documentRepository;

    public Document createDocument(Document document) {
        logger.info("Creating new document with title: {}", document.getTitle());
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        if (document.getContentFormat() == null) {
            document.setContentFormat(ContentFormat.MARKDOWN); // 기본 포맷
            logger.debug("Setting default content format to MARKDOWN");
        }
        Document saved = documentRepository.save(document);
        logger.info("Document created with ID: {}", saved.getId());
        return saved;
    }

    public Document updateDocument(Long id, Document updatedDocument) {
        logger.info("Updating document with ID: {}", id);
        Optional<Document> existing = documentRepository.findById(id);
        if (existing.isPresent()) {
            Document doc = existing.get();
            // 히스토리 생성: 업데이트 이전 내용을 스냅샷으로 저장합니다.
            // - 인자: updatedDocument (새로 들어온 문서 정보)
            // - 리턴값: 없음 (히스토리는 doc에 추가됨)
            // - 동작 흐름: 기존 본문 스냅샷 -> 편집자/시각 설정 -> doc.addHistory 호출
            // - 주의사항: 히스토리는 "업데이트 전" 내용을 저장하는 정책입니다.
            DocumentHistory history = new DocumentHistory();
            history.setEditor(updatedDocument.getAuthor());
            history.setContent(doc.getContent());
            history.setEditedAt(LocalDateTime.now());
            history.setChangeDescription("Document updated");
            doc.addHistory(history);

            doc.setTitle(updatedDocument.getTitle());
            doc.setContent(updatedDocument.getContent());
            doc.setContentFormat(updatedDocument.getContentFormat());
            doc.setAuthor(updatedDocument.getAuthor());
            doc.setUpdatedAt(LocalDateTime.now());
            Document saved = documentRepository.save(doc);
            logger.info("Document updated successfully with ID: {}", id);
            return saved;
        }
        logger.error("Document not found with ID: {}", id);
        throw new RuntimeException("Document not found");
    }

    public Document getDocument(Long id) {
        logger.info("Retrieving document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Document not found with ID: {}", id);
                    return new RuntimeException("Document not found");
                });
        logger.debug("Document retrieved: {}", document.getTitle());
        return document;
    }
}
