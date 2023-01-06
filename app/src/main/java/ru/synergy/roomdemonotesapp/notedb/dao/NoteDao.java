package ru.synergy.roomdemonotesapp.notedb.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.synergy.roomdemonotesapp.notedb.model.Note;
import ru.synergy.roomdemonotesapp.util.Constants;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM " + Constants.TABLE_NAME_NOTE)
    List<Note> getNotes();

    /* Insert the object in database @param note, object to be inserted*/
    @Insert
    long insertNote(Note note);

    /*update the object in database
    * @param note, object to be updated*/
    @Update
    void updateNote(Note repos);

    /*delete the object from database
    * @param note, object to be deleted*/
    @Delete
    void deleteNote(Note note);

    /*delete list of objects from database
    * @param note, array of object to be deleted*/
    @Delete
    void deleteNotes(Note... note);
    //Note... is varargs, here note is an array
}
