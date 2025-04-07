package org.example.todolistandnotebook.backend.service;

import org.example.todolistandnotebook.backend.mapper.NoteFilesMapper;
import org.example.todolistandnotebook.backend.pojo.NoteFile;
import org.example.todolistandnotebook.backend.service.IService.NoteFilesIService;
import org.example.todolistandnotebook.backend.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class NoteFilesService implements NoteFilesIService {

    @Autowired
    private NoteFilesMapper noteFilesMapper;

    private FileUtil fileUtil = new FileUtil();

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
            fileUtil.deleteFromDisk(StoragePath);
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
    public void deleteNoteFileByNoteFileId(Long noteFileId) {
        NoteFile file = noteFilesMapper.selectById(noteFileId);
        if (file != null) {
            String storagePath = file.getFilePath();
            fileUtil.deleteFromDisk(storagePath);
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
            deleteNoteFileByNoteFileId(id);
        }
    }

    @Transactional
    @Override
    public List<NoteFile> saveNoteFiles(List<MultipartFile> files, Long noteId, Integer userId) throws IOException, NoSuchAlgorithmException {
        List<NoteFile> nfList = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return nfList;
        }
        for (MultipartFile file : files) {
            int flag = fileUtil.validateFile(file);
            if(flag != 0)
                continue;
            String fileHash = fileUtil.calculateSHA256(file);
            NoteFile nf = new NoteFile();
            nf.setOriginalName(file.getOriginalFilename());
            String storageName = fileUtil.generateStoredName(file.getOriginalFilename());
            nf.setStoredName(storageName);
            Path storagePath = fileUtil.buildStoragePath(userId , storageName);
            fileUtil.saveToDisk(file, storagePath);
            nf.setFilePath(String.valueOf(storagePath));
            nf.setFileType(file.getContentType());
            nf.setSize(file.getSize());
            nf.setNoteId(noteId);
            nf.setUserId(userId);
            nf.setFileHash(fileHash);
            nfList.add(saveNoteFile(nf));
        }
        return nfList;
    }

    @Override
    public NoteFile saveNoteFile(NoteFile noteFile) {
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
}
