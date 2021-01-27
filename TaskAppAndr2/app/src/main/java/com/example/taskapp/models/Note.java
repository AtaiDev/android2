package com.example.taskapp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity
public class Note implements Serializable {
    @NonNull
    @PrimaryKey()
    private String id;
    private String title;
    private String createdAt;


    public Note() {
    }

    public Note(String title, String createdAt) {
        this.title = title;
        this.createdAt = createdAt;
    }

    @NotNull
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
