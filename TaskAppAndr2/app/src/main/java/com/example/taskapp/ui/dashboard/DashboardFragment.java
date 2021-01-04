package com.example.taskapp.ui.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskapp.R;
import com.example.taskapp.models.Note;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;


import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;


public class DashboardFragment extends Fragment {
    private static final String TAG = "DashboardFragment";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebook = db.collection("noteBook");
    private NoteAdapterDashboard adapterDashboard;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        timerProgress();
        setUpRecycler(view);

    }

    private void timerProgress() {
        final Handler mHandler = new Handler();
        Runnable mRunnable = () -> {
            progressBar = getView().findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        };
        mHandler.postDelayed(mRunnable, 1500);

        /*just to know that we can achieve in this way too*/
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
                        note.setDocumentId(snapshot.getId());
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
        adapterDashboard.startListening();

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
        adapterDashboard.stopListening();
    }

}
