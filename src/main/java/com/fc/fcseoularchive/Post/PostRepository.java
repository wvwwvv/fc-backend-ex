package com.fc.fcseoularchive.Post;

import com.fc.fcseoularchive.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {

}
