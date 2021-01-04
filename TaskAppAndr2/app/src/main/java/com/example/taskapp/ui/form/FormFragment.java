package com.example.taskapp.ui.form;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskapp.App;
import com.example.taskapp.MainActivity;
import com.example.taskapp.models.Note;
import com.example.taskapp.R;
import com.example.taskapp.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class FormFragment extends Fragment {
    private static final String TAG = "FormFragment";
    private static final String TO_DATABASE = "formFragment";
    private EditText editText;
    private Note noteToEdit;
    private FirebaseFirestore firestore;
    private String text;
    private String dateString;

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
        firestore = FirebaseFirestore.getInstance();
        if (getArguments() != null) retrieveBundle();

        view.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }

            private void save() {
                text = editText.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm yyyy/MM/dd", Locale.ROOT);
                dateString = dateFormat.format(System.currentTimeMillis());
                //String dateSt = DateFormat.getDateInstance().format(System.currentTimeMillis());<---Dec 16, 2020 in this format;
                // to use in both cases
                if (noteToEdit == null) {
                    noteToEdit = new Note(text, dateString);
                    App.getDataBase().noteDao().insert(noteToEdit);
                    addToFirestore();
                } else {
                    noteToEdit.setTitle(text);
                    App.getDataBase().noteDao().updateItem(noteToEdit);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("note", noteToEdit);
                getParentFragmentManager().setFragmentResult("rk_task", bundle);
                ((MainActivity) requireActivity()).closeFragment();
            }
        });
    }

    private void addToFirestore() {
//        HashMap<String, Object> noteList = new HashMap<>();
//        noteList.put(TO_DATABASE, noteToEdit);
        Note noteList = new Note(text,dateString);
        firestore.collection("noteBook").add(noteList)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e(TAG, "onSuccess: "+documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // retrieving the date to edit from the homefragment 👇
    private void retrieveBundle() {
            noteToEdit = (Note) getArguments().getSerializable(HomeFragment.EDIT_KEY);
            if (noteToEdit != null) editText.setText(noteToEdit.getTitle());

        /*this is the another way of retrieving the the data from bundle*/
//        getParentFragmentManager().setFragmentResultListener("key_edit", getViewLifecycleOwner(), new FragmentResultListener() {
//            @Override
//            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
//                if (result != null) {
//                    noteToEdit = (Note) result.getSerializable("edit");
//                    editText.setText(noteToEdit.getTitle());
//                    timing = noteToEdit.getCreatedAt();
//                    Log.e("ololo", "onFragmentResult: "+ noteToEdit.getTitle() + " " + timing );
//
//                }
//            }
//        });
//
    }
}