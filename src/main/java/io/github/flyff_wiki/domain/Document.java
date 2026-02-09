package io.github.flyff_wiki.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private ContentFormat contentFormat;

    @ManyToOne
    private User author;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DocumentHistory> latestHistory = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 문서에 히스토리를 추가하고 양방향 연관관계를 동기화합니다.
     * - 인자: history (추가할 히스토리 엔티티)
     * - 리턴값: 없음 (void)
     * - 동작 흐름: null 리스트 방지 -> history의 document 설정 -> 리스트에 추가
     * - 주의사항: history가 null이면 호출자가 null 체크를 해야 합니다.
     * - 사용 예시: document.addHistory(history);
     */
    public void addHistory(DocumentHistory history) {
        if (latestHistory == null) {
            latestHistory = new ArrayList<>();
        }
        history.setDocument(this);
        latestHistory.add(history);
    }
}
