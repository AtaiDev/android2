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

import com.example.taskapp.MainActivity;
import com.example.taskapp.R;



public class FormFragment extends Fragment {

    EditText editText;
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
            private static final String CLICKED = "ololo";
            @Override
            public void onClick(View v) {
                save();
            }
            private void save() {
                    String text = editText.getText().toString();
                    Log.e("FormFragment", "onClick: button clicked" + text);
                    Bundle bundle = new Bundle();
                    bundle.putString("text", text);
                    getParentFragmentManager().setFragmentResult("rk_task", bundle);
                    ((MainActivity) requireActivity()).closeFragment();
            }
        });

    }

    private void retrieveBundle() {
        editText.setText(getArguments().getString("edit"));
        Log.e("ololo", "retrieveBundle: "+getArguments().getString("edit"));
    }
}