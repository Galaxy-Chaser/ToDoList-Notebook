package org.example.todolistandnotebook.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.example.todolistandnotebook.backend.pojo.CommonResponse;
import org.example.todolistandnotebook.backend.pojo.ListOfNotes;
import org.example.todolistandnotebook.backend.pojo.Note;
import org.example.todolistandnotebook.backend.pojo.NoteFile;
import org.example.todolistandnotebook.backend.service.NoteFilesService;
import org.example.todolistandnotebook.backend.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/notes")
public class NotesController {

    @Autowired
    private NotesService service;

    @Autowired
    private NoteFilesService noteFilesService;

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

//    // 删除文件
//    @DeleteMapping("/noteFiles")
//    public CommonResponse deleteNoteFiles(@RequestParam List<Long> noteFilesId) {
//        noteFilesService.deleteNoteFilesByNoteFilesId(noteFilesId);
//        return CommonResponse.success();
//    }

    // 查询所有笔记（分页）
    @GetMapping("/getAll")
    public CommonResponse<ListOfNotes> getNotes(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "5") Integer pageSize,
                                                @RequestHeader("Authorization") String jwt) {
        if (pageNum < 1)
            return CommonResponse.error();
        return CommonResponse.success(service.GetAllNotes(pageNum, pageSize, jwt));
    }

    // 根据标题查询笔记（分页）
    @GetMapping("/getByTitle")
    public CommonResponse<ListOfNotes> getNotes(@RequestParam String title,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "5") Integer pageSize,
                                                @RequestHeader("Authorization") String jwt) {
        if (pageNum < 1)
            return CommonResponse.error();
        return CommonResponse.success(service.GetNotesByTitle(title, pageNum, pageSize, jwt));
    }

    // 下载文件
    @GetMapping("/getFiles")
    public ResponseEntity<Resource> getFile(@RequestParam("fileId") Long fileId,
                                            @RequestParam(defaultValue = "false") boolean download) {
        NoteFile file = noteFilesService.getNoteFileByNoteFileId(fileId);
        Path path = Paths.get(file.getFilePath());
        Resource resource = new FileSystemResource(path);
        if (resource.exists() && resource.isReadable()) {
            MediaType mediaType = MediaType.parseMediaType(file.getFileType());
            String disposition = download ? "attachment" : "inline";
            // 对文件名进行 URL 编码
            String encodedFilename = UriUtils.encode(file.getOriginalName(), StandardCharsets.UTF_8);
            String contentDisposition = String.format("%s; filename=\"%s\"; filename*=UTF-8''%s",
                    disposition, encodedFilename, encodedFilename);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
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
