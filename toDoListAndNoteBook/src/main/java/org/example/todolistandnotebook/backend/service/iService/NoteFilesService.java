package org.example.todolistandnotebook.backend.service.iService;

import org.example.todolistandnotebook.backend.pojo.NoteFile;
import org.example.todolistandnotebook.backend.pojo.ResourceDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;

@Service
public interface NoteFilesService {

    /**
     * 根据笔记Id返回笔记所关联文件
     * @param noteId 笔记Id
     * @return List<NoteFile> 文件列表
     */
    public List<NoteFile> getNoteFileByNoteId(Long noteId);

    /**
     * 根据笔记Id删除笔记所关联文件
     * @param noteId 笔记Id
     *
     */
    public void deleteNoteFileByNoteId(Long noteId);

    /**
     * 根据文件Id获取文件
     * @param noteFileId 文件id
     * @return NoteFile 文件
     */
    public NoteFile getNoteFileByNoteFileId(Long noteFileId);

    /**
     * 根据文件Id删除文件
     * @param noteFileId 文件Id
     */
    public void deleteSingleNoteFileByNoteFileId(Long noteFileId);

    /**
     * 根据文件Id删除文件（批量）
     * @param nfIdList 文件idliebiao
     */
    public void deleteNoteFilesByNoteFilesId(List<Long> nfIdList);

    /**
     * 批量保存文件,生成存储名、存储路径等信息
     * @param files 从前端传来的文件
     * @param noteId 笔记Id
     * @param userId 用户Id
     * @return 返回保存的列表
     */
    public List<NoteFile> saveNoteFilesLocally(List<MultipartFile> files , Long noteId , Integer userId) throws IOException, NoSuchAlgorithmException;

    /**
     * 将信息保存到数据库
     * @param noteFile 文件
     * @return NoteFile 返回文件
     */
    public NoteFile saveSingleNoteFile(NoteFile noteFile);

    /**
     * 修改文件
     * @param noteFile
     */
    public void updateNoteFile(NoteFile noteFile);

    /**
     * 获取所有已存储文件的Hash值
     * @return 返回所有的文件Hash值,如果有相同的Hash值则不需要存储在本地中
     */
    public HashSet<String> getAllFileHash();

    /**
     * 检验是否有相同文件上传到同一个笔记中
     * @param noteId 笔记Id
     * @return List<String> 返回该笔记Id所关联的文件Hash值
     */
    public List<String> getFileHashByNoteId(Long noteId);

    /**
     *
     * @param fileId 文件ID
     * @param download 是否下载
     * @return ResourceDto
     */
    ResourceDto downloadFile(Long fileId, boolean download);
}
