package com.team.HoneyBadger.Enum;

import java.io.File;

public enum FileType {
    FOLDER, IMAGE, VIDEO, AUDIO, COMPRESSED, DOCUMENT, ETC;

    public static FileType get(File file) {
        if (file.isDirectory())
            return FOLDER;
        if (file.getName().contains(".")) {
            String type = file.getName().split("\\.")[1];
            return switch (type.toUpperCase()) {
                case "TXT", "HWP", "PDF", "XLS", "XLSX", "PPT", "PPTX", "DOC", "DOCX" -> DOCUMENT;
                case "PNG", "JPEG","JPG", "TIFF", "GIF", "BMP" -> IMAGE;
                case "MP4", "WMV", "AVI", "MKV", "MPEG-2", "MOV" -> VIDEO;
                case "WAV", "MP3", "OGG", "WMA", "AAC" -> AUDIO;
                case "ZIP", "7Z","APK","RAR","TAR","DEB","RPM","JAR","EAR","WAR","COMPRESS","GZIP","BZIP2","XZ" -> COMPRESSED;
                default -> ETC;
            };
        }
        return ETC;
    }
    public boolean isAllow(File file){
        return this.equals(get(file));
    }
}
