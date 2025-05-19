package org.example.todolistandnotebook.backend.service;

import org.example.todolistandnotebook.backend.mapper.NoteFilesMapper;
import org.example.todolistandnotebook.backend.pojo.NoteFile;
import org.example.todolistandnotebook.backend.pojo.ResourceDto;
import org.example.todolistandnotebook.backend.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class NoteFilesServiceImpl implements org.example.todolistandnotebook.backend.service.iService.NoteFilesService {

    @Autowired
    private NoteFilesMapper noteFilesMapper;

    @Autowired
    private FileUtils fileUtils;

    @Override
    public List<NoteFile> getNoteFileByNoteId(Long noteId) {
        List<NoteFile> nfList = noteFilesMapper.selectByNoteId(noteId);
        return nfList;
    }

    @Override
    public void deleteNoteFileByNoteId(Long noteId) {
        List<NoteFile> nfList = noteFilesMapper.selectByNoteId(noteId);
        for (NoteFile file : nfList) {
            String StoragePath = file.getFilePath();
            //调用FileUtils的deleteFromDisk方法删除文件
            fileUtils.deleteFromDisk(StoragePath);
        }
        noteFilesMapper.deleteByNoteId(noteId);
        return;
    }

    @Override
    public NoteFile getNoteFileByNoteFileId(Long noteFileId) {
        NoteFile noteFile = noteFilesMapper.selectById(noteFileId);
        return noteFile;
    }

    @Override
    public void deleteSingleNoteFileByNoteFileId(Long noteFileId) {
        NoteFile file = noteFilesMapper.selectById(noteFileId);
        if (file != null) {
            String storagePath = file.getFilePath();
            //调用FileUtils的deleteFromDisk方法删除文件
            fileUtils.deleteFromDisk(storagePath);
            noteFilesMapper.deleteById(noteFileId);
        } else {
            // 可以选择记录日志或直接返回
            System.err.println("未找到对应的 NoteFile，ID：" + noteFileId);
        }
    }


    @Override
    public void deleteNoteFilesByNoteFilesId(List<Long> nfIdList) {
        if(nfIdList == null || nfIdList.isEmpty()) return;
        for(Long id : nfIdList) {
            deleteSingleNoteFileByNoteFileId(id);
        }
    }

    @Transactional
    @Override
    public List<NoteFile> saveNoteFilesLocally(List<MultipartFile> files, Long noteId, Integer userId) throws IOException, NoSuchAlgorithmException {
        List<NoteFile> nfList = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return nfList;
        }
        for (MultipartFile file : files) {
            //校验文件是否合法
            int flag = fileUtils.validateFile(file);
            if(flag != 0)
                continue;
            //计算文件哈希值
            String fileHash = fileUtils.calculateSHA256(file);
            NoteFile nf = new NoteFile();
            nf.setOriginalName(file.getOriginalFilename());
            //生成存储文件名
            String storageName = fileUtils.generateStoredName(file.getOriginalFilename());
            nf.setStoredName(storageName);
            //生成存储路径
            Path storagePath = fileUtils.buildStoragePath(userId , storageName);
            //文件保存到磁盘
            fileUtils.saveToDisk(file, storagePath);
            nf.setFilePath(String.valueOf(storagePath));
            nf.setFileType(file.getContentType());
            nf.setSize(file.getSize());
            nf.setNoteId(noteId);
            nf.setUserId(userId);
            nf.setFileHash(fileHash);
            nfList.add(saveSingleNoteFile(nf));
        }
        return nfList;
    }

    @Override
    public NoteFile saveSingleNoteFile(NoteFile noteFile) {
        noteFilesMapper.insert(noteFile);
        long id = noteFile.getId();
        return noteFilesMapper.selectById(id);
    }

    @Override
    public void updateNoteFile(NoteFile noteFile) {
        noteFilesMapper.update(noteFile);
        return;
    }

    @Override
    public HashSet<String> getAllFileHash() {
        List<String> dbList = noteFilesMapper.selectAllFileHash();
        HashSet<String> hashSet = new HashSet<>(dbList.size() + 1); // +1避免刚好满容
        hashSet.addAll(dbList);
        return hashSet;
    }

    @Override
    public List<String> getFileHashByNoteId(Long noteId) {
        List<String> nfList = noteFilesMapper.selectFileHashByNoteId(noteId);
        return nfList;
    }

    @Override
    public ResourceDto downloadFile(Long fileId, boolean download) {
        NoteFile file = getNoteFileByNoteFileId(fileId);
        Path path = Paths.get(file.getFilePath());
        Resource resource = new FileSystemResource(path);

        MediaType mediaType = MediaType.parseMediaType(file.getFileType());
        String disposition = download ? "attachment" : "inline";
        // 对文件名进行 URL 编码
        String encodedFilename = UriUtils.encode(file.getOriginalName(), StandardCharsets.UTF_8);
        String contentDisposition = String.format("%s; filename=\"%s\"; filename*=UTF-8''%s",
                disposition, encodedFilename, encodedFilename);

        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setResource(resource);
        resourceDto.setMediaType(mediaType);
        resourceDto.setContentDisposition(contentDisposition);
        return resourceDto;
    }
}
