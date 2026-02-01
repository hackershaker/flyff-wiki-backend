package io.github.flyff_wiki.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.flyff_wiki.domain.Document;
import io.github.flyff_wiki.service.DocumentService;

@RestController
@RequestMapping("/api/v1")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentService documentService;

    @PostMapping("/document")
    public Document writeDocument(@RequestBody Document document) {
        logger.info("Received request to create document: {}", document.getTitle());
        Document result = documentService.createDocument(document);
        logger.info("Document created successfully with ID: {}", result.getId());
        return result;
    }

    @PostMapping("/document/{id}")
    public Document updateDocument(@PathVariable Long id, @RequestBody Document document) {
        logger.info("Received request to update document with ID: {}", id);
        Document result = documentService.updateDocument(id, document);
        logger.info("Document updated successfully with ID: {}", id);
        return result;
    }

    @GetMapping("/document/{id}")
    public Document getDocument(@PathVariable Long id) {
        logger.info("Received request to get document with ID: {}", id);
        Document result = documentService.getDocument(id);
        logger.debug("Returning document: {}", result.getTitle());
        return result;
    }
}
