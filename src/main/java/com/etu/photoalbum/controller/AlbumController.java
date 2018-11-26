package com.etu.photogallery.controller;

import com.etu.photogallery.exception.ResourceNotFoundException;
import com.etu.photogallery.model.Album;
import com.etu.photogallery.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    @Autowired
    AlbumRepository albumRepository;

    @GetMapping("/")
    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    @PostMapping("/")
    public Album createAlbum(@Valid @RequestBody Album album) {
        return albumRepository.save(album);
    }

    @PutMapping("/{albumId}")
    public Album updatePost(@PathVariable Long albumId, @Valid @RequestBody Album albumRequest) {
        return albumRepository.findById(albumId).map(post -> {
            post.setTitle(albumRequest.getTitle());
            return albumRepository.save(post);
        }).orElseThrow(() -> new ResourceNotFoundException("AlbumId " + albumId + " not found"));
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<?> deleteAlbum(@PathVariable Long albumId) {
        return albumRepository.findById(albumId).map(post -> {
            albumRepository.delete(post);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("PostId " + albumId + " not found"));
    }

}
