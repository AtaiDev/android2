package com.example.taskapp.ui.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskapp.App;
import com.example.taskapp.models.Note;
import com.example.taskapp.R;
import com.example.taskapp.interfaces.OnItemClickListener;


import java.util.List;

public class HomeFragment extends Fragment {

    public static final String EDIT_KEY = "edit_key";
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private Note note;
    private List<Note> list;


    private boolean isEditing = false;// helps to figure out to detect if we are going to add or edit
    private int posEdited;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskAdapter = new TaskAdapter();
        list = App.getDataBase().noteDao().getAll();
        taskAdapter.addList(list);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        initView();
        setFragmentListener(view);
    }


    private void setFragmentListener(View view) {
        // btn fub operation to second form fragment
        view.findViewById(R.id.fab).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_navigation_home_to_formFragment);
            isEditing = false;
//            Navigation.findNavController(getView()).navigate(R.id.action_navigation_home_to_formFragment); shortcut
        });
        // getting  data from the form fragment;
        getParentFragmentManager().setFragmentResultListener("rk_task", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                note = (Note) result.getSerializable("note");
                if (isEditing) {
                    taskAdapter.updateItem(posEdited, note);
//                    App.getDataBase().noteDao().updateItem(note);
                } else {
                    taskAdapter.addItem(note);
//                    App.getDataBase().noteDao().insert(note);
                }
            }
        });
    }
    private void initView() {
        recyclerView.setAdapter(taskAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
        taskAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                posEdited = position;
                Bundle bundle = new Bundle();
                bundle.putSerializable(EDIT_KEY, taskAdapter.getItem(position));
//           another way of sending bundle into another fragment;
//           you have to handle this in the next fragment with
//           getParentFragmentManager().setFragmentResultListener("rk_task", getViewLifecycleOwner(), new FragmentResultListener() {
//             getParentFragmentManager().setFragmentResult("key_edit", bundle); <----- this one sends sets the bundle into result;
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_home_to_formFragment,bundle);
                isEditing = true;
            }
            @Override
            public void onLongClick(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                        .setView(R.layout.alert_dialog_view)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            App.getDataBase().noteDao().delete(taskAdapter.getItem(position));
                            taskAdapter.deleteElement(position);
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {   //!!!keep deference 🛑
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(requireContext(), "You are back" + which, Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.create().show();
            }
        });
    }
}