package com.cnt.police.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnt.police.R;
import com.cnt.police.adapters.CaseUpdatesAdapter;
import com.cnt.police.models.Crime;
import com.cnt.police.status.CaseStatus;
import com.cnt.police.ui.dialogs.ConfirmCaseSolvedDialog;
import com.cnt.police.ui.dialogs.ConfirmReopenCaseDialog;
import com.cnt.police.viewmodels.CrimeViewModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;


public class CaseDetailsFragment extends Fragment {
    private static final String TAG = "CaseDetailsFragment";
    private RecyclerView recyclerCaseUpdates;
    private Crime mCrime;
    private TextView txtNoCaseUpdates;
    private CaseUpdatesAdapter mCaseUpdatesAdapter;
    private TextView txtCaseStatus, txtCrimeID, txtCrimeType, txtCrimeDetails, txtCrimePlace, txtCrimeDistrict,
            txtCrimeState, txtCrimeDate;
    private RatingBar mRatingCrime;
    private Chip chipMarkSolved, chipViewCriminal, chipReopen;
    private CrimeViewModel mCrimeViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_case_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        mCrimeViewModel = new ViewModelProvider(requireActivity()).get(CrimeViewModel.class);

        if (getArguments() != null) {
            mCrime = getArguments().getParcelable("crimeData");
        }

        mCrimeViewModel.setCrimeLiveData(mCrime);

        if (mCrime == null) {
            txtNoCaseUpdates.setVisibility(View.VISIBLE);
            return;
        }
        if (mCrime.getCaseUpdates() == null || mCrime.getCaseUpdates().isEmpty()) {
            txtNoCaseUpdates.setVisibility(View.VISIBLE);
            mCrime.setCaseUpdates(new ArrayList<>());
        }
        if (mCrime.getCase_status().equals(CaseStatus.SOLVED.name())) {
            chipMarkSolved.setVisibility(View.GONE);
            setHasOptionsMenu(false);
        } else {
            chipReopen.setVisibility(View.GONE);
        }

        txtCaseStatus.setText(mCrime.getCase_status());
        txtCrimeID.setText("#".concat(mCrime.getCrime_id()));
        txtCrimeType.setText(mCrime.getCrime_type());
        txtCrimeDetails.setText(mCrime.getCrime_details());
        txtCrimePlace.setText(mCrime.getAddress_of_crime());
        txtCrimeDistrict.setText(mCrime.getCrime_district());
        txtCrimeState.setText(mCrime.getCrime_state());
        txtCrimeDate.setText(mCrime.getDate_of_crime());
        mRatingCrime.setRating(mCrime.getCrime_rating());

        mCrimeViewModel.getCrimeMutableLiveData().observe(getActivity(), new Observer<Crime>() {
            @Override
            public void onChanged(Crime crime) {
                txtCaseStatus.setText(crime.getCase_status());
                if (crime.getCaseUpdates() == null || crime.getCaseUpdates().isEmpty()) {
                    return;
                }
                if (crime.getCaseUpdates().size() - mCrime.getCaseUpdates().size() == 1) {
                    mCrime.getCaseUpdates().add(crime.getCaseUpdates().get(crime.getCaseUpdates().size() - 1));
                    mCaseUpdatesAdapter.notifyItemInserted(mCrime.getCaseUpdates().size() - 1);
                }
                mCaseUpdatesAdapter = new CaseUpdatesAdapter(getContext(), mCrime.getCaseUpdates());
                recyclerCaseUpdates.setAdapter(mCaseUpdatesAdapter);
            }
        });
        chipMarkSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chipMarkSolved.setText("SOLVED");
                    chipMarkSolved.setTextColor(getResources().getColor(R.color.green, requireContext().getTheme()));
                    chipMarkSolved.setChipStrokeColor(getResources().getColorStateList(R.color.green, requireContext().getTheme()));
                    ConfirmCaseSolvedDialog mConfirmCaseSolvedDialog =
                            new ConfirmCaseSolvedDialog(chipMarkSolved, mCrime.getCrime_id());
                    mConfirmCaseSolvedDialog.setCancelable(false);
                    mConfirmCaseSolvedDialog.show(getChildFragmentManager(), "dialog_solved");

                } else {
                    chipMarkSolved.setText("MARK AS SOLVED");
                    chipMarkSolved.setTextColor(getResources().getColor(R.color.blue, requireContext().getTheme()));
                    chipMarkSolved.setChipStrokeColor(getResources().getColorStateList(R.color.blue, requireContext().getTheme()));
                }
            }
        });

        chipReopen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ConfirmReopenCaseDialog confirmReopenCaseDialog =
                            new ConfirmReopenCaseDialog(chipReopen, mCrime.getCrime_id());
                    confirmReopenCaseDialog.setCancelable(false);
                    confirmReopenCaseDialog.show(getChildFragmentManager(), "dialog_reopen");
                }
            }
        });

        chipViewCriminal.setOnClickListener(v -> {
            CaseDetailsFragmentDirections.ActionCaseDetailsFragmentToCriminalProfileActivity action =
                    CaseDetailsFragmentDirections.actionCaseDetailsFragmentToCriminalProfileActivity();
            action.setCriminalId(mCrime.getCriminal_id());
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                    .navigate(action);
        });
        mCaseUpdatesAdapter = new CaseUpdatesAdapter(getContext(), mCrime.getCaseUpdates());
        recyclerCaseUpdates.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCaseUpdates.setItemAnimator(new DefaultItemAnimator());
        recyclerCaseUpdates.setAdapter(mCaseUpdatesAdapter);
        recyclerCaseUpdates.setNestedScrollingEnabled(false);
    }

    private void initViews(View view) {
        recyclerCaseUpdates = view.findViewById(R.id.recyclerCaseUpdates);
        txtNoCaseUpdates = view.findViewById(R.id.txtNoCaseUpdates);
        txtCaseStatus = view.findViewById(R.id.caseDetailsStatus);
        txtCrimeID = view.findViewById(R.id.caseDetailsID);
        txtCrimeType = view.findViewById(R.id.caseDetailsCrimeType);
        txtCrimeDetails = view.findViewById(R.id.caseDetailsCrimeDetails);
        txtCrimePlace = view.findViewById(R.id.caseDetailsCrimePlace);
        txtCrimeDistrict = view.findViewById(R.id.caseDetailsDistrict);
        txtCrimeState = view.findViewById(R.id.caseDetailsState);
        txtCrimeDate = view.findViewById(R.id.caseDetailsDate);
        mRatingCrime = view.findViewById(R.id.caseDetailsRating);
        chipMarkSolved = view.findViewById(R.id.chipMarkSolved);
        chipViewCriminal = view.findViewById(R.id.chipViewCriminal);
        chipReopen = view.findViewById(R.id.chipReopen);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_update_status, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_update_status) {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_caseDetailsFragment_to_addCaseUpdateFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}