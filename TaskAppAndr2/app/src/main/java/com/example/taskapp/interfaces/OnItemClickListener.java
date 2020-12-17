package com.example.taskapp.interfaces;

import com.example.taskapp.models.Note;

public interface OnItemClickListener {
    void onItemClick(int position, Note note);
    void onLongClick(int position,Note note);

}
