package com.fc.fcseoularchive.domain.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByGame_IdAndUser_Id(Long gameId, String userId);

    void deleteByGame_IdAndUser_Id(Long gameId, String userId);

    // 게임 id 와 유저 id로 찾되, image 의 pk 가 가장 작은 것 반환 - 없을 수도 있다
    // 단, 게시물의 개수만큼 image 조회가 N 번 발생
    Optional<Image> findFirstByGame_IdAndUser_IdOrderByIdAsc(Long gameId, String userId);


}
