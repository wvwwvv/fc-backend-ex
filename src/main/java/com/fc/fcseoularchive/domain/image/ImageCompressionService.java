package com.fc.fcseoularchive.domain.image;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class ImageCompressionService {

    private static final int MAX_DIMENSION = 800;

    public byte[] compress(MultipartFile file) throws IOException {
        byte[] originalBytes = file.getBytes();

        // 원본 크기 체크
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(originalBytes));
        if (img == null) {
            throw new IOException("이미지를 읽을 수 없습니다");
        }
        int width = img.getWidth();
        int height = img.getHeight();

        // 투명도 여부로 중간 포맷 결정 (PNG는 PNG로, JPG는 JPG로 유지)
        boolean hasAlpha = img.getColorModel().hasAlpha();
        String format = hasAlpha ? "png" : "jpg";

        // 필요시 리사이즈 (원본 포맷 유지)
        byte[] processedBytes;
        if (width > MAX_DIMENSION || height > MAX_DIMENSION) {
            ByteArrayOutputStream resized = new ByteArrayOutputStream();
            Thumbnails.of(new ByteArrayInputStream(originalBytes))
                    .size(MAX_DIMENSION, MAX_DIMENSION)
                    .outputFormat(format)
                    .outputQuality(hasAlpha ? 1.0 : 0.9)
                    .toOutputStream(resized);
            processedBytes = resized.toByteArray();
        } else {
            // 리사이즈 불필요 - 원본 그대로 WebP 변환
            processedBytes = originalBytes;
        }

        // WebP로 변환
        try {
            return convertToWebp(processedBytes);
        } catch (Exception e) {
            log.warn("WebP 변환 실패, 원본 반환: {}", e.getMessage());
            return processedBytes;
        }
    }

    private byte[] convertToWebp(byte[] imageBytes) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "cwebp",
                "-q", "85",
                "-alpha_q", "100",
                "-m", "4",
                "-quiet",
                "-o", "-",
                "--",
                "-"
        );

        Process process = pb.start();

        try (var stdin = process.getOutputStream()) {
            stdin.write(imageBytes);
        }

        byte[] result = process.getInputStream().readAllBytes();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("cwebp exit code: " + exitCode);
        }
        return result;
    }
}