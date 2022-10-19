package com.cnt.police;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cnt.police.models.City;
import com.cnt.police.models.PoliceStation;
import com.cnt.police.models.PoliceUser;
import com.cnt.police.storage.MyPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    //    private LottieAnimationView animView;
    private TextInputEditText txtEmailID, txtPoliceName, txtPoliceNumber;
    private MaterialAutoCompleteTextView txtPostedCity, txtDesignation, txtPoliceStation;
    private ArrayList<City> listCity;
    private ArrayList<PoliceStation> policeStationList;
    private RadioButton rYes, rNo;
    private MaterialButton btnProceed;
    private boolean doubleBackToExitPressedOnce = false;
    private PoliceUser mPoliceUser;
    private FirebaseAuth mAuth;
    private String phoneNumber;
    private MyPrefs mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
//        animView.setProgress(0.75f);
        mPoliceUser = new PoliceUser();
        mAuth = FirebaseAuth.getInstance();
        mPrefs = new MyPrefs(this);
        phoneNumber = mAuth.getCurrentUser() == null ? "" :
                mAuth.getCurrentUser().getPhoneNumber();
        txtPoliceNumber.setText(phoneNumber);
        txtPoliceNumber.setEnabled(false);

        txtDesignation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                txtDesignation.setError(null);
            }
        });


        txtPoliceStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txtPoliceStation.setError(null);
                mPoliceUser.setPolice_station_id(((PoliceStation) parent.getItemAtPosition(position)).getName());
                mPoliceUser.setPosted_city(((PoliceStation) parent.getItemAtPosition(position)).getCity());
                mPoliceUser.setPosted_state(((PoliceStation) parent.getItemAtPosition(position)).getState());
                if (((PoliceStation) parent.getItemAtPosition(position)).getName().equals("Others")) {
                    txtPostedCity.setEnabled(true);
                    txtPostedCity.setText("");
                } else {
                    txtPostedCity.setEnabled(false);
                    txtPostedCity.setText(mPoliceUser.getPosted_city());
                }
            }
        });

        txtPostedCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPoliceUser.setPosted_city(((City)parent.getItemAtPosition(position)).getCity());
                mPoliceUser.setPosted_state(((City) parent.getItemAtPosition(position)).getState());
            }
        });

        btnProceed.setOnClickListener(v -> {


            mPoliceUser.setPolice_name(
                    txtPoliceName.getText() != null ?
                            txtPoliceName.getText().toString().trim() : "");
            mPoliceUser.setEmail(
                    txtEmailID.getText() != null ?
                            txtEmailID.getText().toString().trim() : "");
            mPoliceUser.setPhoneNumber(phoneNumber);
            mPoliceUser.setDesignation(
                    txtDesignation.getText() != null ?
                            txtDesignation.getText().toString().trim() : "");
//            mPoliceUser.setPolice_station_id(
//                    txtPoliceStation.getText() !=null ?
//                            txtPoliceStation.getText().toString().trim() : "");
            mPoliceUser.setPosted_city(
                    txtPostedCity.getText() != null ?
                            txtPostedCity.getText().toString().trim() : "");
            mPoliceUser.setPsHead(rYes.isChecked());
            mPoliceUser.setNotification_id("");
            mPoliceUser.setRating(0);
            boolean flag = true;
            if (mPoliceUser.getPolice_name().isEmpty()) {
                flag = false;
                txtPoliceName.setError("Invalid Name");
                return;
            }
//            if(mPoliceUser.getPhoneNumber().length()!=10){
//                flag = false;
//                txtPoliceNumber.setError("Incorrect Phone Number");
//                return;
//            }
            if(mPoliceUser.getEmail().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mPoliceUser.getEmail()).matches()){
                flag = false;
                txtEmailID.setError("Invalid Email Address");
                return;
            }
            if(mPoliceUser.getDesignation().isEmpty() || mPoliceUser.getDesignation().equals("Select Your Designation")){
                flag = false;
                txtDesignation.setError("Please Select Appropriate Value");
                return;
            }
            if(mPoliceUser.getPolice_station_id().isEmpty() || mPoliceUser.getPolice_station_id().equals("Select Police Station")){
                flag = false;
                txtPoliceStation.setError("Please Select Appropriate Value");
                return;
            }
            if (mPoliceUser.getPosted_city().isEmpty() || mPoliceUser.getPosted_city().length() < 3) {
                flag = false;
                txtPostedCity.setError("Please Select Appropriate Value");
                return;
            }
            if (flag) {
                mPoliceUser.setVerified(null);
                mPrefs.setCity(mPoliceUser.getPosted_city());
                mPrefs.setName(mPoliceUser.getPolice_name());
                mPrefs.setDesignation(mPoliceUser.getDesignation());
                mPrefs.setPS(mPoliceUser.getPolice_station_id());

                FirebaseMessaging.getInstance().subscribeToTopic(mPoliceUser.getPosted_city());

                ProgressDialog mProgressDialog = new ProgressDialog(RegisterActivity.this);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setTitle("Profile Creation");
                mProgressDialog.setMessage("Please wait while we upload your data");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.show();
                String uid = mAuth.getCurrentUser() == null ?
                        "" :
                        mAuth.getCurrentUser().getUid();
                if (!uid.isEmpty()) {
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
                                    batch.set(refPoliceUser, mPoliceUser);
                                    batch.update(refPoliceUser, "notification_id", token);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("notification_id", token);
                                    map.put("city", mPoliceUser.getPosted_city());
                                    map.put("notification_ON", true);
                                    map.put("userType", "POLICE_USER");
                                    batch.set(refPoliceNotif, map);
                                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mProgressDialog.setProgress(100);
                                                mProgressDialog.setMessage("Successfully Uploaded");
                                                mProgressDialog.dismiss();
                                                Intent intent = new Intent(RegisterActivity.this, PoliceVerificationActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "" + task.getException().getMessage()
                                                        , Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                }
            }
        });
