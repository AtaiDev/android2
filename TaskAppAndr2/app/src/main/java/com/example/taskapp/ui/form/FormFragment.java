package com.example.taskapp.ui.form;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.taskapp.App;
import com.example.taskapp.MainActivity;
import com.example.taskapp.models.Note;
import com.example.taskapp.R;
import com.example.taskapp.ui.dashboard.DashboardFragment;
import com.example.taskapp.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class FormFragment extends Fragment {
    private static final String TAG = "FormFragment";
    private static final String CHANEL_ID = "chanel_notification";
    private static final int NOTIFICATION_ID = 1;


    private EditText editText;
    private Note note;
    private FirebaseFirestore firestore;
    private String text;
    private String dateString;
    private ProgressBar progressLoad;
    private View layoutBtnEdit;


    private boolean isFromDashboard = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        if (getArguments() != null) {
            if (getArguments().getSerializable(DashboardFragment.KEY_STORED) == null)
                retrieveBundle();
            else {
                isFromDashboard = true;
                dashboardRetrieve();
            }
        }
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
                if (note == null && !isFromDashboard) {
                    note = new Note(text, dateString);
                    Log.e(TAG, "save: " + text);
                    /*hides to show the progress bar onClick the btn Save*/
                    hideToShow();
                    addToFirestore(note);

                } else if (isFromDashboard) {
                    Note noteDash = (Note) getArguments().getSerializable(DashboardFragment.KEY_STORED);
                    noteDash.setTitle(editText.getText().toString());
                    updateNoteFirstore(noteDash);
                    Log.e(TAG, "save: " + isFromDashboard);
                    return;
                } else {
                    note.setTitle(text);
                    App.getDataBase().noteDao().updateItem(note);
                    ((MainActivity) requireActivity()).closeFragment();
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("note", note);
                getParentFragmentManager().setFragmentResult("rk_task", bundle);
            }
        });
    }




    private void hideToShow() {
        layoutBtnEdit.setVisibility(View.GONE);
        progressLoad.setVisibility(View.VISIBLE);
    }

    private void initView(View view) {
        editText = view.findViewById(R.id.edit_text);
        firestore = FirebaseFirestore.getInstance();
        progressLoad = view.findViewById(R.id.progress_form);
        layoutBtnEdit = view.findViewById(R.id.hide_on_loading);
    }

    private void addToFirestore(Note note) {
        /*if we want to save data into the firestore we can save with maps , or else we use models*/
//        HashMap<String, Object> noteList = new HashMap<>();
//        noteList.put(TO_DATABASE, noteToEdit);
        /*short cut of getting the instance of firestore 👇*/
//        FirebaseFirestore.getInstance().collection("noteBook").add(note)
        firestore.collection("noteBook").add(note)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {

                            note.setId(task.getResult().getId());
                            App.getDataBase().noteDao().insert(note);
                            updateNoteFirstore(note);
                            // notifies the user about uploaded file in the cloud;
                            sendNotification();

                        } else {
                            Toast.makeText(requireContext(), "Recording failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*updates the element in the firestore since above sets id manually*/
    private void updateNoteFirstore(Note note) {
        firestore.collection("noteBook")
                .document(note.getId())
                .set(note)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Navigation.findNavController(getView()).navigateUp();
                            Toast.makeText(requireContext(), "data is updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Navigation.findNavController(getView()).navigateUp();
                            Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*i send notification on sending the data to the cloud and it works inside the onComplete method 👆*/
    private void sendNotification() {

        Intent mainIntent = new Intent(requireActivity(),MainActivity.class);

        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                          | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivities(requireActivity(),0, new Intent[]{mainIntent},0);

        Bitmap bitmap = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.alien);


        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(requireContext(),CHANEL_ID)
                .setSmallIcon(R.drawable.cloud_done)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle("TakApp notification").setLargeIcon(bitmap)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setShowWhen(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText("Just uploaded "+ editText.getText().toString()+" into cloud")
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null));

        ////////////*🛑  if i use the Notification instead of NotificationCompat.Builder*//////////
        //notificationManager.notify(NOTIFICATION_ID,builder);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify(NOTIFICATION_ID,notBuilder.build());



    }


    // retrieving the data to edit from the homeFragment 👇
    private void retrieveBundle() {
        note = (Note) getArguments().getSerializable(HomeFragment.EDIT_KEY);
        if (note != null) editText.setText(note.getTitle());

        /*this is the another way of retrieving the the data with bundle*/
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
    }


    private void dashboardRetrieve() {
        Note toGetTitle = (Note) getArguments().getSerializable(DashboardFragment.KEY_STORED);
        editText.setText(toGetTitle.getTitle());
        Log.e(TAG, "dashboardRetrieve: " + toGetTitle.getTitle());
    }

}