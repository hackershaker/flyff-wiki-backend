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
import io.github.flyff_wiki.domain.DocumentHistory;
import io.github.flyff_wiki.repository.DocumentRepository;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentService documentService;

    @Test
    public void testCreateDocument() {
        // 테스트 목적: 문서 생성 시 기본 포맷(MARKDOWN)과 기본 필드 설정이 적용되는지 검증합니다.
        // 기대 동작 흐름: 입력 문서 -> 서비스가 createdAt/updatedAt/기본 포맷 세팅 -> 저장 결과 반환
        // Given: 테스트 입력 문서를 구성합니다.
        // - 제목/내용만 설정하고 contentFormat은 비워 기본값(MARKDOWN) 설정 흐름을 타게 합니다.
        // - repository.save가 호출될 때 동일 객체가 반환되도록 스텁을 준비합니다.
        Document document = new Document();
        document.setTitle("Test Title");
        document.setContent("Test Content");

        when(documentRepository.save(any(Document.class))).thenReturn(document);

        // When: 서비스의 createDocument를 호출하여 생성 흐름을 실행합니다.
        // - 내부적으로 createdAt/updatedAt 설정 및 기본 포맷 지정이 수행되어야 합니다.
        Document result = documentService.createDocument(document);

        // Then: 생성 결과가 null이 아니고, 기본 포맷이 자동 지정되며 입력 값이 유지되는지 확인합니다.
        // - 기본 포맷: MARKDOWN
        // - 제목: "Test Title"
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Title");
        assertThat(result.getContentFormat()).isEqualTo(ContentFormat.MARKDOWN); // 기본값 설정 확인
    }

    @Test
    public void testGetDocument() {
        // 테스트 목적: ID로 문서를 조회할 때 올바른 문서가 반환되는지 검증합니다.
        // 기대 동작 흐름: repository.findById -> 존재하는 문서 반환 -> 서비스 반환
        // Given: repository에서 반환될 문서를 준비합니다.
        // - findById가 Optional.of(document)를 반환하도록 스텁합니다.
        // - 조회 대상 ID는 1L로 고정합니다.
        Document document = new Document();
        document.setId(1L);
        document.setTitle("Test Title");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        // When: 서비스의 getDocument를 호출하여 조회 흐름을 실행합니다.
        // - 내부적으로 repository.findById가 호출되어야 합니다.
        Document result = documentService.getDocument(1L);

        // Then: 반환된 문서가 null이 아니고, 기대한 ID를 가지는지 확인합니다.
        // - ID: 1L
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    public void testUpdateDocumentAddsHistory() {
        // 테스트 목적: 문서 업데이트 시 히스토리가 추가되는지 검증합니다.
        // 기대 동작 흐름: 기존 문서 로드 -> 업데이트 전 본문을 히스토리로 저장 -> 변경 내용 반영 -> 저장
        // Given: 기존 문서와 업데이트 요청 문서를 준비합니다.
        // - 기존 문서는 "Old Content"를 포함하도록 설정합니다.
        // - 업데이트 문서는 "New Content"로 변경되도록 설정합니다.
        // - findById는 기존 문서를 반환하도록 스텁합니다.
        // - save는 전달된 엔티티를 그대로 반환하도록 스텁합니다.
        Document existing = new Document();
        existing.setId(1L);
        existing.setTitle("Old Title");
        existing.setContent("Old Content");
        existing.setContentFormat(ContentFormat.MARKDOWN);

        Document updated = new Document();
        updated.setTitle("New Title");
        updated.setContent("New Content");
        updated.setContentFormat(ContentFormat.HTML);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(existing));
        // 저장 결과를 그대로 반환하도록 설정하여 서비스 결과를 검증합니다.
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: 서비스의 updateDocument를 호출하여 업데이트 흐름을 실행합니다.
        // - 업데이트 전에 기존 본문이 히스토리에 저장되어야 합니다.
        Document result = documentService.updateDocument(1L, updated);

        // Then: 업데이트 결과와 히스토리 저장 결과를 모두 검증합니다.
        // - 문서 본문/제목은 새 값으로 변경되어야 합니다.
        // - 히스토리는 1건 추가되어야 하며, 내용은 "Old Content"이어야 합니다.
        // - 히스토리의 document 참조는 업데이트된 문서여야 합니다.
        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getContent()).isEqualTo("New Content");
        assertThat(result.getLatestHistory()).hasSize(1);
        DocumentHistory history = result.getLatestHistory().get(0);
        assertThat(history.getContent()).isEqualTo("Old Content");
        assertThat(history.getDocument()).isEqualTo(result);
    }
}
