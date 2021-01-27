package com.example.taskapp.ui.profile;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.taskapp.App;
import com.example.taskapp.R;
import com.example.taskapp.utils.Prefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;


public class ProfileFragment extends Fragment {
    private ImageView imageView;
    private ProfileViewModel profileViewModel;
    private ConstraintLayout constraintLayout;
    private Drawable drawable;
    private View layoutProfile;
    private View layoutProgress;
    private ProgressBar progressBar;
    private TextView textProgress;

    /* upload to the firsote storage below */
    public static Uri uriImage;
    private StorageReference storeRef;
    private View view;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initView(view);
        getStoredImagePref();

        imageViewLuncher();

//        downloadFile();

        return view;
    }

    private void getStoredImagePref() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (preferences.getString("image", null) != null) {
            String imageStored = preferences.getString("image", null);


            Glide.with(requireContext())
                    .load(imageStored)
                    .circleCrop()
                    .into(imageView);

        }
    }

    private void downloadFile() {
        Log.e("TAG", "downloadFile: " + imageView.toString());


        Uri downLoaded = storeRef.child("images/0b02a274-266f-4477-8ac1-ea63d47b2384.jpeg").getDownloadUrl().getResult();
        Glide.with(requireContext())
                .load(downLoaded)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }


    private void initView(View view) {
        this.view = view;
        constraintLayout = view.findViewById(R.id.constraint);
        imageView = view.findViewById(R.id.imageViewProfile);

        progressBar = view.findViewById(R.id.progress_profile);
        layoutProfile = view.findViewById(R.id.layout_profile);
        layoutProgress = view.findViewById(R.id.progress_view_layout);
        textProgress = view.findViewById(R.id.txtPercentage);

        storeRef = FirebaseStorage.getInstance().getReference();

    }


    private void imageViewLuncher() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentGetter.launch("image/*");
            }
        });
    }


    ActivityResultLauncher<String> contentGetter = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            clearImages();

            try {
                if (result != null) uriImage = result;
                InputStream inputStream = getActivity().getContentResolver().openInputStream(result);
                drawable = Drawable.createFromStream(inputStream, result.toString());
                /*saves the image into sharedPref*/
                savePrefImage();

            } catch (FileNotFoundException a) {
                a.getMessage();
            }
            /*specific options for Glide to request for the looking of the picture*/
            RequestOptions requestOptions = new RequestOptions()
                    .fitCenter()
                    .circleCrop();
            /*glide itself*/
            Glide.with(requireContext())
                    .load(result)
                    .apply(requestOptions)
                    .into(imageView);
            uploadToStorage();
        }

    });
        /*clear before loading the image , avoiding loading on top*/
    private void clearImages() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    private void savePrefImage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("image", String.valueOf(uriImage));
        editor.apply();
    }

    private void uploadToStorage() {
        /*deprecated one to show the progress bar*/
//        final ProgressDialog pd = new ProgressDialog(requireContext());
//        pd.setTitle("Uploading an image . . .");
//        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        storeRef = storeRef.child("images/" + randomKey);
        storeRef.putFile(uriImage)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        showProgress();
                        double progressIndicator = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressBar.setProgress((int) progressIndicator);
                        textProgress.setText(progressBar.getProgress() + " %");
                        Log.e("one", "onProgress: " + progressBar.getProgress());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        constraintLayout.setBackground(drawable);
                        hideProgress();
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Image Uploaded....", Snackbar.LENGTH_LONG)
                                .setAction("View", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(requireContext(), "olololo", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(), "Failed to upload file", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showProgress() {
        layoutProgress.setVisibility(View.VISIBLE);
        layoutProfile.setVisibility(View.GONE);
        constraintLayout.setBackground(null);
    }

    public void hideProgress() {
        layoutProgress.setVisibility(View.GONE);
        layoutProfile.setVisibility(View.VISIBLE);
    }

}