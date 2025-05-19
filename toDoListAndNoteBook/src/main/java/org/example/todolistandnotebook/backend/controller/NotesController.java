package org.example.todolistandnotebook.backend.controller;

import org.example.todolistandnotebook.backend.pojo.CommonResponse;
import org.example.todolistandnotebook.backend.pojo.ListOfNotes;
import org.example.todolistandnotebook.backend.pojo.Note;
import org.example.todolistandnotebook.backend.pojo.ResourceDto;
import org.example.todolistandnotebook.backend.service.NoteFilesServiceImpl;
import org.example.todolistandnotebook.backend.service.NotesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/notes")
public class NotesController {

    @Autowired
    private NotesServiceImpl service;

    @Autowired
    private NoteFilesServiceImpl noteFilesServiceImpl;

    // 新增笔记接口：使用@RequestPart解析JSON数据和文件
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<Note> addNote(
            @RequestPart("note") Note note,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestHeader("Authorization") String jwt) throws IOException, NoSuchAlgorithmException {
        Note newNote = service.InsertNotes(note, files, jwt);
        return CommonResponse.success(newNote);
    }

    // 删除笔记
    @DeleteMapping
    public CommonResponse deleteNote(@RequestParam Long id) {
        if (id == null)
            return CommonResponse.error();
        service.DeleteNotes(id);
        return CommonResponse.success();
    }

    // 查询所有笔记（分页）
    @GetMapping
    public CommonResponse<ListOfNotes> getNotes(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "5") Integer pageSize,
                                                @RequestHeader("Authorization") String jwt) {
        if (pageNum < 1)
            return CommonResponse.error();
        return CommonResponse.success(service.GetAllNotes(pageNum, pageSize, jwt));
    }

    // 根据标题查询笔记（分页）
    @GetMapping(params = "title")
    public CommonResponse<ListOfNotes> getNotes(@RequestParam() String title,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "5") Integer pageSize,
                                                @RequestHeader("Authorization") String jwt) {
        if (pageNum < 1)
            return CommonResponse.error();
        return CommonResponse.success(service.GetNotesByTitle(title, pageNum, pageSize, jwt));
    }

    // 下载文件
    @GetMapping("files")
    public ResponseEntity<Resource> getFile(@RequestParam("fileId") Long fileId,
                                            @RequestParam(defaultValue = "false") boolean download) {

        ResourceDto resourceDto = noteFilesServiceImpl.downloadFile(fileId, download);

        //资源是否存在，是否可以访问
        if (!resourceDto.getResource().exists() || !resourceDto.getResource().isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(resourceDto.getMediaType())
                .header(HttpHeaders.CONTENT_DISPOSITION, resourceDto.getContentDisposition())
                .body(resourceDto.getResource());
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<Note> updateNote(
            @RequestPart("note") Note note,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "deletedFiles", required = false) List<Long> deletedFiles,
            @RequestHeader("Authorization") String jwt) throws IOException, NoSuchAlgorithmException {
        Note updatedNote = service.UpdateNotes(note, files, deletedFiles, jwt);
        return CommonResponse.success(updatedNote);
    }

}
