package org.example.todolistandnotebook.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.todolistandnotebook.backend.pojo.NoteFile;
import java.util.List;

@Mapper
public interface NoteFilesMapper {

    @Options(keyProperty = "id" , useGeneratedKeys = true)
    @Insert("insert into noteFiles(originalName, storedName, filePath, fileType, size, noteId, userId , fileHash)" +
            " values " +
            "(#{originalName}, #{storedName}, #{filePath}, #{fileType}, #{size}, #{noteId}, #{userId} , #{fileHash})"
    )
    Long insert(NoteFile noteFile);

    @Select("select id, originalName, storedName, filePath, fileType, size, createdAt, noteId, userId , fileHash" +
            " from noteFiles where id = #{id}")
    NoteFile selectById(Long id);

    @Select("select id, originalName, storedName, filePath, fileType, size, createdAt, noteId, userId , fileHash" +
            " from noteFiles where noteId = #{noteId}")
    List<NoteFile> selectByNoteId(Long noteId);

    @Select("select fileHash from notefiles where noteId = #{noteId}")
    List<String> selectFileHashByNoteId(Long noteId);

    @Select("select fileHash from notefiles")
    List<String> selectAllFileHash();

    @Delete("delete from noteFiles where id = #{id}")
    void deleteById(Long id);

    @Delete("delete from noteFiles where noteId = #{noteId}")
    void deleteByNoteId(Long noteId);

    void update(NoteFile noteFile);
}