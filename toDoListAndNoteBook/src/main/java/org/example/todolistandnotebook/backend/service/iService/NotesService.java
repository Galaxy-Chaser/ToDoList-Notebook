package org.example.todolistandnotebook.backend.service.iService;

import org.example.todolistandnotebook.backend.pojo.ListOfNotes;
import org.example.todolistandnotebook.backend.pojo.Note;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public interface NotesService {

    /**
     *新增笔记
     *
     * @param note 笔记
     * @param jwt jwt令牌
     */
    Note InsertNotes(Note note, List<MultipartFile> files, String jwt) throws IOException, NoSuchAlgorithmException;

    /**
     *修改笔记
     *
     * @param note 笔记
     * @param files 新增文件
     * @param deletedFiles 待删除文件
     * @param jwt jwt令牌
     * @return Note 修改后的笔记
     */
    Note UpdateNotes(Note note , List<MultipartFile> files , List<Long> deletedFiles, String jwt) throws IOException, NoSuchAlgorithmException;

    /**
     *删除笔记
     *
     * @param id 笔记id
     */
    void DeleteNotes(Long id);

    /**
     * 根据标题内容
     * 查询笔记
     *
     * @param title 笔记的标题
     * @param jwt jwt令牌
     * @return List<Note> 笔记列表
     */
    ListOfNotes GetNotesByTitle(String title , Integer pageNum , Integer pageSize, String jwt);

    /**
     * 查询所有笔记
     *
     * @param jwt jwt令牌
     * @return List<Note> 笔记列表
     */
    ListOfNotes GetAllNotes(Integer pageNum , Integer pageSize, String jwt);

    /**
     * 通过用户id删除与用户相关联的所有笔记
     * @param id 用户id
     */
    void DeleteNoteByUserId(Integer id);


}
