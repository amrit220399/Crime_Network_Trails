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
import com.cnt.police.adapters.TeamOfficerAdapter;
import com.cnt.police.models.PoliceUser;
import com.cnt.police.storage.MyPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TeamFragment extends Fragment {

    private TextView txtNoTeamOfficers;
    private RecyclerView recyclerTeamOfficers;
    private FirebaseFirestore db;
    private MyPrefs mPrefs;
    private ArrayList<PoliceUser> mPoliceUsers;
    private TeamOfficerAdapter mTeamOfficerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_team, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtNoTeamOfficers = view.findViewById(R.id.txtNoTeamOfficers);
        recyclerTeamOfficers = view.findViewById(R.id.recyclerTeamOfficers);
        mPrefs = new MyPrefs(getContext());
        db = FirebaseFirestore.getInstance();
        mPoliceUsers = new ArrayList<>();
        mTeamOfficerAdapter = new TeamOfficerAdapter(getActivity(), mPoliceUsers);
        recyclerTeamOfficers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerTeamOfficers.setItemAnimator(new DefaultItemAnimator());
        recyclerTeamOfficers.setAdapter(mTeamOfficerAdapter);

        getOfficersList();
    }

    private void getOfficersList() {
        txtNoTeamOfficers.setVisibility(View.GONE);
        db.collection("PoliceUsers")
                .whereEqualTo("police_station_id", mPrefs.getPS())
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
                                txtNoTeamOfficers.setVisibility(View.VISIBLE);
                                return;
                            }
                            mTeamOfficerAdapter.notifyDataSetChanged();
                        } else {
                            txtNoTeamOfficers.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }
}