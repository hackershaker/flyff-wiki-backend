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
        // 테스트 목적: JPA 리포지토리가 문서를 저장하고 조회할 수 있는지 검증합니다.
        // 기대 동작 흐름: save -> ID 생성 -> findById로 조회 성공
        // Given: 저장할 문서 엔티티를 준비합니다.
        // - title/content/contentFormat을 설정하여 필수 값 누락을 방지합니다.
        // - 저장 이후 ID가 생성되는지 확인할 수 있도록 초기 ID는 비워둡니다.
        Document document = new Document();
        document.setTitle("Test Title");
        document.setContent("Test Content");
        document.setContentFormat(ContentFormat.MARKDOWN);

        // When: JPA 리포지토리의 save를 호출하여 영속화합니다.
        // - 저장 후 반환된 엔티티에 ID가 할당되어야 합니다.
        Document saved = documentRepository.save(document);

        // Then: 저장 결과와 조회 결과를 모두 검증합니다.
        // - saved.id가 null이 아니어야 합니다.
        // - findById(saved.id)가 존재해야 합니다.
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Title");
        assertThat(documentRepository.findById(saved.getId())).isPresent();
    }
}
