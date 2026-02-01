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
        // Given
        Document document = new Document();
        document.setId(1L);
        document.setTitle("Test Title");
        document.setContent("Test Content");
        document.setContentFormat(ContentFormat.MARKDOWN);

        when(documentService.createDocument(any(Document.class))).thenReturn(document);

        // When & Then
        mockMvc.perform(post("/api/v1/document")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(document)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    public void testGetDocument() throws Exception {
        // Given
        Document document = new Document();
        document.setId(1L);
        document.setTitle("Test Title");

        when(documentService.getDocument(1L)).thenReturn(document);

        // When & Then
        mockMvc.perform(get("/api/v1/document/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"));
    }
}