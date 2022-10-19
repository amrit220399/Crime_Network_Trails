package com.cnt.police;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cnt.police.adapters.CrimeAdapter;
import com.cnt.police.models.Crime;
import com.cnt.police.models.Criminal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CriminalProfileActivity extends AppCompatActivity {

    private static int ADD_CRIME_REQUEST_CODE = 12311;
    private TextView txtCriminalName, txtDOB, txtAge, txtCrimes, txtAppearance, txtAddress;
    private ImageView imgCriminal;
    private RecyclerView mRecyclerCrimes;
    private CrimeAdapter mCrimeAdapter;
    private ArrayList<Crime> mCrimes;
    private Criminal mCriminal;
    private String criminalID;
    private FirebaseFirestore db;
    private MaterialButton btnAddNewCrime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criminal_profile);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtCriminalName = findViewById(R.id.CP_txtCriminalName);
        txtDOB = findViewById(R.id.CP_txtDOB);
        txtAge = findViewById(R.id.CP_txtAge);
        txtAddress = findViewById(R.id.CP_CriminalAddress);
        txtAppearance = findViewById(R.id.CP_CriminalAppearance);
        txtCrimes = findViewById(R.id.CP_txtCrimes);
        btnAddNewCrime = findViewById(R.id.btnAddNewCrime);
        imgCriminal = findViewById(R.id.CP_imgCriminal);
        mRecyclerCrimes = findViewById(R.id.CP_recyclerCrimes);
        txtCriminalName.setSelected(true);
        mCrimes = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        mCrimeAdapter = new CrimeAdapter(CriminalProfileActivity.this, mCrimes);
        mRecyclerCrimes.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerCrimes.setItemAnimator(new DefaultItemAnimator());
        mRecyclerCrimes.setNestedScrollingEnabled(false);
        mRecyclerCrimes.setAdapter(mCrimeAdapter);

        mCriminal = getIntent().getParcelableExtra("criminal");
        if (mCriminal != null)
            setData();
        criminalID = getIntent().getStringExtra("criminal_id");
        Log.i("TAG ID", "onCreate: " + criminalID);
        if (criminalID != null && !criminalID.isEmpty()) {
            fetchCriminalProfileFromFirestore();
        }

        btnAddNewCrime.setOnClickListener(v -> {
            if (mCriminal == null) {
                Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(CriminalProfileActivity.this, AddNewCrimeActivity.class);
            if (criminalID != null && !criminalID.isEmpty()) {
                intent.putExtra("criminalID", criminalID);
            } else {
                intent.putExtra("criminalID", mCriminal.getCriminal_id());
            }
            intent.putExtra("criminalName", mCriminal.getCriminal_name());
            intent.putExtra("criminalPic", mCriminal.getProfile_pic_url());
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, imgCriminal, Objects.requireNonNull(ViewCompat.getTransitionName(imgCriminal))
            );
            startActivityForResult(intent, ADD_CRIME_REQUEST_CODE, options.toBundle());
        });

    }

    private void fetchCriminalProfileFromFirestore() {
        db.collection("Criminals")
                .document(criminalID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            mCriminal = task.getResult().toObject(Criminal.class);
                            setData();
                        }
                    }
                });
    }

    private void setData() {

        if (mCriminal == null) {
            return;
        }
        txtCriminalName.setText(mCriminal.getCriminal_name());
        txtDOB.setText(mCriminal.getDob());
        try {
            txtAge.setText(getAge(mCriminal.getDob()));
            Log.i("TAG", "setData: " + getAge(mCriminal.getDob()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        txtAppearance.setText(mCriminal.getAppearance());
        txtAddress.setText(mCriminal.getCriminal_address());
        Glide.with(CriminalProfileActivity.this)
                .load(mCriminal.getProfile_pic_url())
                .placeholder(R.drawable.ic_thief)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fitCenter()
                .into(imgCriminal);

        fetchCriminalCrimesFromFirestore();
    }

    private void fetchCriminalCrimesFromFirestore() {
        db.collection("Crimes")
                .whereEqualTo("criminal_id", mCriminal.getCriminal_id())
                .orderBy("updated_at", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    txtCrimes.setText(String.valueOf(task.getResult().getDocuments().size()));
                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                        Crime mCrime = snapshot.toObject(Crime.class);
                        mCrimes.add(mCrime);
                    }
                    mCrimeAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private String getAge(String dob) throws ParseException {
        Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(dob);
        Calendar today = Calendar.getInstance();
        Calendar c_dob = Calendar.getInstance();
        if (date == null)
            return "NA";
        c_dob.setTime(date);
        int age = today.get(Calendar.YEAR) - c_dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < c_dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return String.valueOf(age).concat(" years");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CRIME_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Crime mNewCrime = data.getParcelableExtra("newCrime");
            mCrimes.add(0, mNewCrime);
            mCrimeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}