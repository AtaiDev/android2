package com.example.taskapp.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.taskapp.models.Note;

@Database(entities = {Note.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract NoteDao noteDao();
}
