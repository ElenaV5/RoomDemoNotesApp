package ru.synergy.roomdemonotesapp.notedb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ru.synergy.roomdemonotesapp.notedb.dao.NoteDao;
import ru.synergy.roomdemonotesapp.notedb.model.Note;
import ru.synergy.roomdemonotesapp.util.Constants;
import ru.synergy.roomdemonotesapp.util.DateRoomConverter;

@Database(entities = { Note.class }, version = 1)
@TypeConverters({DateRoomConverter.class})
public abstract class NoteDatabase extends RoomDatabase {

    public abstract NoteDao getNoteDao();

    private static NoteDatabase noteDB;

    //synchronized is use to avoid concurrent access in multithred environment
    public static /*synchronized*/ NoteDatabase getInstance(Context context) {
        if (null == noteDB) {
            noteDB = buildDatabaseInstance(context);
        }
        return noteDB;
    }

    private static NoteDatabase buildDatabaseInstance(Context context){
        return Room.databaseBuilder(context,
                NoteDatabase.class,
                Constants.DB_NAME).allowMainThreadQueries().build();
    }

    public void cleanUp(){
        noteDB = null;
    }
}
