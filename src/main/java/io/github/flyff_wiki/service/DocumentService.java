package io.github.flyff_wiki.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.flyff_wiki.domain.ContentFormat;
import io.github.flyff_wiki.domain.Document;
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
