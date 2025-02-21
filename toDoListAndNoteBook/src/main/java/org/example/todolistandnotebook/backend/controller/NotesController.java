package org.example.todolistandnotebook.backend.controller;

import org.example.todolistandnotebook.backend.pojo.CommonResponse;
import org.example.todolistandnotebook.backend.pojo.ListOfNotes;
import org.example.todolistandnotebook.backend.pojo.Note;
import org.example.todolistandnotebook.backend.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/notes")
public class NotesController {

    @Autowired
    private NotesService service;

    //增
    @PostMapping
    public CommonResponse<Note> addNote(@RequestBody Note note) {
        Note newNote = service.InsertNotes(note);
        return CommonResponse.success(newNote);
    }

    //删
    @DeleteMapping
    public CommonResponse deleteNote(@RequestParam Long id) {
        if(id == null)
            return CommonResponse.error();
        service.DeleteNotes(id);
        return CommonResponse.success();
    }

    //查
    //查所有代表事项
    @GetMapping("/getAll")
    public CommonResponse<ListOfNotes> getNotes(@RequestParam(defaultValue = "1") Integer pageNum , @RequestParam(defaultValue = "5") Integer pageSize) {
        if(pageNum < 1)
            return CommonResponse.error();
        return CommonResponse.success(service.GetAllNotes(pageNum , pageSize));
    }

    @GetMapping("/getByTitle")
    public CommonResponse<ListOfNotes> getNotes(@RequestParam String title , @RequestParam(defaultValue = "1") Integer pageNum , @RequestParam(defaultValue = "5") Integer pageSize) {
        if(pageNum < 1)
            return CommonResponse.error();
        return CommonResponse.success(service.GetNotesByTitle(title , pageNum , pageSize));
    }


    //改
    @PutMapping
    public CommonResponse<Note> updateNote(@RequestBody Note note) {
        Note updatedNotes = service.UpdateNotes(note);
        return CommonResponse.success(updatedNotes);
    }


}
