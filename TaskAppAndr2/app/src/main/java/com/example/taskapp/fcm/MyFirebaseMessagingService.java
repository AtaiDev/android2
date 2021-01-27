package com.example.taskapp.fcm;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null){
            Log.e("FCM", "onMessageReceived: "+ remoteMessage.getNotification().getTitle());
            Log.e("FCM", "onMessageReceived: "+ remoteMessage.getNotification().getBody());
        }

    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("FCM", "onNewToken: "+ s);

    }
}
