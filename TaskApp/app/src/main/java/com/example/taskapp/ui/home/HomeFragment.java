package com.example.taskapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskapp.R;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements ClickListener {
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private ArrayList<String> list;
    private String text;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TaskAdapter(this);
        if (savedInstanceState != null) {
            Log.e("ololo", "onCreate: " + savedInstanceState.getStringArrayList("listState"));
            list = savedInstanceState.getStringArrayList("listState");
        } else {
            list = new ArrayList<>();
            list.add("Aijan");
            list.add("Azima");
            list.add("Nurjan");
            list.add("Bema");
            list.add("Tansuluu");
            list.add("Duulat");
            list.add("Erdem");
        }
        adapter.addList(list);

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
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_navigation_home_to_formFragment);
            }
        });
        fragmentResultListener();
    }


    private void fragmentResultListener() {
        getParentFragmentManager().setFragmentResultListener("rk_task", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                text = result.getString("text");
                adapter.addItem(text);

            }
        });

    }


    private void initView() {
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }


    @Override
    public void clickForPosition(int position) {
        Toast.makeText(getContext(), "position you've selected " + (++position), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("listState", list);

    }


}

interface ClickListener {
    void clickForPosition(int position);
}