package com.cnt.police.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnt.police.R;
import com.cnt.police.adapters.FIRAdapter;
import com.cnt.police.adapters.NOCAdapter;
import com.cnt.police.models.FIR;
import com.cnt.police.models.NOC;
import com.cnt.police.viewmodels.AppointmentsViewModel;

import java.util.ArrayList;

public class AppointmentsFragment extends Fragment {

    private TextView txtNoFir, txtNoNoc;
    private RadioButton rbFIR, rbNOC;
    private RecyclerView recyclerFIR, recyclerNOC;
    private AppointmentsViewModel mAppointmentsViewModel;
    private FIRAdapter mFIRAdapter;
    private NOCAdapter mNOCAdapter;
    private ArrayList<NOC> mNOCS;
    private ArrayList<FIR> mFIRS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        mAppointmentsViewModel = new ViewModelProvider(requireActivity(),
                new AppointmentsViewModel.AppointmentViewModelFactory(requireContext()))
                .get(AppointmentsViewModel.class);
        mFIRS = new ArrayList<>();
        mNOCS = new ArrayList<>();
        mFIRAdapter = new FIRAdapter(requireContext(), mFIRS);
        recyclerFIR.setItemAnimator(new DefaultItemAnimator());
        recyclerFIR.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerFIR.setAdapter(mFIRAdapter);

        mNOCAdapter = new NOCAdapter(requireContext(), mNOCS);
        recyclerNOC.setItemAnimator(new DefaultItemAnimator());
        recyclerNOC.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerNOC.setAdapter(mNOCAdapter);


        mAppointmentsViewModel.getLiveFIRList().observe(requireActivity(), new Observer<ArrayList<FIR>>() {
            @Override
            public void onChanged(ArrayList<FIR> firs) {
                if (!mFIRS.isEmpty()) {
                    mFIRS.clear();
                }
                mFIRS.addAll(firs);
                if (mFIRS.isEmpty()) {
                    txtNoFir.setVisibility(View.VISIBLE);
                } else {
                    txtNoFir.setVisibility(View.GONE);
                }
                mFIRAdapter.notifyDataSetChanged();
            }
        });

        mAppointmentsViewModel.getLiveNOCList().observe(requireActivity(), new Observer<ArrayList<NOC>>() {
            @Override
            public void onChanged(ArrayList<NOC> nocs) {
                if (!mNOCS.isEmpty()) {
                    mNOCS.clear();
                }
                mNOCS.addAll(nocs);
                if (mNOCS.isEmpty()) {
                    txtNoNoc.setVisibility(View.VISIBLE);
                } else {
                    txtNoNoc.setVisibility(View.GONE);
                }
                mNOCAdapter.notifyDataSetChanged();
            }
        });


        rbFIR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recyclerFIR.setVisibility(View.VISIBLE);
                    recyclerNOC.setVisibility(View.GONE);
                    if (mFIRS.isEmpty()) {
                        txtNoFir.setVisibility(View.VISIBLE);
                        txtNoNoc.setVisibility(View.GONE);
                    }
                }
            }
        });

        rbNOC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recyclerNOC.setVisibility(View.VISIBLE);
                    recyclerFIR.setVisibility(View.GONE);
                    if (mNOCS.isEmpty()) {
                        txtNoFir.setVisibility(View.GONE);
                        txtNoNoc.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        rbFIR.setChecked(true);
    }

    private void initViews(View view) {
        txtNoFir = view.findViewById(R.id.txtNoFIR);
        txtNoNoc = view.findViewById(R.id.txtNoNOC);
        recyclerFIR = view.findViewById(R.id.recyclerFIR);
        recyclerNOC = view.findViewById(R.id.recyclerNOC);
        rbFIR = view.findViewById(R.id.rb_FIR);
        rbNOC = view.findViewById(R.id.rb_NOC);
    }
}