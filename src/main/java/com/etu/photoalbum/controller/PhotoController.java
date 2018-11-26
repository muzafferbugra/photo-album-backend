package com.etu.photogallery.controller;

import com.etu.photogallery.exception.ResourceNotFoundException;
import com.etu.photogallery.model.Photo;
import com.etu.photogallery.repository.AlbumRepository;
import com.etu.photogallery.repository.PhotoRepository;
import com.etu.photogallery.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PhotoController {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/albums/{albumId}/photos")
    public List<Photo> getAllPhotosByAlbumId(@PathVariable (value = "albumId") Long albumId) {
        return photoRepository.findByAlbumId(albumId);
    }

    @GetMapping("/albums/{albumId}/photos/{photoId}")
    public Photo getPhoto(@PathVariable (value = "albumId") Long albumId,
                                             @PathVariable (value = "photoId") Long photoId) {
        return photoRepository.findByAlbumIdAndId(albumId, photoId);
    }

    @PostMapping("/albums/{albumId}/photos")
    public Photo createPhoto(@PathVariable (value = "albumId") Long albumId,
                             @RequestPart("tags") String tags,
                             @RequestPart("file") @Valid MultipartFile file){
        if (file == null) {
            throw new ResourceNotFoundException("File is not found");
        }
        Map<String, String> photoInfo = cloudinaryService.uploadFile(file);
        return albumRepository.findById(albumId).map(album -> {
            Photo photo = new Photo();
            photo.setAlbum(album);
            photo.setTags(tags);
            photo.setPhotoLink(photoInfo.get("url"));
            photo.setPublicCloudinaryId(photoInfo.get("publicId"));
            return photoRepository.save(photo);
        }).orElseThrow(() -> new ResourceNotFoundException("AlbumId " + albumId + " not found"));
    }

    @PutMapping("/albums/{albumId}/photos/{photoId}")
    public Photo updatePhoto(@PathVariable (value = "albumId") Long albumId,
                                 @PathVariable (value = "photoId") Long photoId,
                                 @Valid @RequestBody Photo photoRequest) {
        if(!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException("AlbumId " + albumId + " not found");
        }

        return photoRepository.findById(photoId).map(photo -> {
            photo.setTags(photoRequest.getTags());
            return photoRepository.save(photo);
        }).orElseThrow(() -> new ResourceNotFoundException("PhotoId " + photoId + "not found"));
    }

    @DeleteMapping("/albums/{albumId}/photos/{photoId}")
    public ResponseEntity<?> deletePhoto(@PathVariable (value = "albumId") Long albumId,
                                           @PathVariable (value = "photoId") Long photoId) {
        if(!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException("AlbumId " + albumId + " not found");
        }

        return photoRepository.findById(photoId).map(photo -> {
            photoRepository.delete(photo);
            cloudinaryService.deleteFile(photo.getPublicCloudinaryId());
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("PhotoId " + photoId + " not found"));
    }

    @GetMapping("/searchWithTag/{tag}")
    public List<Photo> searchWithTag(@PathVariable (value = "tag") String tag){
        return photoRepository.findByTagsContaining(tag);
    }

    @GetMapping("/albums/{albumId}/photos/{photoId}/getUrl")
    public Map<String, String> getUrl (@PathVariable (value = "albumId") Long albumId,
                                       @PathVariable (value = "photoId") Long photoId) {
        if(!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException("AlbumId " + albumId + " not found");
        }
        Photo photo = photoRepository.findByAlbumIdAndId(albumId, photoId);
        if (photo == null) {
            throw new ResourceNotFoundException("PhotoId " + photoId + " not found");
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("url", photo.getPhotoLink());
        return map;
    }

    @PostMapping("/setDeleteHour/{hour}")
    public ResponseEntity<?> setDeleteHour(@PathVariable (value = "hour") int hour){
        return ResponseEntity.ok().build();
    }

}