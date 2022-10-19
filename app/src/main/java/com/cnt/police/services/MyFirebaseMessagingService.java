package com.cnt.police.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cnt.police.PatrollingActivity;
import com.cnt.police.R;
import com.cnt.police.storage.MyPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Arrays;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServ";
    private static final String CHANNEL_ID = "100100";
    private static int notification_id = 0;
    private MyPrefs mPrefs;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        sendRegistrationToServer(s);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        mPrefs = new MyPrefs(this);
        if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
            Map<String, String> map = remoteMessage.getData();
            Log.i(TAG, "onMessageReceived: " + "NOTIFICATION RECEIVED" + Arrays.toString(map.values().toArray()));
            if (map.get("click_action").equals("Emergency")) {
                if (map.containsKey("senderUID") && map.get("senderUID").equals(mPrefs.getUID())) {
                    return;
                }

                showNotification(map);
            }
        }
    }

    private void showNotification(Map<String, String> map) {
        Intent intent = new Intent(this, PatrollingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_local_police_24)
                .setContentTitle(map.get("title"))
                .setContentText(map.get("body"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "CNT_POLICE_EMERGENCY", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Patrolling Mode Notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notification_id++, builder.build());
    }

    private void sendRegistrationToServer(String token) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser() == null ? "" : mAuth.getCurrentUser().getUid();
        Log.w(TAG, "sendRegistrationToServer: >>" + uid + "<<");
        if (!uid.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            WriteBatch batch = db.batch();
            DocumentReference refPoliceUser = db.collection("PoliceUsers")
                    .document(uid);
            DocumentReference refPoliceNotif = db.collection("PoliceNotif")
                    .document(uid);
            batch.update(refPoliceUser, "notification_id", token);
            batch.update(refPoliceNotif, "notification_id", token);
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "onComplete: " + "Messaging Token Updated");
                    }
                }
            });
        }
    }
}
