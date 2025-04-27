package org.example.todolistandnotebook.backend.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Set;
import java.util.UUID;

/**
 * @packageName: org.example.todolistandnotebook.backend.utils
 * @className: FileUtils
 * @description: web上传文件工具类，用于对文件进行校验、生成唯一存储文件名、计算文件哈希值、存储文件、删除文件等操作
 */
@Component
public class FileUtils {

    private final String uploadRoot = "L:\\UrlStorage\\Todolist&Notebook";

    //校验成功 ： 0
    //文件为空 ： 1
    //文件名包含非法字符 ： 2
    //文件类型不符合要求 ： 3
    //文件超过大小限制 ： 4
    public Integer validateFile(MultipartFile file) {
        // 空文件校验
        if (file.isEmpty()) {
            return 1;
        }

        // 文件名校验（防止路径遍历攻击）
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains("..")) {
            return 2;
        }

        // 文件类型白名单校验
        Set<String> allowedTypes = Set.of("image/jpeg", "image/png", "application/pdf");
        String contentType = file.getContentType();
        if (!allowedTypes.contains(contentType)) {
            return 3;
        }

        // 文件大小限制（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            return 4;
        }

        return 0;
    }

    //生成唯一存储文件名
    public String generateStoredName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    //计算文件哈希值（SHA-256）
    public String calculateSHA256(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
        }
        return HexFormat.of().formatHex(md.digest());
    }

    //获取文件存储路径
    public Path buildStoragePath(Integer userId, String storedName) {
        return Paths.get(
                uploadRoot, // 从配置读取的根目录
                "user_" + userId,
                LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                storedName
        );
    }

    //存储文件
    public void saveToDisk(MultipartFile file, Path storagePath) {
        try {
            // 创建父目录（如果不存在）
            Files.createDirectories(storagePath.getParent());

            // 保存文件（使用原子移动操作）
            Path tempFile = Files.createTempFile(storagePath.getParent(), "upload_", ".tmp");
            file.transferTo(tempFile);
            Files.move(tempFile, storagePath, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //删除文件
    public void deleteFromDisk(String storagePath) {
        File file = new File(storagePath);
        file.delete();
    }
}
