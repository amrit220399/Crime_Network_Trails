package com.cnt.police.ui.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cnt.police.MainActivity;
import com.cnt.police.R;
import com.cnt.police.RegisterActivity;
import com.cnt.police.VerificationStatusActivity;
import com.cnt.police.storage.MyPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mukesh.OtpView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PhoneVerifyBottomDialog extends BottomSheetDialogFragment {

    private static final String TAG = "PhoneVerifyBottomDialog";
    private TextView txtTimer, txtResendOtp, txtOtpStatus;
    private OtpView mOtpView;
    private ImageView imgDismiss;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private int counter;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private MyPrefs mPrefs;

    public static PhoneVerifyBottomDialog newInstance() {
        return new PhoneVerifyBottomDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_bottom_verify_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        mAuth = FirebaseAuth.getInstance();
        mAuth.useAppLanguage();
        db = FirebaseFirestore.getInstance();
        mPrefs = new MyPrefs(requireContext());
        String phoneNumber = getArguments().getString("phoneNumber");

        // [END initialize_auth]

        // Initialize phone auth callbacks

        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                mOtpView.setText(phoneAuthCredential.getSmsCode());
                mOtpView.setEnabled(false);
                //signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(getActivity(),"Invalid Request",Toast.LENGTH_LONG).show();
                    dismiss();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(getActivity(),"SMS Quota Exceeded! Please try after 24 hours.",Toast.LENGTH_LONG).show();
                    dismiss();
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };
        // [END phone_auth_callbacks]

        startTimer();
        startPhoneNumberVerification("+91".concat(phoneNumber));

        imgDismiss.setOnClickListener(v -> {
            dismiss();
        });

        mOtpView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()==6){
                    verifyPhoneNumberWithCode(mVerificationId,s.toString());
                }
            }
        });

        txtResendOtp.setOnClickListener(v -> {
            txtResendOtp.setVisibility(View.INVISIBLE);
            txtTimer.setVisibility(View.VISIBLE);
            startTimer();
            resendVerificationCode("+91".concat(phoneNumber),mResendToken);
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && task.getResult()!=null) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            mPrefs.setUID(user.getUid());
                            db.collection("PoliceUsers")
                                    .document(user != null ? user.getUid() : "")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult() != null && task.getResult().exists()) {
                                                    mPrefs.setCity(task.getResult().getString("posted_city"));
                                                    mPrefs.setPS(task.getResult().getString("police_station_id"));
                                                    mPrefs.setName(task.getResult().getString("police_name"));
                                                    mPrefs.setDesignation(task.getResult().getString("designation"));
                                                    FirebaseMessaging.getInstance().subscribeToTopic(mPrefs.getCity());

                                                    if (task.getResult().getBoolean("isVerified") == null
                                                            || !task.getResult().getBoolean("isVerified")) {
                                                        updateNotificationID(task.getResult().getId());
                                                        Intent intent = new Intent(getActivity(), VerificationStatusActivity.class);
                                                        startActivity(intent);
                                                        requireActivity().finish();
                                                    } else {
                                                        updateNotificationID(task.getResult().getId());
                                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                                        startActivity(intent);
                                                        requireActivity().finish();
                                                    }
                                                }else{
                                                    Intent intent = new Intent(getActivity(), RegisterActivity.class);
                                                    startActivity(intent);
                                                    requireActivity().finish();
                                                }

                                            }
                                        }
                                    });
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getActivity(), "Invalid OTP Entered", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void updateNotificationID(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();
        DocumentReference refPoliceUser = db.collection("PoliceUsers")
                .document(uid);
        DocumentReference refPoliceNotif = db.collection("PoliceNotif")
                .document(uid);
        FirebaseMessaging
                .getInstance()
                .getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        batch.update(refPoliceUser, "notification_id", token);
                        Map<String, String> map = new HashMap<>();
                        map.put("notification_id", token);
                        batch.set(refPoliceNotif, map);
                        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "onComplete: " + "NOTIFICATION TOKEN UPDATED");
                                } else {
                                    Toast.makeText(getActivity(), "" + task.getException().getMessage()
                                            , Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
    }
    // [END sign_in_with_phone]

    private void initViews(View view) {
        txtTimer = view.findViewById(R.id.txtTimer);
        mOtpView = view.findViewById(R.id.otp_view);
        imgDismiss = view.findViewById(R.id.imgDismiss);
        txtOtpStatus = view.findViewById(R.id.txt_otp_status);
        txtResendOtp = view.findViewById(R.id.txtResend);
    }

    private void startTimer(){
        counter = 30;
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                txtTimer.setText("00:".concat(String.format(Locale.getDefault(),"%02d", counter)));
                counter--;
            }

            public void onFinish() {
                txtTimer.setVisibility(View.INVISIBLE);
                txtResendOtp.setVisibility(View.VISIBLE);
            }
        }.start();
    }
}
