package com.example.taskapp.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskapp.MainActivity;
import com.example.taskapp.R;
import com.example.taskapp.dialogs.LearnDialogCustomView;
import com.example.taskapp.models.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class PhoneFragment extends Fragment implements TextWatcher {
    private View viewPhone;
    private View viewCode;
    private EditText editPhone;
    private TextView textTimer,errorInform;
    private EditText otpEditTextFirst, otpEditTextSecond, otpEditTextThird, otpEditTextForth, otpEditTextFives,otpEditTextSix;
    private EditText otpEditText;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String verificationID;
    private CountDownTimer timer;
    private boolean isCodeSent = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        showPhoneView();//method to show the initial view for the OTP request;
        view.findViewById(R.id.btnNext).setOnClickListener(v -> requestSms());
        view.findViewById(R.id.btnCode).setOnClickListener(v -> confirm());
        initCallback();
        addsTextWatcher();

    }

    private void addsTextWatcher() {
        otpEditTextFirst.addTextChangedListener(this);
        otpEditTextSecond.addTextChangedListener(this);
        otpEditTextThird.addTextChangedListener(this);
        otpEditTextForth.addTextChangedListener(this);
    }

    private void initializeViews(View view) {
        editPhone = view.findViewById(R.id.editPhone);
        viewPhone = view.findViewById(R.id.viewPhone);
        errorInform = viewPhone.findViewById(R.id.txtInput_error);
        //CodeView for the second one
        viewCode = view.findViewById(R.id.viewCode);
//        otpEditText = view.findViewById(R.id.edit_code_otp);
        otpEditTextFirst = view.findViewById(R.id.otp_edit_first);
        otpEditTextSecond = view.findViewById(R.id.otp_edit_second);
        otpEditTextThird = view.findViewById(R.id.otp_edit_third);
        otpEditTextForth = view.findViewById(R.id.otp_edit_four);
        otpEditTextFives = view.findViewById(R.id.otp_edit_fives);
        otpEditTextSix = view.findViewById(R.id.otp_edit_six);
        textTimer = view.findViewById(R.id.textTimer);

    }

    private void confirm() {
        String codeAllOne = otpEditTextFirst.getText().toString().trim()
                + otpEditTextSecond.getText().toString().trim()
                + otpEditTextThird.getText().toString().trim()
                + otpEditTextForth.getText().toString().trim()
                + otpEditTextFives.getText().toString().trim()
                + otpEditTextSix.getText().toString().trim();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, codeAllOne);
        singIn(credential);
    }

    private void showPhoneView() {
        viewPhone.setVisibility(View.VISIBLE);
        viewCode.setVisibility(View.GONE);
    }

    private void showCodeView() {
        viewPhone.setVisibility(View.GONE);
        viewCode.setVisibility(View.VISIBLE);
        startTimer();
    }

    private void startTimer() {
            timer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String second = String.valueOf(millisUntilFinished/1000);
                textTimer.setText("00:"+second);
                if (second.length() < 2) {textTimer.setText("00:0"+second);}
                Log.e("one", "onTick: "+textTimer.getText().toString());
            }
            @Override
            public void onFinish() {
                showPhoneView();
                errorInform.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void singIn(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (timer != null) timer.cancel();
                    ((MainActivity) requireActivity()).closeFragment();
                } else {
                    Toast.makeText(requireContext(), "Error of authentication", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void initCallback() {
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.e("TAG", "onVerificationCompleted: ");
                if (isCodeSent){
                    // here suppoused to be the edit text in single line , since i did in multiple edit i could't use in this way;
//                    otpEditText.setText(phoneAuthCredential.getSmsCode());
                    Toast.makeText(requireActivity(),"Auto fill sms code",Toast.LENGTH_SHORT).show();
                }else {
                    singIn(phoneAuthCredential);
                }

            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("TAG", "onVerificationFailed: " + e.getMessage());
            }
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                isCodeSent = true;
                verificationID = s;
                showCodeView();
            }
        };
    }

    private void requestSms() {
        String phone = "+996"+editPhone.getText().toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(requireActivity())
                        .setCallbacks(callbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
//        showCodeView();


        //this was called when i did this when i opened dialog window which was not best approach
//        LearnDialogCustomView dialog = new LearnDialogCustomView();
//        dialog.show(getParentFragmentManager(), null);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 1){
            if (otpEditTextFirst.length() == 1) otpEditTextSecond.requestFocus();
            if (otpEditTextSecond.length() == 1) otpEditTextThird.requestFocus();
            if (otpEditTextThird.length() == 1) otpEditTextForth.requestFocus();
            if (otpEditTextForth.length() == 1) otpEditTextFives.requestFocus();
            if (otpEditTextFives.length() == 1) otpEditTextSix.requestFocus();
        }else if (s.length() == 0){
            if (otpEditTextSix.length() == 0) otpEditTextFives.requestFocus();
            if (otpEditTextFives.length() == 0) otpEditTextForth.requestFocus();
            if (otpEditTextForth.length() == 0) otpEditTextThird.requestFocus();
            if (otpEditTextThird.length() == 0) otpEditTextSecond.requestFocus();
            if (otpEditTextSecond.length()== 0) otpEditTextFirst.requestFocus();

        }
    }
}