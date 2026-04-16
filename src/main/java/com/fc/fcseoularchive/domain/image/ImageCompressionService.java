package com.fc.fcseoularchive.domain.image;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class ImageCompressionService {

    private static final int MAX_DIMENSION = 1080;
    private static final double JPEG_QUALITY = 0.80;

    /**
     * 이미지를 thumbnail 로 압축함, 최대 1080px on the longest side.
     * Thumbnailator 가 비율을 보존한다
     * PNG는 투명 배경 보존을 위해 PNG 포맷 유지, JPG는 0.8 품질로 압축
     */
    public byte[] compress(MultipartFile file) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // PNG 파일 T/F 반환
        boolean isPng = "image/png".equals(file.getContentType());

        Thumbnails.of(file.getInputStream())
                .size(MAX_DIMENSION, MAX_DIMENSION)
                .outputFormat(isPng ? "png" : "jpg")      // 포맷이 png 파일 이라면, png
                .outputQuality(isPng ? 1.0 : JPEG_QUALITY)
                .toOutputStream(out);
        return out.toByteArray();
    }

}
