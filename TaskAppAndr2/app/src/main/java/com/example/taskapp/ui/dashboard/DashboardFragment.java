package com.example.taskapp.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskapp.R;
import com.example.taskapp.interfaces.OnItemClickListener;
import com.example.taskapp.models.Note;

import com.example.taskapp.ui.home.TaskAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment {
    private static final String TAG = "DashboardFragment";
    public static final String KEY_STORED = "updateFromFirestore";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebook = db.collection("noteBook");
    private NoteAdapterDashboard adapterDashboard;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TaskAdapter taskAdapter;//for the second way of retrieving data;
    private List<Note> list = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //just to keep the same adapter to retrieve the data(second way of getting data);👇

        initRecyProgress();
        //setUpRecycler(view); this one with snapshot, live listener
        /*second way of the getting data from firestore👇*/
        initView();
        loadData();
    }


    private void initRecyProgress() {
        recyclerView = getView().findViewById(R.id.recyclerDashBoard);
        progressBar = getView().findViewById(R.id.progress_bar);

        /*not a best way but just kept to knew this way, might help ones anyway*/
//       timer = new Timer();
//        recyclerView = view.findViewById(R.id.recyclerDashBoard);
//        progressBar = view.findViewById(R.id.progress_bar);
//        TimerTask tt = new TimerTask() {
//            @Override
//            public void run() {
//                counter++;
//                if (counter == 100) {
//                    recyclerView.setVisibility(View.VISIBLE);
//                    progressBar.setVisibility(View.GONE);
//                    timer.cancel();
//                }
//            }
//        };
//        timer.schedule(tt, 0, 100);

    }

    /*below from this all the way down till the note for the first way of getting the data from firestore 🛑🛑🛑🛑🛑🛑🛑  */
    private void setUpRecycler(View view) {
        recyclerView = view.findViewById(R.id.recyclerDashBoard);
        Query query = notebook.orderBy("createdAt", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                /*i could've done like down below, if i did't add removing on swipe to right */
//                .setQuery(query, Note.class)
                .setQuery(query, new SnapshotParser<Note>() {
                    @NonNull
                    @Override
                    public Note parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Note note = snapshot.toObject(Note.class);
                        note.setId(snapshot.getId());
                        return note;
                    }
                })
                .setLifecycleOwner(requireActivity())
                .build();
        adapterDashboard = new NoteAdapterDashboard(options);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapterDashboard);
        itemTouchHelper();
    }

    private void itemTouchHelper() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String docId = (String) viewHolder.itemView.getTag();
                db.collection("noteBook")
                        .document(docId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Element has been removed", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onFailure: " + e.toString());
                            }
                        });
            }
        }).attachToRecyclerView(recyclerView);
        adapterDashboard.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
//        adapterDashboard.startListening();

        /*down below for the auto listening for the changes without recycler if we assigning  straight  to the editText*/
//        notebook.addSnapshotListener(requireActivity(), new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    Toast.makeText(getContext(), "Error while loading", Toast.LENGTH_SHORT).show();
//                    Log.d("one", error.toString());
//                    return;
//                }
//                Query query = notebook.orderBy("createdAt", Query.Direction.DESCENDING);
//                FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
//                        .setQuery(query, Note.class)
//                        .build();
//            }
//        });
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: stopListening was called");
//        adapterDashboard.stopListening();
    }


    /*Below from this all for  getting a data from firestore in a second way 📕📕📕📕*/
    private void initView() {
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));
        taskAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_STORED, list.get(position));
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_dashboard_to_formFragment, bundle);
                taskAdapter.clearList(list);
            }

            @Override
            public void onLongClick(int position) {
                if (!list.isEmpty()) {
                    String id = list.get(position).getId();
                    AlertDialog.Builder alert = new AlertDialog.Builder(requireContext())
                            .setTitle("Are you sure to delete?")
                            .setMessage("Deleted item won't be retrieved!")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.collection("noteBook")
                                            .document(id)
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        taskAdapter.deleteElement(position);
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(requireContext(), "Has been deleted", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(requireContext(), "You're back to business!", Toast.LENGTH_SHORT).show();
                                }
                            });
                    alert.create().show();
                }
            }
        });
    }

    /*loads the data from the firebase firestore and will show the progress bar till addOnComplete is successful*/
    private void loadData() {
        db.collection("noteBook")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    String documentId = snapshot.getId();
                                    list.add(snapshot.toObject(Note.class));

                            }
//                            List<Note> list = task.getResult().toObjects(Note.class);
                            taskAdapter.addList(list);
                        }
                    }
                });
    }


}
