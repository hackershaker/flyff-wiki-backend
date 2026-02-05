package io.github.flyff_wiki.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import io.github.flyff_wiki.domain.ContentFormat;
import io.github.flyff_wiki.domain.Document;
import io.github.flyff_wiki.service.DocumentService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(DocumentController.class)
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateDocument() throws Exception {
        // 테스트 목적: 문서 생성 API가 정상적으로 200 OK와 기대 JSON을 반환하는지 검증합니다.
        // 기대 동작 흐름: 요청 JSON -> 컨트롤러 -> 서비스 호출 -> 응답 JSON 매핑
        // Given: 컨트롤러가 반환해야 할 문서 객체를 준비합니다.
        // - 서비스가 호출되면 해당 객체를 반환하도록 스텁합니다.
        // - 요청/응답 JSON 매핑 검증을 위해 id, title, content를 채워둡니다.
        Document document = new Document();
        document.setId(1L);
        document.setTitle("Test Title");
        document.setContent("Test Content");
        document.setContentFormat(ContentFormat.MARKDOWN);

        when(documentService.createDocument(any(Document.class))).thenReturn(document);

        // When & Then: POST 요청을 수행하고 응답을 검증합니다.
        // - 상태 코드는 200 OK여야 합니다.
        // - 응답 JSON의 id, title이 기대값과 일치해야 합니다.
        mockMvc.perform(post("/api/v1/document")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(document)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    public void testGetDocument() throws Exception {
        // 테스트 목적: 문서 조회 API가 정상적으로 200 OK와 기대 JSON을 반환하는지 검증합니다.
        // 기대 동작 흐름: 경로 변수 ID -> 컨트롤러 -> 서비스 호출 -> 응답 JSON 매핑
        // Given: 조회 요청에 대해 반환될 문서 객체를 준비합니다.
        // - 서비스가 getDocument(1L) 호출 시 해당 객체를 반환하도록 스텁합니다.
        // - 응답 JSON의 필드 매핑을 검증하기 위해 id, title을 설정합니다.
        Document document = new Document();
        document.setId(1L);
        document.setTitle("Test Title");

        when(documentService.getDocument(1L)).thenReturn(document);

        // When & Then: GET 요청을 수행하고 응답을 검증합니다.
        // - 상태 코드는 200 OK여야 합니다.
        // - 응답 JSON의 id, title이 기대값과 일치해야 합니다.
        mockMvc.perform(get("/api/v1/document/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"));
    }
}
