package org.example.todolistandnotebook.backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.todolistandnotebook.backend.mapper.NotesMapper;
import org.example.todolistandnotebook.backend.pojo.ListOfNotes;
import org.example.todolistandnotebook.backend.pojo.Note;
import org.example.todolistandnotebook.backend.service.IService.NotesIService;
import org.example.todolistandnotebook.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class NotesService implements NotesIService {

    @Autowired
    private NotesMapper notesMapper;

    @Autowired
    private NoteFilesService noteFilesService;

    private JwtUtil jwtUtil;

    @Transactional
    @Override
    public Note InsertNotes(Note note, List<MultipartFile> files, String jwt) throws IOException, NoSuchAlgorithmException {
        //获取用户id
        jwtUtil = new JwtUtil();
        Integer userId = jwtUtil.getIdFromJwt(jwt);
        note.setUserId(userId);
        //新增笔记部分
        notesMapper.insert(note);
        Long id = note.getId();
        //新增文件部分
        noteFilesService.saveNoteFiles(files , id , userId);
        return notesMapper.getById(id);
    }

    @Transactional
    @Override
    public Note UpdateNotes(Note note, List<MultipartFile> files, List<Long> deletedFiles, String jwt) throws IOException, NoSuchAlgorithmException {
        Long id = note.getId();
        //删除文件
        noteFilesService.deleteNoteFilesByNoteFilesId(deletedFiles);
        //新增文件
        JwtUtil jwtUtil = new JwtUtil();
        Integer userId = jwtUtil.getIdFromJwt(jwt);
        noteFilesService.saveNoteFiles(files , note.getId() , userId);
        notesMapper.update(note);
        Note updatedNote = notesMapper.getById(id);
        updatedNote.setAttachedFiles(noteFilesService.getNoteFileByNoteId(id));
        return updatedNote;
    }

    @Transactional
    @Override
    public void DeleteNotes(Long id) {
        noteFilesService.deleteNoteFileByNoteId(id);
        notesMapper.delete(id);
        return;
    }

    @Transactional
    @Override
    public ListOfNotes GetNotesByTitle(String title, Integer pageNum , Integer pageSize, String jwt) {
        jwtUtil = new JwtUtil();
        Integer userId = jwtUtil.getIdFromJwt(jwt);
        PageHelper.startPage(pageNum, pageSize);
        List<Note> notes = notesMapper.getByTitle(title , userId);
        if(notes != null && !notes.isEmpty()){
            for(Note note : notes){
                note.setAttachedFiles(noteFilesService.getNoteFileByNoteId(note.getId()));
            }
        }
        PageInfo<Note> pageInfos = new PageInfo<>(notes);
        return new ListOfNotes(pageInfos.getList() , pageInfos.getTotal());
    }

    @Transactional
    @Override
    public ListOfNotes GetAllNotes(Integer pageNum , Integer pageSize, String jwt) {
        jwtUtil = new JwtUtil();
        Integer userId = jwtUtil.getIdFromJwt(jwt);
        PageHelper.startPage(pageNum, pageSize);
        List<Note> notes = notesMapper.getAll(userId);
        if(notes != null && !notes.isEmpty()){
            for(Note note : notes){
                note.setAttachedFiles(noteFilesService.getNoteFileByNoteId(note.getId()));
            }
        }
        PageInfo<Note> pageInfos = new PageInfo<>(notes);
        return new ListOfNotes(pageInfos.getList() , pageInfos.getTotal());
    }
}
