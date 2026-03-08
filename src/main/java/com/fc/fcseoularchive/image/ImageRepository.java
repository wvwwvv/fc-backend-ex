package com.fc.fcseoularchive.image;

import com.fc.fcseoularchive.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
