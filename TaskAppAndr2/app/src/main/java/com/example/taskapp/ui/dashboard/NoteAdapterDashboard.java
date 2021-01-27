package com.example.taskapp.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskapp.R;
import com.example.taskapp.models.Note;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NoteAdapterDashboard extends FirestoreRecyclerAdapter<Note, NoteAdapterDashboard.NoteViewHolder> {


    public NoteAdapterDashboard(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note model) {
        holder.onBind(model,position);

    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_list, parent, false);
        return new NoteViewHolder(view);
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        private final TextView textNote;
        private final TextView textTime;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textNote = itemView.findViewById(R.id.textDashView);
            textTime = itemView.findViewById(R.id.text_time_dash);
        }

        public void onBind(Note model,int position) {
            itemView.setTag(model.getId());
            textTime.setText(model.getCreatedAt());
            textNote.setText(model.getTitle());
            if (position%2 == 0) itemView.setBackgroundColor(itemView.getResources().getColor(R.color.colorBackgroundElement_blueShade));
            else itemView.setBackgroundColor(itemView.getResources().getColor(R.color.colorBackgroundElement_grey));
        }
    }
}
