package org.example.todolistandnotebook.backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.todolistandnotebook.backend.mapper.NotesMapper;
import org.example.todolistandnotebook.backend.pojo.ListOfNotes;
import org.example.todolistandnotebook.backend.pojo.Note;
import org.example.todolistandnotebook.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class NotesServiceImpl implements org.example.todolistandnotebook.backend.service.iService.NotesService {

    @Autowired
    private NotesMapper notesMapper;

    @Autowired
    private NoteFilesServiceImpl noteFilesServiceImpl;

    private JwtUtils jwtUtils;

    @Transactional
    @Override
    public Note InsertNotes(Note note, List<MultipartFile> files, String jwt) throws IOException, NoSuchAlgorithmException {
        //获取用户id
        jwtUtils = new JwtUtils();
        Integer userId = jwtUtils.getIdFromJwt(jwt);
        note.setUserId(userId);
        //新增笔记部分
        notesMapper.insert(note);
        Long id = note.getId();
        //新增文件部分
        noteFilesServiceImpl.saveNoteFilesLocally(files , id , userId);
        return notesMapper.getById(id);
    }

    @Transactional
    @Override
    public Note UpdateNotes(Note note, List<MultipartFile> files, List<Long> deletedFiles, String jwt) throws IOException, NoSuchAlgorithmException {
        Long id = note.getId();
        //删除文件
        noteFilesServiceImpl.deleteNoteFilesByNoteFilesId(deletedFiles);
        //新增文件
        JwtUtils jwtUtils = new JwtUtils();
        Integer userId = jwtUtils.getIdFromJwt(jwt);
        noteFilesServiceImpl.saveNoteFilesLocally(files , note.getId() , userId);
        notesMapper.update(note);
        Note updatedNote = notesMapper.getById(id);
        updatedNote.setAttachedFiles(noteFilesServiceImpl.getNoteFileByNoteId(id));
        return updatedNote;
    }

    @Transactional
    @Override
    public void DeleteNotes(Long id) {
        noteFilesServiceImpl.deleteNoteFileByNoteId(id);
        notesMapper.delete(id);
        return;
    }

    @Transactional
    @Override
    public ListOfNotes GetNotesByTitle(String title, Integer pageNum , Integer pageSize, String jwt) {
        //获取用户id
        jwtUtils = new JwtUtils();
        Integer userId = jwtUtils.getIdFromJwt(jwt);
        //分页插件 设置页码和每页显示数量
        PageHelper.startPage(pageNum, pageSize);
        List<Note> notes = notesMapper.getByTitle(title , userId);
        if(notes != null && !notes.isEmpty()){
            for(Note note : notes){
                note.setAttachedFiles(noteFilesServiceImpl.getNoteFileByNoteId(note.getId()));
            }
        }
        //获取分页信息
        PageInfo<Note> pageInfos = new PageInfo<>(notes);
        return new ListOfNotes(pageInfos.getList() , pageInfos.getTotal());
    }

    @Transactional
    @Override
    public ListOfNotes GetAllNotes(Integer pageNum , Integer pageSize, String jwt) {
        //获取用户id
        jwtUtils = new JwtUtils();
        Integer userId = jwtUtils.getIdFromJwt(jwt);
        //分页插件 设置页码和每页显示数量
        PageHelper.startPage(pageNum, pageSize);
        List<Note> notes = notesMapper.getAll(userId);
        if(notes != null && !notes.isEmpty()){
            for(Note note : notes){
                note.setAttachedFiles(noteFilesServiceImpl.getNoteFileByNoteId(note.getId()));
            }
        }
        //获取分页信息
        PageInfo<Note> pageInfos = new PageInfo<>(notes);
        return new ListOfNotes(pageInfos.getList() , pageInfos.getTotal());
    }

    @Override
    public void DeleteNoteByUserId(Integer id) {
        List<Long> notes = notesMapper.getByUserId(id);
        if(notes != null && !notes.isEmpty()){
            for(Long noteId : notes){
                notesMapper.delete(noteId);
            }
        }
        return;
    }
}
