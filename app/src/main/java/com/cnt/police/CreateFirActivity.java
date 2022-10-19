package com.cnt.police;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cnt.police.models.City;
import com.cnt.police.models.FIR;
import com.cnt.police.status.AppointmentStatus;
import com.cnt.police.status.UserType;
import com.cnt.police.storage.MyPrefs;
import com.cnt.police.ui.dialogs.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CreateFirActivity extends AppCompatActivity {

    private TextInputEditText txtComplainantName, txtPoliceStation, txtApplicantName, txtApplicantAddress,
            txtApplicantNumber, txtAccusedName, txtCrimeDetails, txtCrimePlace, txtCrimeDate, txtCrimeTime;
    private MaterialAutoCompleteTextView txtDistrict, txtState, txtCrimeType;
    private MaterialButton btnCreateFIR;
    private FIR mFIR;
    private ArrayList<City> listCity;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private LoadingDialog mLoadingDialog;
    private MyPrefs mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_fir);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Create FIR");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        mFIR = new FIR();
        mPrefs = new MyPrefs(this);
        listCity = new ArrayList<>();
        getCitiesList();
        ArrayAdapter<City> adapterCities = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, listCity);
        txtDistrict.setAdapter(adapterCities);
        txtDistrict.setThreshold(1);

        ArrayAdapter<CharSequence> adapterStation = ArrayAdapter.createFromResource(
                this, R.array.MainCrimeTypes, android.R.layout.simple_spinner_dropdown_item);

        txtCrimeType.setAdapter(adapterStation);

        txtCrimeType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFIR.setCrime_type((String) parent.getItemAtPosition(position));
                txtCrimeType.setError(null);
            }
        });
        txtDistrict.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txtDistrict.setText(((City) parent.getItemAtPosition(position)).getCity());
                txtState.setText(((City) parent.getItemAtPosition(position)).getState());
                mFIR.setPS_district(((City) parent.getItemAtPosition(position)).getCity());
                mFIR.setPS_state(((City) parent.getItemAtPosition(position)).getState());
            }
        });

        txtCrimeDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtCrimeDate.setText(String.format(Locale.US, "%02d-%02d-%02d", dayOfMonth, month + 1, year));
                        mFIR.setCrime_date(Objects.requireNonNull(txtCrimeDate.getText()).toString());
                    }
                }, year, month, day);
                datePickerDialog.show();
                txtCrimeDate.clearFocus();
            }
            txtCrimeDate.setError(null);
        });

        txtCrimeTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minutes = calendar.get(Calendar.MINUTE);
                    timePickerDialog = new TimePickerDialog(CreateFirActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            txtCrimeTime.setText(String.format(Locale.ENGLISH, "%02d:%02d", hourOfDay, minute));
                            mFIR.setCrime_time(txtCrimeTime.getText().toString());
                        }
                    }, hour, minutes, true);
                    timePickerDialog.show();
                    txtCrimeTime.clearFocus();
                }
                txtCrimeTime.setError(null);
            }
        });


        btnCreateFIR.setOnClickListener(v -> {
            if (validateInputs()) {
                mLoadingDialog = new LoadingDialog();
                mLoadingDialog.setCancelable(false);
                mLoadingDialog.show(getSupportFragmentManager(), "loading_dialog");
                createFirInFireStore();
            }
        });
    }

    private void createFirInFireStore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("FIR").document();
        mFIR.setFIR_ID(docRef.getId());
        mFIR.setCreated_at(new Timestamp(Calendar.getInstance().getTime()));
        docRef.set(mFIR).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "FIR Created", Toast.LENGTH_SHORT).show();
                mLoadingDialog.dismiss();
                finish();
            }
        });
    }

    private boolean validateInputs() {
        if (txtComplainantName.getText() == null || txtComplainantName.getText().toString().isEmpty()) {
            txtComplainantName.setError("Invalid Value");
            return false;
        } else if (txtPoliceStation.getText() == null || txtPoliceStation.getText().toString().isEmpty()) {
            txtPoliceStation.setError("Invalid Value");
            return false;
        } else if (txtApplicantName.getText() == null || txtApplicantName.getText().toString().isEmpty()) {
            txtApplicantName.setError("Invalid Value");
            return false;
        } else if (txtApplicantAddress.getText() == null || txtApplicantAddress.getText().toString().isEmpty()) {
            txtApplicantAddress.setError("Invalid Value");
            return false;
        } else if (txtApplicantNumber.getText() == null || txtApplicantNumber.getText().toString().isEmpty()) {
            txtApplicantNumber.setError("Invalid Value");
            return false;
        } else if (txtAccusedName.getText() == null || txtAccusedName.getText().toString().isEmpty()) {
            txtAccusedName.setError("Invalid Value");
            return false;
        } else if (txtCrimeDetails.getText() == null || txtCrimeDetails.getText().toString().isEmpty()) {
            txtCrimeDetails.setError("Invalid Value");
            return false;
        } else if (txtCrimePlace.getText() == null || txtCrimePlace.getText().toString().isEmpty()) {
            txtCrimePlace.setError("Invalid Value");
            return false;
        } else if (txtCrimeDate.getText() == null || txtCrimeDate.getText().toString().isEmpty()) {
            txtCrimeDate.setError("Invalid Value");
            return false;
        } else if (txtCrimeTime.getText() == null || txtCrimeTime.getText().toString().isEmpty()) {
            txtCrimeTime.setError("Invalid Value");
            return false;
        } else if (mFIR.getCrime_date() == null || mFIR.getCrime_date().isEmpty()) {
            txtCrimeDate.setError("Not Selected");
            return false;
        } else if (mFIR.getCrime_time() == null || mFIR.getCrime_time().isEmpty()) {
            txtCrimeTime.setError("Not Selected");
            return false;
        } else if (mFIR.getCrime_type() == null || mFIR.getCrime_type().isEmpty()) {
            txtCrimeType.setError("Not Selected");
            return false;
        }
        mFIR.setComplainantID(mPrefs.getUID());
        mFIR.setComplainantName(txtComplainantName.getText().toString().trim());
        mFIR.setApplicantName(txtApplicantName.getText().toString().trim());
        mFIR.setApplicantAddress(txtApplicantAddress.getText().toString().trim());
        mFIR.setApplicantPhoneNumber(txtApplicantNumber.getText().toString().trim());
        mFIR.setAccusedName(txtAccusedName.getText().toString().trim());
        mFIR.setCrime_details(txtCrimeDetails.getText().toString().trim());
        mFIR.setCrime_place(txtCrimePlace.getText().toString().trim());
        mFIR.setPoliceStation(txtPoliceStation.getText().toString().trim());
        mFIR.setStatus(AppointmentStatus.PENDING.name());
        mFIR.setCreatedBy(UserType.POLICE_USER.name());
        return true;
    }

    private void getCitiesList() {
        // Exceptions are returned by JSONObject when the object cannot be created
        try {
            // Convert the string returned to a JSON object
            JSONObject jsonObject = new JSONObject(getJson("cities.json"));
            // Get Json array
            JSONArray array = jsonObject.getJSONArray("array");
            // Navigate through an array item one by one
            for (int i = 0; i < array.length(); i++) {
                // select the particular JSON data
                JSONObject object = array.getJSONObject(i);
                City mCity = new City();
                mCity.setId(object.getString("id"));
                mCity.setCity(object.getString("name"));
                mCity.setState(object.getString("state"));
                // add to the lists in the specified format
                listCity.add(mCity);
            }
        } catch (JSONException e) {
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
        } catch (IOException ex) {
            ex.printStackTrace();
            return json;
        }
        return json;
    }

    private void initViews() {
        txtComplainantName = findViewById(R.id.FIR_txtComplainantName);
        txtPoliceStation = findViewById(R.id.FIR_txtPS);
        txtApplicantName = findViewById(R.id.FIR_txtApplicantName);
        txtApplicantAddress = findViewById(R.id.FIR_txtApplicantAddress);
        txtApplicantNumber = findViewById(R.id.FIR_txtApplicantNumber);
        txtAccusedName = findViewById(R.id.FIR_txtAccusedName);
        txtCrimeDetails = findViewById(R.id.FIR_txtCrimeDetails);
        txtCrimePlace = findViewById(R.id.FIR_txtCrimePlace);
        txtCrimeDate = findViewById(R.id.FIR_txtCrimeDate);
        txtCrimeTime = findViewById(R.id.FIR_txtTimeOfCrime);
        txtDistrict = findViewById(R.id.FIR_txtCrimeDistrict);
        txtState = findViewById(R.id.FIR_txtCrimeState);
        txtCrimeType = findViewById(R.id.FIR_crimeType);
        btnCreateFIR = findViewById(R.id.FIR_btnCreateFIR);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}