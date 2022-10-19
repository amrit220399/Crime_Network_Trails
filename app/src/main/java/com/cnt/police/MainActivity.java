package com.cnt.police;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cnt.police.network.RetrofitClient;
import com.cnt.police.network.RetrofitInterface;
import com.cnt.police.ui.dialogs.LoadingDialog;
import com.cnt.police.ui.fragments.CreateFeedFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private static final String TAG = "MainActivity";
    private boolean isFaceSearchClicked = false;
    private RetrofitInterface mClient;
    private File baseFile;
    private NavController navController;
    private BottomNavigationView navView;
    private LoadingDialog loadingDialog;
    private int counter = 0, total;
    private boolean match = false;
    private DrawerLayout drawer;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mClient = RetrofitClient.getInstance().create(RetrofitInterface.class);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navView = findViewById(R.id.bottom_nav_view);
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.bottom_nav_home, R.id.bottom_nav_feeds, R.id.bottom_nav_appointments, R.id.nav_settings,
                R.id.nav_teams, R.id.nav_criminals, R.id.nav_city_officers, R.id.nav_reported_criminals,
                R.id.nav_profile, R.id.nav_user_complaints)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavigationUI.setupWithNavController(navView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_add_criminal) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.OFF)
                            .start(MainActivity.this);
                    drawer.closeDrawer(navigationView);
                    return true;
                } else {
                    boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                    if (handled)
                        drawer.closeDrawer(navigationView);
                    return handled;
                }
            }
        });


        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bottom_nav_face_search) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.OFF)
                            .start(MainActivity.this);
                    isFaceSearchClicked = true;
                    return true;
                }
                return NavigationUI.onNavDestinationSelected(item, navController);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            navView.getMenu().findItem(R.id.bottom_nav_face_search).setChecked(false);
            navView.getMenu().findItem(R.id.bottom_nav_home).setChecked(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof CreateFeedFragment) {
                fragment.onActivityResult(requestCode, resultCode, data);
                break;
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && !isFaceSearchClicked) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result != null) {
                resultUri = result.getUri();
//                Toast.makeText(getActivity(),"FETCHED",Toast.LENGTH_SHORT).show();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    processFaceDetection(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE && result != null) {
                Exception error = result.getError();
                Toast.makeText(this, "FAILED" + error, Toast.LENGTH_LONG).show();
            }
        }
//        if (!isFaceSearchClicked) {
//            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
//                Log.i(TAG, "onActivityResult: " + fragment.getClass());
//                    fragment.onActivityResult(requestCode, resultCode, data);
//                    break;
//
//            }
//        }

        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && isFaceSearchClicked) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            isFaceSearchClicked = false;
            if (resultCode == RESULT_OK && result != null) {
                Uri resultUri = result.getUri();
                baseFile = new File(resultUri.getPath());
                loadingDialog = new LoadingDialog();
                loadingDialog.setCancelable(false);
                loadingDialog.show(getSupportFragmentManager(), "loading_dialog");
                counter = 0;
                match = false;
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference listRef = storage.getReference().child("criminals");
                listRef.listAll()
                        .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(@NonNull ListResult listResult) {
                                total = listResult.getItems().size();
                                for (StorageReference item : listResult.getItems()) {
                                    if (match) {
                                        break;
                                    }
                                    try {
                                        String criminal_id = item.getName();
                                        String criminalID = criminal_id.substring(0, criminal_id.length() - 4);
                                        File tempFile = File.createTempFile("images", "jpg");
                                        item.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                callFaceCompareApi(tempFile, criminalID);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE && result != null) {
                Exception error = result.getError();
                Toast.makeText(this, "FAILED" + error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void processFaceDetection(Bitmap bitmap) {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        FaceDetector detector = FaceDetection.getClient(options);
        Log.i(TAG, "processFaceDetection: ");
        Task<List<Face>> result = detector.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                    @Override
                    public void onSuccess(@NonNull List<Face> faces) {
                        if (faces.size() == 0) {
                            Toast.makeText(MainActivity.this, "Unable to Detect Face!", Toast.LENGTH_SHORT).show();
                        } else if (faces.size() > 1) {
                            Toast.makeText(MainActivity.this, "More than 1 Face Detected!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Face Detected Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, AddCriminalActivity.class);
                            intent.putExtra("imgUri", resultUri.toString());
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });

    }

    private void callFaceCompareApi(File file2, String criminalID) {

        mClient.compareFaces(RequestBody.create(MediaType.parse("text/plain"), getString(R.string.face_api_key)),
                RequestBody.create(MediaType.parse("text/plain"), getString(R.string.face_api_secret)),
                RequestBody.create(MediaType.parse("image/*"), baseFile),
                RequestBody.create(MediaType.parse("image/*"), file2))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getJSONArray("faces1").length() > 0 && jsonObject.getJSONArray("faces2").length() > 0) {
                                    if (jsonObject.getDouble("confidence") >= 75) {
                                        loadingDialog.dismiss();
                                        counter = 0;
                                        match = true;
                                        Toast.makeText(MainActivity.this, "Match Found!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, CriminalProfileActivity.class);
                                        intent.putExtra("criminal_id", criminalID);
                                        startActivity(intent);
                                    }
                                }
                                counter++;
                                Log.i(TAG, "onResponse: " + jsonObject.toString());
                                if (counter == total && !match) {
                                    loadingDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "NO Match Found!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e(TAG, "onResponse: CODE " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });

    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}