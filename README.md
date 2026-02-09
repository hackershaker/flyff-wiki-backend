# 문제 및 해결: `latestHistory` 재귀 직렬화 문제

프로젝트에서 `Document` 엔티티의 `latestHistory` 필드가 `DocumentHistory` 객체들을 포함하고, 각 `DocumentHistory`가 다시 `Document`를 참조하는 양방향 관계 때문에 JSON 직렬화 시 재귀적으로 계속 반복되는 문제가 발생했습니다. 예를 들어 `updateDocument` 호출 후 반환된 JSON이 무한히 중첩되어 도구나 클라이언트가 결과를 잘라내기 전까지 계속 반복되는 현상이 있었습니다.

## 문제 (수정 전)

다음은 `updateDocument` 호출 시 반환된 JSON 예시(일부 생략):

```json
{
	"author": null,
	"content": "변경된 컨텐츠",
	"id": 1,
	"latestHistory": [
		{
			"changeDescription": "Document updated",
			"content": "내용 1",
			"document": {
				"author": null,
				"content": "변경된 컨텐츠",
				"id": 1,
				"latestHistory": [
					{
						"changeDescription": "Document updated",
						"content": "내용 1",
						"document": {
							"author": null,
							"content": "변경된 컨텐츠",
							"id": 1,
							"latestHistory": [ ... 반복 ... ]
						}
					}
				]
			}
		}
	]
}
```

### 왜 이런 문제가 발생했나?
- `Document`와 `DocumentHistory`는 JPA에서 양방향 관계입니다.
	- `Document`는 `@OneToMany`로 `latestHistory`를 가지고 있고,
	- `DocumentHistory`는 `@ManyToOne`으로 부모 `document`를 참조합니다.
- Spring Boot가 사용하는 Jackson은 기본적으로 객체 그래프를 따라가며 직렬화합니다. 따라서 부모 → 자식 → 부모 → 자식 식으로 계속 순회하면서 무한 중첩이 발생합니다.

## 원인 요약

양방향 연관관계에 대해 JSON 직렬화가 어떻게 처리되어야 할지(어떤 쪽을 직렬화하고 어떤 쪽을 무시할지)를 지정하지 않아서 Jackson이 양쪽을 모두 직렬화하려고 한 것이 문제의 핵심입니다.

## 적용한 해결 방법

여러 방법이 있지만 이 코드베이스에서는 Jackson의 managed/back reference를 사용해 해결했습니다.

1. 부모 측 (`Document.latestHistory`)에 `@JsonManagedReference` 추가
2. 자식 측 (`DocumentHistory.document`)에 `@JsonBackReference` 추가

이 두 어노테이션은 Jackson에게 "managed(관리)되는 쪽만 직렬화하고, back reference는 직렬화하지 마라"고 알려 재귀 직렬화를 방지합니다.

또한 별도 문제로 H2에서의 예약어 충돌을 피하기 위해 `User` 엔티티에 `@Table(name = "users")`를 추가해 테이블명을 명시했습니다.

## 변경 전 코드

- `Document.java` (변경 전 발췌):

```java
@OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
private List<DocumentHistory> latestHistory = new ArrayList<>();
```

- `DocumentHistory.java` (변경 전 발췌):

```java
@ManyToOne
@JoinColumn(name = "document_id")
private Document document;
```

## 변경 후 코드

- `Document.java` (변경 후 발췌):

```java
@OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
@JsonManagedReference
private List<DocumentHistory> latestHistory = new ArrayList<>();
```

- `DocumentHistory.java` (변경 후 발췌):

```java
@ManyToOne
@JoinColumn(name = "document_id")
@JsonBackReference
private Document document;
```

## 대안

- `@JsonIgnore`를 자식의 `document` 필드에 붙여 단순히 직렬화에서 제외할 수 있습니다.
- API 응답 전용 DTO를 만들어 직렬화할 필드를 명확히 관리하는 방법(DTO 사용 권장).
- H2 예약어 문제는 `@Table(name = "users")`처럼 테이블명을 바꾸거나 `spring.jpa.properties.hibernate.globally_quoted_identifiers=true`를 통해 회피할 수 있습니다.
