package io.github.flyff_wiki.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.flyff_wiki.domain.ContentFormat;
import io.github.flyff_wiki.domain.Document;
import io.github.flyff_wiki.repository.DocumentRepository;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentService documentService;

    @Test
    public void testCreateDocument() {
        // Given
        Document document = new Document();
        document.setTitle("Test Title");
        document.setContent("Test Content");

        when(documentRepository.save(any(Document.class))).thenReturn(document);

        // When
        Document result = documentService.createDocument(document);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Title");
        assertThat(result.getContentFormat()).isEqualTo(ContentFormat.MARKDOWN); // 기본값 설정 확인
    }

    @Test
    public void testGetDocument() {
        // Given
        Document document = new Document();
        document.setId(1L);
        document.setTitle("Test Title");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        // When
        Document result = documentService.getDocument(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }
}