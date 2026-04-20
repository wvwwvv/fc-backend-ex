package com.fc.fcseoularchive.global.util;

import java.util.UUID;

public class FileNameUtils {

    private FileNameUtils() {}

    /**
     * 원본 파일명을 기반으로 UUID가 붙은 WebP 파일명 생성
     */
    public static String generateWebpFileName(String originalFilename) {
        String nameWithoutExt = stripExtension(originalFilename);
        return UUID.randomUUID() + "_" + nameWithoutExt + ".webp";
    }

    /**
     * 파일명에서 확장자 제거
     */
    public static String stripExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "image";
        }
        int dotIdx = filename.lastIndexOf('.');
        return (dotIdx == -1) ? filename : filename.substring(0, dotIdx);
    }
}