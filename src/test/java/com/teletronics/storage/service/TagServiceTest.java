package com.teletronics.storage.service;

import com.teletronics.storage.model.Tag;
import com.teletronics.storage.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTags_shouldReturnAllTags() {
        Tag tag1 = new Tag();
        tag1.setId("1");
        tag1.setName("tag1");

        Tag tag2 = new Tag();
        tag2.setId("2");
        tag2.setName("tag2");

        when(tagRepository.findAll()).thenReturn(Arrays.asList(tag1, tag2));

        assertEquals(2, tagService.getAllTags().size());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void createTag_shouldCreateTagSuccessfully() {
        Tag tag = new Tag();
        tag.setId("1");
        tag.setName("tag1");

        when(tagRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag createdTag = tagService.createTag("tag1");
        assertEquals("1", createdTag.getId());
        assertEquals("tag1", createdTag.getName());

        verify(tagRepository, times(1)).findByNameIgnoreCase(anyString());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void createTag_shouldThrowExceptionWhenTagExists() {
        Tag tag = new Tag();
        tag.setId("1");
        tag.setName("tag1");

        when(tagRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(tag));

        assertThrows(IllegalArgumentException.class, () -> tagService.createTag("tag1"));

        verify(tagRepository, times(1)).findByNameIgnoreCase(anyString());
        verify(tagRepository, times(0)).save(any(Tag.class));
    }

    @Test
    void deleteTag_shouldDeleteTagSuccessfully() {
        doNothing().when(tagRepository).deleteById(anyString());

        tagService.deleteTag("1");

        verify(tagRepository, times(1)).deleteById("1");
    }
}
