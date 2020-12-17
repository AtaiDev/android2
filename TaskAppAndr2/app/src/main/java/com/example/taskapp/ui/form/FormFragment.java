package com.example.taskapp.ui.form;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.taskapp.App;
import com.example.taskapp.MainActivity;
import com.example.taskapp.models.Note;
import com.example.taskapp.R;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class FormFragment extends Fragment {
    private EditText editText;
    private Note noteToEdit;
    private String timing;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editText = view.findViewById(R.id.edit_text);
        retrieveBundle();
        view.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                save();
            }

            private void save() {
                String text = editText.getText().toString();
                Log.e("FormFragment", "onClick: button clicked" + text);
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm yyyy/MM/dd", Locale.ROOT);
                String dateString = dateFormat.format(System.currentTimeMillis());
                //String dateSt = DateFormat.getDateInstance().format(System.currentTimeMillis());<---Dec 16, 2020 in this format;
                // to use in both cases
                Note note;
                if (noteToEdit!= null) {
                    note = new Note(text, timing);
                    App.getDataBase().noteDao().updateItem(note);
                } else {
                    note = new Note(text, dateString);
                    App.getDataBase().noteDao().insert(note);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("note", note);
                getParentFragmentManager().setFragmentResult("rk_task", bundle);
                ((MainActivity) requireActivity()).closeFragment();


            }
        });

    }

    private void retrieveBundle() {
        getParentFragmentManager().setFragmentResultListener("key_edit", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if (result != null) {
                    noteToEdit = (Note) result.getSerializable("edit");
                    editText.setText(noteToEdit.getTitle());
                    timing = noteToEdit.getCreatedAt();
                    Log.e("ololo", "onFragmentResult: "+ noteToEdit.getTitle() + " " + timing );

                }
            }
        });

    }
}