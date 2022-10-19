package com.cnt.police;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PoliceVerificationActivity extends AppCompatActivity {

    private static final String TAG = "PoliceVerificationActiv";
    private static final int PICK_IMAGE_REQUEST_CODE = 1101;
    private static final int CAMERA_PERMISSION_CODE = 222;
    static int CAMERA_REQUEST_CODE = 1100;
    private MaterialButton btnChoose, btnEdit, btnUpload;
    private String cameraPhotoPath;
    private ImageView imgID;
    private String uid;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_verification);
        initViews();
        btnChoose.setOnClickListener(v -> showImageChooseDialog());
        btnEdit.setOnClickListener(v -> showImageChooseDialog());
        btnUpload.setOnClickListener(v -> uploadImageToFirebaseStorage());
    }

    private void uploadImageToFirebaseStorage() {
        if (mBitmap == null)
            return;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser() == null ? "" : mAuth.getCurrentUser().getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference()
                .child("police_ids")
                .child(uid + ".jpg");
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
                    updateVerificationIdUrl(url);
                }
            }
        });
    }

    private void updateVerificationIdUrl(String url) {
        Map<String, Object> map = new HashMap<>();
        map.put("verification_id_url", url);
        map.put("isVerified", null);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PoliceUsers")
                .document(uid)
                .update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(PoliceVerificationActivity.this, VerificationStatusActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(PoliceVerificationActivity.this, "Something Went Wrong",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showImageChooseDialog() {
        AlertDialog.Builder picDialog = new AlertDialog.Builder(this);
        picDialog.setTitle("Choose an Action");
        String[] picDialogItems = {
                "Choose from Gallery",
                "Capture from Camera"};
        picDialog.setItems(picDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which) {
                    case 0:
                        ChooseFromGallery();
                        break;
                    case 1:
                        ClickFromCamera();
                        break;
                }
            }
        });
        picDialog.show();
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        cameraPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void ClickFromCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "com.cnt.police.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        }
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


    private void initViews() {
        btnChoose = findViewById(R.id.btn_ID_Choose);
        imgID = findViewById(R.id.imgID_Card);
        btnEdit = findViewById(R.id.btn_ID_Edit);
        btnUpload = findViewById(R.id.btn_ID_Upload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mBitmap = BitmapFactory.decodeFile(cameraPhotoPath);
            imgID.setImageBitmap(mBitmap);
        } else if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri filePath = data.getData();
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgID.setImageBitmap(mBitmap);
        }
        btnChoose.setVisibility(View.GONE);
        btnEdit.setVisibility(View.VISIBLE);
        btnUpload.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ClickFromCamera();
            } else {
                Toast.makeText(this, "Camera Permission Required", Toast.LENGTH_LONG).show();
            }
        }
    }
}