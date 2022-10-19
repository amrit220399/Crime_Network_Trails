package com.cnt.police.ui.dialogs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.cnt.police.R;
import com.cnt.police.SplashActivity;
import com.cnt.police.storage.MyPrefs;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutDialog extends AppCompatDialogFragment {

    private MaterialButton btnYes, btnCancel;
    private MyPrefs mPrefs;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_logout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnYes = view.findViewById(R.id.btnLogoutYes);
        btnCancel = view.findViewById(R.id.btnLogoutCancel);
        mPrefs = new MyPrefs(getActivity());
        mAuth = FirebaseAuth.getInstance();

        btnYes.setOnClickListener(v -> {
            mAuth.signOut();
            mPrefs.setUID("");
            dismiss();
            Intent intent = new Intent(getActivity(), SplashActivity.class);
            startActivity(intent);
            getActivity().finishAffinity();
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }
}
