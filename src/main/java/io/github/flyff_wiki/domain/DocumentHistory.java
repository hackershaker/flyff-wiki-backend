package io.github.flyff_wiki.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 문서 편집 이력을 저장하는 엔티티입니다.
 * - 참고사항: Document와 양방향 관계를 구성할 때 Document.addHistory를 사용하면 안전합니다.
 */
@Entity
@Table(name = "document_history")
@Getter
@Setter
public class DocumentHistory {
    /**
     * 히스토리 고유 ID (자동 증가).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 어떤 문서의 히스토리인지 나타내는 연관 관계.
     */
    @ManyToOne
    @JoinColumn(name = "document_id")
    @JsonBackReference
    private Document document;

    /**
     * 편집을 수행한 사용자 (없으면 null 허용).
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User editor;

    /**
     * 편집 이전 혹은 이후의 본문 스냅샷 (서비스 정책에 따름).
     */
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    /**
     * 편집 시각.
     */
    private LocalDateTime editedAt;

    /**
     * 변경 요약 설명.
     */
    private String changeDescription;
}
