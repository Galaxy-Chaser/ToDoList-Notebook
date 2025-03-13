package org.example.todolistandnotebook.backend.service;

import org.example.todolistandnotebook.backend.pojo.ListOfNotes;
import org.example.todolistandnotebook.backend.pojo.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotesIService {

    /**
     *新增笔记
     *
     * @param note 笔记
     * @param jwt jwt令牌
     */
    Note InsertNotes(Note note, String jwt);

    /**
     *修改笔记
     *
     * @param note 笔记
     * @return Todo 修改后的笔记
     */
    Note UpdateNotes(Note note);

    /**
     *删除笔记
     *
     * @param id 笔记id
     */
    Void DeleteNotes(Long id);

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
}
