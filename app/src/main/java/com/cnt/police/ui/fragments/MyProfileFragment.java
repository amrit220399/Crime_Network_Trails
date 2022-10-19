package com.cnt.police.ui.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cnt.police.R;
import com.cnt.police.models.PoliceStation;
import com.cnt.police.models.PoliceUser;
import com.cnt.police.storage.MyPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;


public class MyProfileFragment extends Fragment {

    private TextView txtOfficerName, txtPhone, txtEmail, txtRating;
    private TextInputEditText txtPSDistrict, txtPSState;
    private TextInputLayout layoutDesignation, layoutPS;
    private MaterialAutoCompleteTextView txtDesignation, txtPS;
    private MyPrefs mPrefs;
    private FirebaseFirestore db;
    private boolean isDesignationClicked = false, isPSClicked = false;
    private ArrayList<PoliceStation> policeStationList;
    private String designation, PS, state, district;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        mPrefs = new MyPrefs(requireActivity());
        db = FirebaseFirestore.getInstance();
        policeStationList = new ArrayList<>();
        txtOfficerName.setText(mPrefs.getName());
        txtPS.setText(mPrefs.getPS());
        txtPSDistrict.setText(mPrefs.getCity());
        txtDesignation.setText(mPrefs.getDesignation());
        txtDesignation.setInputType(InputType.TYPE_NULL);
        txtPS.setInputType(InputType.TYPE_NULL);
        txtPSState.setInputType(InputType.TYPE_NULL);
        txtPSDistrict.setInputType(InputType.TYPE_NULL);
        getMyProfile();
        getPoliceStationList();

        layoutDesignation.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDesignationClicked = !isDesignationClicked;
                if (isDesignationClicked) {
                    ArrayAdapter<CharSequence> adapterDesignation = ArrayAdapter.createFromResource(
                            getActivity(), R.array.PoliceDesignation, android.R.layout.simple_spinner_dropdown_item);
                    txtDesignation.setAdapter(adapterDesignation);
                    txtDesignation.setThreshold(0);

                    txtDesignation.showDropDown();
                    layoutDesignation.setEndIconDrawable(R.drawable.ic_baseline_check_circle_24);
                } else {
                    txtDesignation.setAdapter(null);
                    layoutDesignation.setEndIconDrawable(R.drawable.ic_baseline_edit_24);
                    updateDesignation();
                }
            }
        });
        layoutPS.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPSClicked = !isPSClicked;
                if (isPSClicked) {
                    ArrayAdapter<PoliceStation> adapterStation = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, policeStationList);
                    txtPS.setAdapter(adapterStation);
                    txtPS.setThreshold(0);
                    txtPS.showDropDown();
                    layoutPS.setEndIconDrawable(R.drawable.ic_baseline_check_circle_24);
                } else {
                    txtPS.setAdapter(null);
                    updatePS();
                    layoutPS.setEndIconDrawable(R.drawable.ic_baseline_edit_24);
                }
            }
        });


        txtPS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PS = ((PoliceStation) parent.getItemAtPosition(position)).getName();
                state = ((PoliceStation) parent.getItemAtPosition(position)).getState();
                district = ((PoliceStation) parent.getItemAtPosition(position)).getCity();
                txtPSState.setText(state);
                txtPSDistrict.setText(district);
            }
        });

        txtDesignation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                designation = (String) parent.getItemAtPosition(position);
            }
        });

    }

    private void updateDesignation() {
        if (designation != null && !designation.isEmpty()) {
            db.collection("PoliceUsers")
                    .document(mPrefs.getUID())
                    .update("designation", designation);
            mPrefs.setDesignation(designation);
        }
    }

    private void updatePS() {
        if (PS != null && !PS.isEmpty()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("police_station_id", PS);
            map.put("posted_city", district);
            map.put("posted_state", state);
            db.collection("PoliceUsers")
                    .document(mPrefs.getUID())
                    .update(map);
            mPrefs.setPS(PS);
            mPrefs.setCity(district);

            //TODO: Change Firebase Messaging Topic Name to Current City
        }
    }

    private void getMyProfile() {
        db.collection("PoliceUsers")
                .document(mPrefs.getUID())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    PoliceUser policeUser = task.getResult().toObject(PoliceUser.class);
                    txtEmail.setText(policeUser.getEmail());
                    txtPhone.setText(policeUser.getPhoneNumber());
                    txtPSState.setText(policeUser.getPosted_state());
                    if (policeUser.getRating() > 0) {
                        txtRating.setText(String.valueOf(policeUser.getRating()));
                    }
                }
            }
        });
    }

    private void initViews(View view) {
        txtOfficerName = view.findViewById(R.id.MP_OfficerName);
        txtPhone = view.findViewById(R.id.MP_phone);
        txtEmail = view.findViewById(R.id.MP_email);
        txtRating = view.findViewById(R.id.MP_rating);
        txtDesignation = view.findViewById(R.id.MP_designation);
        txtPS = view.findViewById(R.id.MP_PS);
        txtPSDistrict = view.findViewById(R.id.MP_PS_district);
        txtPSState = view.findViewById(R.id.MP_PS_state);
        layoutDesignation = view.findViewById(R.id.TIL_designation);
        layoutPS = view.findViewById(R.id.TIL_PS);
    }

    private void getPoliceStationList() {
        try {
            // Convert the string returned to a JSON object
            JSONObject jsonObject = new JSONObject(getJson());
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

    private String getJson() {
        String json = null;
        try {
            // Opening cities.json file
            InputStream is = requireActivity().getAssets().open("police_stations.json");
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
}