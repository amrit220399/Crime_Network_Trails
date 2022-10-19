package com.cnt.police.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.cnt.police.R;
import com.cnt.police.storage.MyPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsFragment extends Fragment {

    private SwitchMaterial switchNotifications, switchDarkMode;
    private MyPrefs mPrefs;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPrefs = new MyPrefs(requireContext());
        switchNotifications = view.findViewById(R.id.switchNotification);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        db = FirebaseFirestore.getInstance();

        switchNotifications.setChecked(mPrefs.isNotificationON());

        switchDarkMode.setChecked(mPrefs.isNightMode());
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                turnOnNotifications();
            } else {
                turnOffNotifications();
            }
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                mPrefs.setNightMode(true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                mPrefs.setNightMode(false);
            }
        });
    }

    private void turnOffNotifications() {
        db.collection("PoliceNotif")
                .document(mPrefs.getUID())
                .update("notification_ON", false).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mPrefs.setNotificationMode(false);
                    Toast.makeText(requireActivity(), "Notifications Turned OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void turnOnNotifications() {
        db.collection("PoliceNotif")
                .document(mPrefs.getUID())
                .update("notification_ON", true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mPrefs.setNotificationMode(true);
                    Toast.makeText(requireActivity(), "Notifications Turned ON", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}