package org.example.todolistandnotebook.backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.todolistandnotebook.backend.mapper.NotesMapper;
import org.example.todolistandnotebook.backend.pojo.ListOfNotes;
import org.example.todolistandnotebook.backend.pojo.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotesService implements NotesIService{

    @Autowired
    private NotesMapper notesMapper;

    @Transactional
    @Override
    public Note InsertNotes(Note note) {
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
    public ListOfNotes GetNotesByTitle(String title, Integer pageNum , Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Note> notes = notesMapper.getByTitle(title);
        PageInfo<Note> pageInfos = new PageInfo<>(notes);
        return new ListOfNotes(pageInfos.getList() , pageInfos.getTotal());
    }

    @Override
    public ListOfNotes GetAllNotes(Integer pageNum , Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Note> notes = notesMapper.getAll();
        PageInfo<Note> pageInfos = new PageInfo<>(notes);
        return new ListOfNotes(pageInfos.getList() , pageInfos.getTotal());
    }
}
