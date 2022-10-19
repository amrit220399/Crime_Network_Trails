package com.cnt.police.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnt.police.R;
import com.cnt.police.adapters.CriminalAdapter;
import com.cnt.police.models.Criminal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class CriminalsFragment extends Fragment {

    private RecyclerView mRecyclerCriminals;
    private ArrayList<Criminal> mCriminals;
    private CriminalAdapter mCriminalAdapter;

    public CriminalsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_criminals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerCriminals = view.findViewById(R.id.recyclerCriminalsList);
        mCriminals = new ArrayList<>();
        mCriminalAdapter = new CriminalAdapter(getContext(), mCriminals);
        mRecyclerCriminals.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerCriminals.setItemAnimator(new DefaultItemAnimator());
        mRecyclerCriminals.setAdapter(mCriminalAdapter);
        fetchCriminalsFromFirestore();
    }

    private void fetchCriminalsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Criminals").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                        Criminal mCriminal = snapshot.toObject(Criminal.class);
                        mCriminals.add(mCriminal);
                    }
                    mCriminalAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}