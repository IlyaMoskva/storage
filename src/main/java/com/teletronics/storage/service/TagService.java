package com.teletronics.storage.service;

import com.teletronics.storage.model.Tag;
import com.teletronics.storage.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag createTag(String name) {
        Optional<Tag> existingTag = tagRepository.findByNameIgnoreCase(name);
        if (existingTag.isPresent()) {
            throw new IllegalArgumentException("Tag already exists");
        }
        Tag tag = new Tag();
        tag.setName(name);
        return tagRepository.save(tag);
    }

    public void deleteTag(String id) {
        tagRepository.deleteById(id);
    }
}
