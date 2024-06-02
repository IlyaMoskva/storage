package com.teletronics.storage.controller;

import com.teletronics.storage.model.Tag;
import com.teletronics.storage.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }

    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody String name) {
        Tag tag = tagService.createTag(name);
        return new ResponseEntity<>(tag, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable String id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
