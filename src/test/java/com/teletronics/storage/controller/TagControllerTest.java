package com.teletronics.storage.controller;

import com.teletronics.storage.model.Tag;
import com.teletronics.storage.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TagControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
    }

    @Test
    void getAllTags_shouldReturnTagList() throws Exception {
        Tag tag1 = new Tag();
        tag1.setId("1");
        tag1.setName("tag1");

        Tag tag2 = new Tag();
        tag2.setId("2");
        tag2.setName("tag2");

        when(tagService.getAllTags()).thenReturn(Arrays.asList(tag1, tag2));

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("tag1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("tag2"));

        verify(tagService, times(1)).getAllTags();
    }

    @Test
    void createTag_shouldCreateTagSuccessfully() throws Exception {
        Tag tag = new Tag();
        tag.setId("1");
        tag.setName("tag1");

        when(tagService.createTag(anyString())).thenReturn(tag);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("tag1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("tag1"));

        verify(tagService, times(1)).createTag(anyString());
    }

    @Test
    void deleteTag_shouldDeleteTagSuccessfully() throws Exception {
        doNothing().when(tagService).deleteTag(anyString());

        mockMvc.perform(delete("/api/tags/1"))
                .andExpect(status().isNoContent());

        verify(tagService, times(1)).deleteTag("1");
    }
}
