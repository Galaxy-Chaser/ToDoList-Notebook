package org.example.todolistandnotebook.backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.todolistandnotebook.backend.mapper.NotesMapper;
import org.example.todolistandnotebook.backend.pojo.ListOfNotes;
import org.example.todolistandnotebook.backend.pojo.Note;
import org.example.todolistandnotebook.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotesService implements NotesIService{

    @Autowired
    private NotesMapper notesMapper;

    private JwtUtil jwtUtil;

    @Transactional
    @Override
    public Note InsertNotes(Note note, String jwt) {
        jwtUtil = new JwtUtil();
        Integer userId = jwtUtil.getIdFromJwt(jwt);
        note.setUserId(userId);
        notesMapper.insert(note);
        Long id = note.getId();
        return notesMapper.getById(id);
    }

    @Override
    public Note UpdateNotes(Note note) {
        Long id = note.getId();
        notesMapper.update(note);
        return notesMapper.getById(id);
    }

    @Override
    public Void DeleteNotes(Long id) {
        notesMapper.delete(id);
        return null;
    }

    @Override
    public ListOfNotes GetNotesByTitle(String title, Integer pageNum , Integer pageSize, String jwt) {
        jwtUtil = new JwtUtil();
        Integer userId = jwtUtil.getIdFromJwt(jwt);
        PageHelper.startPage(pageNum, pageSize);
        List<Note> notes = notesMapper.getByTitle(title , userId);
        PageInfo<Note> pageInfos = new PageInfo<>(notes);
        return new ListOfNotes(pageInfos.getList() , pageInfos.getTotal());
    }

    @Override
    public ListOfNotes GetAllNotes(Integer pageNum , Integer pageSize, String jwt) {
        jwtUtil = new JwtUtil();
        Integer userId = jwtUtil.getIdFromJwt(jwt);
        PageHelper.startPage(pageNum, pageSize);
        List<Note> notes = notesMapper.getAll(userId);
        PageInfo<Note> pageInfos = new PageInfo<>(notes);
        return new ListOfNotes(pageInfos.getList() , pageInfos.getTotal());
    }
}
