package com.etu.photogallery.repository;

import com.etu.photogallery.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByAlbumId(Long albumId);

    List<Photo> findByTagsContaining(String tag);

    void deleteByCreatedAtBefore(Date expiryDate);

    Photo findByAlbumIdAndId(Long albumId, Long photoId);
}