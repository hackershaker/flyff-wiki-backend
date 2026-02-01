package io.github.flyff_wiki.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import io.github.flyff_wiki.domain.ContentFormat;
import io.github.flyff_wiki.domain.Document;

@DataJpaTest
public class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Test
    public void testSaveAndFindDocument() {
        // Given
        Document document = new Document();
        document.setTitle("Test Title");
        document.setContent("Test Content");
        document.setContentFormat(ContentFormat.MARKDOWN);

        // When
        Document saved = documentRepository.save(document);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Title");
        assertThat(documentRepository.findById(saved.getId())).isPresent();
    }
}