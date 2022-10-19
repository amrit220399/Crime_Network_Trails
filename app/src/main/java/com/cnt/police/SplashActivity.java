package com.cnt.police;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.transition.Fade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.cnt.police.storage.MyPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    private static final int MAIN_ACTIVITY_CODE = 1001;
    private static final int REGISTER_ACTIVITY_CODE = 1002;
    private static final int LOGIN_ACTIVITY_CODE = 1003;
    private static final int VERIFICATION_ACTIVITY_CODE = 1004;
    private LottieAnimationView animView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Handler mHandler;
    private MyPrefs mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = new MyPrefs(this);
        if (mPrefs.isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_splash);
        animView = findViewById(R.id.animationView);
        db = FirebaseFirestore.getInstance();

        //NAVIGATION LOGIC STARTS
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == MAIN_ACTIVITY_CODE) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (msg.what == REGISTER_ACTIVITY_CODE) {
                    Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            SplashActivity.this, animView, Objects.requireNonNull(ViewCompat.getTransitionName(animView))
                    );
//                    Fade fade = new Fade();
//                    getWindow().setEnterTransition(fade);
//                    getWindow().setExitTransition(fade);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent, options.toBundle());
//                    finishAfterTransition();
//                   supportFinishAfterTransition();
//                    finish();
                    return true;
                } else if (msg.what == LOGIN_ACTIVITY_CODE) {
                    Fade fade = new Fade();
                    getWindow().setEnterTransition(fade);
                    getWindow().setExitTransition(fade);
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            SplashActivity.this, animView, Objects.requireNonNull(ViewCompat.getTransitionName(animView))
                    );
                    startActivity(intent, options.toBundle());
                    finish();
                    return true;
                } else if (msg.what == VERIFICATION_ACTIVITY_CODE) {
                    Intent intent = new Intent(SplashActivity.this, VerificationStatusActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
        //NAVIGATION LOGIC ENDS

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null){

            db.collection("PoliceUsers")
            .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                if (task.getResult() != null && task.getResult().exists()) {
                                    if (task.getResult().getBoolean("isVerified") == null ||
                                            !task.getResult().getBoolean("isVerified")) {
                                        mHandler.sendEmptyMessageDelayed(VERIFICATION_ACTIVITY_CODE, 1000);
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(MAIN_ACTIVITY_CODE, 1500);
                                    }
                                } else {
                                    mHandler.sendEmptyMessageDelayed(REGISTER_ACTIVITY_CODE, 1000);
                                }
                            } else {
                                mHandler.sendEmptyMessageDelayed(LOGIN_ACTIVITY_CODE, 2000);
                            }
                        }
                    });

        }else {
            mHandler.sendEmptyMessageDelayed(LOGIN_ACTIVITY_CODE,2000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}