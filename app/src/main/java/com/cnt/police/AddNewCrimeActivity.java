package com.cnt.police;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cnt.police.models.City;
import com.cnt.police.models.Crime;
import com.cnt.police.status.CaseStatus;
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
import com.google.firebase.firestore.WriteBatch;

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

public class AddNewCrimeActivity extends AppCompatActivity {

    private static final String TAG = "AddNewCrimeActivity";
    private ImageView imgCriminal;
    private String criminalID;
    private TextView txtCriminalName;
    private MaterialAutoCompleteTextView crime_type;
    private MaterialButton btnUpdateCrime;
    private TextInputEditText crime_details,
            date_of_crime, address_of_crime;
    private MaterialAutoCompleteTextView crime_district, crime_state;
    private AppCompatRatingBar crime_rating;
    private DatePickerDialog datePickerDialog;
    private Crime mCrime;
    private boolean flag;
    private LoadingDialog mLoadingDialog;
    private WriteBatch mWriteBatch;
    private DocumentReference refCriminals, refCrimes;
    private MyPrefs mPrefs;
    private ArrayList<City> listCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_crime);
        initViews();

        mPrefs = new MyPrefs(this);
        mCrime = new Crime();
        listCity = new ArrayList<>();
        Intent rcv = getIntent();
        criminalID = rcv.getStringExtra("criminalID");
        Log.i(TAG, "onCreate: " + criminalID);
        String criminalName = rcv.getStringExtra("criminalName");
        String criminalPic = rcv.getStringExtra("criminalPic");
        if (criminalPic == null || criminalID == null)
            return;

        txtCriminalName.setText(criminalName);
        Glide.with(AddNewCrimeActivity.this)
                .load(criminalPic)
                .placeholder(R.drawable.ic_thief)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .circleCrop()
                .into(imgCriminal);
        getCitiesList();
        ArrayAdapter<City> adapterCities = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listCity);
        crime_district.setAdapter(adapterCities);
        crime_district.setThreshold(1);
        ArrayAdapter<CharSequence> adapterStation = ArrayAdapter.createFromResource(
                this, R.array.MainCrimeTypes, android.R.layout.simple_spinner_dropdown_item);
        crime_type.setAdapter(adapterStation);
        crime_type.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                crime_type.setError(null);
            }
        });

        crime_district.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                crime_district.setText(((City) parent.getItemAtPosition(position)).getCity());
                crime_state.setText(((City) parent.getItemAtPosition(position)).getState());
            }
        });

        date_of_crime.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date_of_crime.setText(String.format(Locale.US, "%02d-%02d-%02d", dayOfMonth, month + 1, year));
                        mCrime.setDate_of_crime(Objects.requireNonNull(date_of_crime.getText()).toString());
                    }
                }, year, month, day);
                datePickerDialog.show();
                date_of_crime.clearFocus();
            }
            date_of_crime.setError(null);
        });

        btnUpdateCrime.setOnClickListener(v -> validateInputs());


    }

    private void validateInputs() {
        mCrime.setCrime_type(
                crime_type.getText() != null ?
                        crime_type.getText().toString().trim() : "");
        mCrime.setCrime_details(
                crime_details.getText() != null ?
                        crime_details.getText().toString().trim() : "");
        mCrime.setCrime_state(
                crime_state.getText() != null ?
                        crime_state.getText().toString().trim() : "");
        mCrime.setCrime_district(
                crime_district.getText() != null ?
                        crime_district.getText().toString().trim() : "");
        mCrime.setAddress_of_crime(
                address_of_crime.getText() != null ?
                        address_of_crime.getText().toString().trim() : "");
        mCrime.setCrime_rating(crime_rating.getRating());
        flag = true;
        if (mCrime.getCrime_type().isEmpty()) {
            crime_type.setError("Empty Field");
            flag = false;
            return;
        } else if (mCrime.getCrime_details().isEmpty()) {
            crime_details.setError("Empty Field");
            flag = false;
            return;
        } else if (mCrime.getAddress_of_crime().isEmpty()) {
            address_of_crime.setError("Empty Field");
            flag = false;
            return;
        } else if (mCrime.getCrime_state().isEmpty()) {
            crime_state.setError("Empty Field");
            flag = false;
            return;
        } else if (mCrime.getCrime_district().isEmpty()) {
            crime_district.setError("Empty Field");
            flag = false;
            return;
        } else if (mCrime.getDate_of_crime().isEmpty()) {
            date_of_crime.setError("Empty Field");
            flag = false;
            return;
        } else if (mCrime.getCrime_rating() == 0) {
            Toast.makeText(this, "Please rate crime out of 5", Toast.LENGTH_SHORT).show();
            flag = false;
            return;
        }
        if (flag) {
            mLoadingDialog = new LoadingDialog();
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.show(getSupportFragmentManager(), "loading_dialog");
            addNewCrimeToFireStore();
        }
    }

    private void addNewCrimeToFireStore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mWriteBatch = db.batch();
        refCriminals = db.collection("Criminals").document(criminalID);
        refCrimes = db.collection("Crimes").document();

        mCrime.setCreated_at(new Timestamp(Calendar.getInstance().getTime()));
        mCrime.setUpdated_at(mCrime.getCreated_at());
        mCrime.setCrime_id(refCrimes.getId());
        mCrime.setCrime_adder_authority_id(mPrefs.getUID());
        mCrime.setCriminal_id(refCriminals.getId());
        mCrime.setCase_status(CaseStatus.PENDING.name());

        mWriteBatch.set(refCrimes, mCrime);
        mWriteBatch.update(refCriminals, "last_crime", refCrimes.getId());
        mWriteBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent();
                    intent.putExtra("newCrime", mCrime);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                mLoadingDialog.dismiss();
            }
        });
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
        txtCriminalName = findViewById(R.id.update_CriminalName);
        imgCriminal = findViewById(R.id.update_imgCriminal);
        crime_type = findViewById(R.id.update_crime_type);
        crime_details = findViewById(R.id.update_crime_details);
        crime_state = findViewById(R.id.update_crime_state);
        crime_district = findViewById(R.id.update_crime_district);
        date_of_crime = findViewById(R.id.update_date_of_crime);
        address_of_crime = findViewById(R.id.update_address_of_crime);
        crime_rating = findViewById(R.id.update_crime_rating);
        btnUpdateCrime = findViewById(R.id.btnUpdateCrime);
    }
}