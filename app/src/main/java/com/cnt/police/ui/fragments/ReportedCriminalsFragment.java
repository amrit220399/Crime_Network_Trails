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
import com.cnt.police.adapters.ReportedCriminalsAdapter;
import com.cnt.police.models.ReportedCriminal;
import com.cnt.police.storage.MyPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ReportedCriminalsFragment extends Fragment {

    private RecyclerView recyclerReportedCriminals;
    private TextView txtNoReportedCriminals;
    private ReportedCriminalsAdapter mReportedCriminalsAdapter;
    private ArrayList<ReportedCriminal> mReportedCriminals;
    private MyPrefs mPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reported_criminals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerReportedCriminals = view.findViewById(R.id.recyclerReportedCriminals);
        txtNoReportedCriminals = view.findViewById(R.id.txtNoReportedCriminals);
        mPrefs = new MyPrefs(requireContext());
        mReportedCriminals = new ArrayList<>();
        mReportedCriminalsAdapter = new ReportedCriminalsAdapter(requireContext(), mReportedCriminals);
        recyclerReportedCriminals.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerReportedCriminals.setItemAnimator(new DefaultItemAnimator());
        recyclerReportedCriminals.setAdapter(mReportedCriminalsAdapter);

        getReportedCriminals();
    }

    private void getReportedCriminals() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ReportedCriminals")
                .whereEqualTo("seenCity", mPrefs.getCity())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                                mReportedCriminals.add(snapshot.toObject(ReportedCriminal.class));
                            }
                            if (mReportedCriminals.isEmpty()) {
                                txtNoReportedCriminals.setVisibility(View.VISIBLE);
                                return;
                            }
                            mReportedCriminalsAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}