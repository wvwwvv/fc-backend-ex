package com.fc.fcseoularchive.season_auth;


import com.fc.fcseoularchive.domain.entity.Seasonauth;
import com.fc.fcseoularchive.season_auth.dto.SeasonResponse;
import com.fc.fcseoularchive.season_auth.dto.createSeanauthRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "3. SeasonAuthController")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seasonauth")
public class SeasonauthController {

    private final SeasonauthService seasonauthService;

    /** 1. 인증서 POST API */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createSeasonauth(@RequestParam Long id, @NotNull @RequestPart MultipartFile image) throws IOException {
        seasonauthService.create(id, image);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** 2. (관리자) 인증서 GET API */
    @GetMapping
    public ResponseEntity<List<SeasonResponse>> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(seasonauthService.getAll());
    }


    /** 3. (관리자) 인증서 수락 API */
    @PostMapping("/approved/{id}")
    public ResponseEntity<Void> approve(@PathVariable Long id){
        seasonauthService.approve(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /** 4. (관리자) 인증서 거절 API */
    @PostMapping("/rejected/{id}")
    public ResponseEntity<Void> rejected(@PathVariable Long id){
        seasonauthService.rejected(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
