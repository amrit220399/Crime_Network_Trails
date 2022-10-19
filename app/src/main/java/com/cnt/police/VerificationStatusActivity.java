package com.cnt.police;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class VerificationStatusActivity extends AppCompatActivity {

    private static final String TAG = "VerificationStatusActiv";
    private ListenerRegistration mListenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_status);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser() == null ? "" : mAuth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mListenerRegistration = db.collection("PoliceUsers")
                .document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, "onEvent: " + error.getMessage());
                            return;
                        }
                        if (value.getBoolean("isVerified") == null) {
                            Log.i(TAG, "Pending ID Verification");
                        } else if (value.getBoolean("isVerified")) {
                            Intent intent = new Intent(VerificationStatusActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Navigation.findNavController(VerificationStatusActivity.this, R.id.nav_host_verification_fragment)
                                    .navigate(R.id.action_verificationPendingFragment_to_verificationFailedFragment);
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListenerRegistration.remove();
    }
}