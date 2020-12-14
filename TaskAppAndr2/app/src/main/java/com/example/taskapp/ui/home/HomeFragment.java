package com.example.taskapp.ui.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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

import com.example.taskapp.MainActivity;
import com.example.taskapp.R;
import com.example.taskapp.interfaces.OnItemClickListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private String text;
    private ArrayList<String> list;

    private int positionEdited;
    private boolean isEditing = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskAdapter = new TaskAdapter();
        list = new ArrayList<>();
        list.add("Omurzak");
        list.add("Mahabat");
        list.add("Nazar");
        list.add("Ainazik");
        list.add("Ermik");
        list.add("Daniar");
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
        view.findViewById(R.id.fab).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_navigation_home_to_formFragment);
//            Navigation.findNavController(getView()).navigate(R.id.action_navigation_home_to_formFragment); shortcut
        });
        setFragmentListener();
    }

    private void setFragmentListener() {
        getParentFragmentManager().setFragmentResultListener("rk_task", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if (!isEditing) {
                    text = result.getString("text");
                    taskAdapter.addItem(text);
                }else {
                    //**************************
                    text = result.getString("text");
                    taskAdapter.setElement(positionEdited, text);
                    isEditing = false;
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
                positionEdited = position;

                Bundle bundle = new Bundle();
                bundle.putString("edit",list.get(position));
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_home_to_formFragment,bundle);
                isEditing = true;


            }

            @Override
            public void onLongClick(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                        .setView(R.layout.alert_dialog_view)
                        .setPositiveButton("Yes", (dialog, which) -> taskAdapter.deleteElement(position))
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