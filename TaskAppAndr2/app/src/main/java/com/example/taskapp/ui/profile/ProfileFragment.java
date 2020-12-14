package com.example.taskapp.ui.profile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.taskapp.R;
import com.example.taskapp.ui.notifications.NotificationsViewModel;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class ProfileFragment extends Fragment {
    private ImageView imageView;
    private ProfileViewModel profileViewModel;
    private ConstraintLayout constraintLayout;
    private Drawable drawable;

    ActivityResultLauncher<String> contentGetter = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {

            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(result);
                drawable = Drawable.createFromStream(inputStream, result.toString());
                constraintLayout.setBackground(drawable);
            }catch (FileNotFoundException a){
                a.getMessage();
            }

            RequestOptions requestOptions = new RequestOptions()
                    .fitCenter()
                    .circleCrop();
            Glide.with(requireContext())
                    .load(result)
                    .apply(requestOptions)
                    .into(imageView);

        }
    });


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        constraintLayout = root.findViewById(R.id.constraint);
        imageView = root.findViewById(R.id.imageViewProfile);
        imageViewLuncher();

        return root;
    }



    private void imageViewLuncher() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentGetter.launch("image/*");
            }
        });
    }

}