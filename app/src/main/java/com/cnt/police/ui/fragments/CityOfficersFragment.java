package com.cnt.police.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnt.police.R;
import com.cnt.police.adapters.CityOfficerAdapter;
import com.cnt.police.models.PoliceUser;
import com.cnt.police.storage.MyPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CityOfficersFragment extends Fragment {

    private TextView txtNoCityOfficers;
    private RecyclerView recyclerCityOfficers;
    private FirebaseFirestore db;
    private MyPrefs mPrefs;
    private ArrayList<PoliceUser> mPoliceUsers;
    private CityOfficerAdapter mCityOfficerAdapter;

    public CityOfficersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_city_officers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtNoCityOfficers = view.findViewById(R.id.txtNoCityOfficers);
        recyclerCityOfficers = view.findViewById(R.id.recyclerCityOfficers);
        mPrefs = new MyPrefs(getContext());
        db = FirebaseFirestore.getInstance();
        mPoliceUsers = new ArrayList<>();
        mCityOfficerAdapter = new CityOfficerAdapter(getActivity(), mPoliceUsers);
        recyclerCityOfficers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerCityOfficers.setItemAnimator(new DefaultItemAnimator());
        recyclerCityOfficers.setAdapter(mCityOfficerAdapter);

        getOfficersList();
    }

    private void getOfficersList() {
        txtNoCityOfficers.setVisibility(View.GONE);
        db.collection("PoliceUsers")
                .whereEqualTo("posted_city", mPrefs.getCity())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                                if (snapshot.getId().equals(mPrefs.getUID()))
                                    continue;
                                mPoliceUsers.add(snapshot.toObject(PoliceUser.class));
                            }
                            if (mPoliceUsers.isEmpty()) {
                                txtNoCityOfficers.setVisibility(View.VISIBLE);
                                return;
                            }
                            mCityOfficerAdapter.notifyDataSetChanged();
                        } else {
                            txtNoCityOfficers.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }
}