//        Fade fade = new Fade();
//        getWindow().setEnterTransition(fade);
//        getWindow().setExitTransition(fade);
//        getWindow().setAllowEnterTransitionOverlap(false);
//        getWindow().setAllowReturnTransitionOverlap(false);
//        setEnterSharedElementCallback(new SharedElementCallback() {
//            @Override
//            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
//                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
//                SplashActivity.splashActivity.finishAfterTransition();
//            }
//        });

//        mAuth = FirebaseAuth.getInstance();
//        if(mAuth.getCurrentUser() != null)
//        {
//            policeID = FirebaseAuth.getInstance().getUid();
//            setOnclick();
//
//        }
    }

    private void initViews() {
//        animView = findViewById(R.id.RA_animationView);

        txtPoliceName = findViewById(R.id.police_name);
        txtPoliceNumber =findViewById(R.id.phone_number);
        txtEmailID = findViewById(R.id.email_ID);

        txtDesignation = findViewById(R.id.designation);
        txtPoliceStation = findViewById(R.id.policeStation);
        txtPostedCity = findViewById(R.id.posted_city);

        btnProceed = findViewById(R.id.btn_proceed);

        ArrayAdapter<CharSequence> adapterDesignation = ArrayAdapter.createFromResource(
                this, R.array.PoliceDesignation, android.R.layout.simple_spinner_dropdown_item);
        txtDesignation.setAdapter(adapterDesignation);


//        ArrayAdapter<CharSequence> adapterStation = ArrayAdapter.createFromResource(
//                this,R.array.PoliceStations,android.R.layout.simple_spinner_dropdown_item);


        listCity = new ArrayList<>();
        policeStationList = new ArrayList<>();
        getCitiesList();
        getPoliceStationList();
        ArrayAdapter<City> adapterCities = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listCity);
        txtPostedCity.setAdapter(adapterCities);
        txtPostedCity.setThreshold(1);

        ArrayAdapter<PoliceStation> adapterStation = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, policeStationList);
        txtPoliceStation.setAdapter(adapterStation);

        rYes = findViewById(R.id.head_YES);
        rNo = findViewById(R.id.head_NO);

    }

    private void getPoliceStationList() {
        try {
            // Convert the string returned to a JSON object
            JSONObject jsonObject = new JSONObject(getJson("police_stations.json"));
            // Get Json array
            JSONArray array = jsonObject.getJSONArray("array");
            // Navigate through an array item one by one
            for (int i = 0; i < array.length(); i++) {
                // select the particular JSON data
                JSONObject object = array.getJSONObject(i);
                PoliceStation mStation = new PoliceStation();
                mStation.setId(object.getInt("id"));
                mStation.setName(object.getString("name"));
                mStation.setCity(object.getString("city"));
                mStation.setState(object.getString("state"));
                // add to the lists in the specified format
                policeStationList.add(mStation);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    void getCitiesList() {
        // Exceptions are returned by JSONObject when the object cannot be created
        try {
            // Convert the string returned to a JSON object
            JSONObject jsonObject = new JSONObject(getJson("cities.json"));
            // Get Json array
            JSONArray array=jsonObject.getJSONArray("array");
            // Navigate through an array item one by one
            for(int i=0;i<array.length();i++)
            {
                // select the particular JSON data
                JSONObject object=array.getJSONObject(i);
                City mCity = new City();
                mCity.setId(object.getString("id"));
                mCity.setCity(object.getString("name"));
                mCity.setState(object.getString("state"));
                // add to the lists in the specified format
                listCity.add(mCity);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    // Get the content of cities.json from assets directory and store it as string
    private String getJson(String filename) {
        String json = null;
        try {
            // Opening cities.json file
            InputStream is = getAssets().open(filename);
            // is there any content in the file
            int size = is.available();
            byte[] buffer = new byte[size];
            // read values in the byte array
            is.read(buffer);
            // close the stream --- very important
            is.close();
            // convert byte to string
            json = new String(buffer, StandardCharsets.UTF_8);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return json;
        }
        return json;
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

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}