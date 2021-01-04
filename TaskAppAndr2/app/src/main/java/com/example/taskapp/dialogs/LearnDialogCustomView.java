package com.example.taskapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.taskapp.R;
import com.example.taskapp.ui.auth.PhoneFragment;


public class CustomViewDialog extends AppCompatDialogFragment implements TextWatcher{
    private EditText otpTextOne, otpTextTwo, otpTextThree, otpTextFore;
    private TextView countDownText;
    private CountDownTimer countDownTimer;
    private long timeLeftMillies = 60000;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dailog_view, null);
        builder.setView(R.layout.custom_dailog_view)
                .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        otpTextOne = view.findViewById(R.id.editView_one);
        otpTextTwo = view.findViewById(R.id.editView_two);
        otpTextThree = view.findViewById(R.id.editView_three);
        otpTextFore = view.findViewById(R.id.editView_fore);
        countDownText = view.findViewById(R.id.timer);
        timeRunner();
        return builder.create();


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addTextWatcher();

    }

    private void addTextWatcher() {
        otpTextOne.addTextChangedListener(this);
        otpTextTwo.addTextChangedListener(this);
        otpTextThree.addTextChangedListener(this);
        otpTextFore.addTextChangedListener(this);
    }

    private void timeRunner() {
        countDownTimer = new CountDownTimer(timeLeftMillies,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                String secondLeft = String.valueOf(millisUntilFinished / 1000);
//                countDownText.setText("Second left: " + secondLeft);
                countDownText.setText("sec left " +millisUntilFinished/1000);
                countDownText.invalidate();
                Log.e("one", countDownText.getText().toString());

            }
            @Override
            public void onFinish() {
                dismiss();
                PhoneFragment.errorPhone.setVisibility(View.VISIBLE);

            }
        }.start();
    }




    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 1) {
            if (otpTextOne.length() == 1) {
                otpTextTwo.requestFocus();
            }
            if (otpTextTwo.length() == 1) {
                otpTextThree.requestFocus();
            }
            if (otpTextThree.length() == 1) {
                otpTextFore.requestFocus();
            }
        } else if (s.length() == 0) {
            if (otpTextFore.length() == 0) {
                otpTextThree.requestFocus();
            }
            if (otpTextThree.length() == 0) {
                otpTextTwo.requestFocus();
            }
            if (otpTextTwo.length() == 0) {
                otpTextOne.requestFocus();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
