package com.cnt.police.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.cnt.police.R;
import com.cnt.police.models.Feed;
import com.cnt.police.storage.MyPrefs;
import com.cnt.police.ui.dialogs.LoadingDialog;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class CreateFeedFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST_CODE = 1101;
    private MaterialButton btnSendFeed;
    private MaterialAutoCompleteTextView txtFeedTitle;
    private TextInputEditText txtFeedDescription;
    private ImageView imgCreateFeed;
    private Feed mFeed;
    private MyPrefs mPrefs;
    private Bitmap mBitmap;
    private DocumentReference mDocumentReference;
    private LoadingDialog mLoadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtFeedTitle = view.findViewById(R.id.txtCreateFeedTitle);
        txtFeedDescription = view.findViewById(R.id.txtCreateFeedDescription);
        imgCreateFeed = view.findViewById(R.id.imgCreateFeed);
        btnSendFeed = view.findViewById(R.id.btnSendFeed);
        mPrefs = new MyPrefs(requireContext());
        mFeed = new Feed();
        mLoadingDialog = new LoadingDialog();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mDocumentReference = db.collection("Feeds").document();

        imgCreateFeed.setOnClickListener(v -> {
            ChooseFromGallery();
        });

        btnSendFeed.setOnClickListener(v -> {
            if (validateInputs()) {
                mLoadingDialog.setCancelable(false);
                mLoadingDialog.show(getChildFragmentManager(), "loading_feed");
                if (mBitmap != null) {
                    uploadImageToFirebaseStorage();
                } else {
                    sendFeedToFireStore();
                }
            }
        });
    }

    private void uploadImageToFirebaseStorage() {
        Date date = Calendar.getInstance().getTime();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("feeds")
                .child(mDocumentReference.getId().concat(":").concat(date.toString()).concat(".jpg"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        UploadTask uploadTask = storageReference.putBytes(baos.toByteArray());
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @NonNull
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful() && task.getException() != null) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String url = task.getResult().toString();
                    updateImageUrlOfFeed(url);
                }
            }
        });
    }

    private void updateImageUrlOfFeed(String url) {
        mFeed.setFeedImgUrl(url);
        sendFeedToFireStore();
    }

    private void ChooseFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(
                Intent.createChooser(
                        pickIntent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST_CODE);
    }

    private boolean validateInputs() {
        if (txtFeedTitle.getText() != null && txtFeedDescription.getText() != null) {
            if (txtFeedTitle.getText().toString().isEmpty()) {
                txtFeedTitle.setError("Empty Title");
                return false;
            }
            if (txtFeedDescription.getText().toString().isEmpty()) {
                txtFeedDescription.setError("Empty Description");
                return false;
            }
            mFeed.setFeedTitle(txtFeedTitle.getText().toString().trim());
            mFeed.setDescription(txtFeedDescription.getText().toString().trim());
            mFeed.setCreatorID(mPrefs.getUID());
            mFeed.setCreatorLocation(mPrefs.getCity());
            mFeed.setCreatorName(mPrefs.getName());
            mFeed.setCreatorDesignation(mPrefs.getDesignation());
            mFeed.setFeedImgUrl("");
            mFeed.setTimestamp(new Timestamp(Calendar.getInstance().getTime()));
            return true;
        } else {
            Toast.makeText(getActivity(), "Check Field Values", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void sendFeedToFireStore() {
        mDocumentReference.set(mFeed)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mLoadingDialog.dismiss();
                            Toast.makeText(getActivity(), "Feed Created Successfully!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                                    .navigateUp();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri filePath = data.getData();
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgCreateFeed.setImageBitmap(mBitmap);
            imgCreateFeed.setAlpha(1f);
            imgCreateFeed.setForeground(null);
        }
    }
}