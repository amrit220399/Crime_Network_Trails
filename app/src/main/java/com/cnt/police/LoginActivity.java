package com.cnt.police;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cnt.police.ui.dialogs.PhoneVerifyBottomDialog;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int PHONE_HINT_REQUEST_CODE = 1221;
    //    private LottieAnimationView animView;
    private TextInputEditText etxtPhone;
    private MaterialButton btnNext;
    private boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

//        animView.setProgress(0.75f);

        etxtPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                HintRequest hintRequest = new HintRequest.Builder()
                        .setPhoneNumberIdentifierSupported(true)
                        .build();
                PendingIntent pendingIntent = Credentials.getClient(LoginActivity.this).getHintPickerIntent(hintRequest);
                try {
                    startIntentSenderForResult(
                            pendingIntent.getIntentSender(),
                            PHONE_HINT_REQUEST_CODE,
                            null,
                            0,
                            0,
                            0,
                            null
                    );
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

        btnNext.setOnClickListener(v -> {
            String phoneNumber = etxtPhone.getText().toString().trim();
            if (phoneNumber.length() != 10) {
                etxtPhone.setError("Enter a valid mobile number");
                etxtPhone.setFocusable(true);
            } else {
                PhoneVerifyBottomDialog bottomDialog = PhoneVerifyBottomDialog.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("phoneNumber",phoneNumber);
                bottomDialog.setArguments(bundle);
                bottomDialog.setCancelable(false);
                bottomDialog.show(getSupportFragmentManager(),"verify_phone_dialog");
//                Intent intent = new Intent(LoginActivity.this, PhoneAuthActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== PHONE_HINT_REQUEST_CODE && data!=null){
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            if(credential!=null && !credential.getId().isEmpty()) {
                String id = credential.getId();
                etxtPhone.setText(id.substring(id.length() - 10));
            }
        }
    }

    private void initViews() {
//        animView = findViewById(R.id.LA_animationView);
        etxtPhone = findViewById(R.id.etxtPhone);
        btnNext = findViewById(R.id.btnNext);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}