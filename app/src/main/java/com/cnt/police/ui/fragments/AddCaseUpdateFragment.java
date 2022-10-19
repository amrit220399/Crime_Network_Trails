package com.cnt.police.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.cnt.police.R;
import com.cnt.police.models.CaseUpdate;
import com.cnt.police.models.Crime;
import com.cnt.police.status.CaseStatus;
import com.cnt.police.viewmodels.CrimeViewModel;
import com.cnt.police.viewmodels.HomeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class AddCaseUpdateFragment extends Fragment {

    private static final String TAG = "AddCaseUpdateFragment";
    private MaterialAutoCompleteTextView txtUpdateHeadline;
    private TextInputEditText txtUpdateDetails;
    private MaterialButton btnUpdateCase;
    private CaseUpdate mCaseUpdate;
    private Crime mCrime;
    //    private String crimeID;
//    private String case_status;
    private HashMap<String, Object> mHashMap;
    private CrimeViewModel mCrimeViewModel;
    private boolean isStatusChanged = false;
    private HomeViewModel mHomeViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_case_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtUpdateHeadline = view.findViewById(R.id.txtUpdateHeadline);
        txtUpdateDetails = view.findViewById(R.id.txtUpdateDetails);
        btnUpdateCase = view.findViewById(R.id.btnUpdateCase);
        mCrimeViewModel = new ViewModelProvider(requireActivity()).get(CrimeViewModel.class);
        mCrime = mCrimeViewModel.getCrimeMutableLiveData().getValue();
        mHomeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

//        if(getArguments()!=null) {
//            crimeID = getArguments().getString("crimeID");
//            case_status = getArguments().getString("caseStatus");
//        }
//        if(crimeID==null || crimeID.isEmpty() || case_status==null || case_status.isEmpty()){
//            return;
//        }
        Log.i(TAG, "onViewCreated: " + mCrime);
        if (mCrime == null)
            return;
        mCaseUpdate = new CaseUpdate();
        mHashMap = new HashMap<>();

        btnUpdateCase.setOnClickListener(v -> validateInputs());
    }

    private void validateInputs() {
        if (txtUpdateHeadline.getText() == null || txtUpdateHeadline.getText().toString().isEmpty()) {
            txtUpdateHeadline.setError("Invalid Update Headline");
            return;
        }
        if (txtUpdateDetails.getText() == null || txtUpdateDetails.getText().toString().isEmpty()) {
            txtUpdateDetails.setError("Invalid Details");
            return;
        }
        mCaseUpdate.setUpdateTitle(txtUpdateHeadline.getText().toString().trim());
        mCaseUpdate.setUpdateDetails(txtUpdateDetails.getText().toString().trim());
        mCaseUpdate.setUpdateTimestamp(new Timestamp(Calendar.getInstance().getTime()));
        mHashMap.put("caseUpdates", FieldValue.arrayUnion(mCaseUpdate));
        mHashMap.put("updated_at", FieldValue.serverTimestamp());
        if (mCrime.getCase_status().equals(CaseStatus.PENDING.name())) {
            mHashMap.put("case_status", CaseStatus.IN_PROGRESS.name());
            mCrime.setCase_status(CaseStatus.IN_PROGRESS.name());
            isStatusChanged = true;
        }
        updateCaseDetailsInFireStore();
    }

    private void updateCaseDetailsInFireStore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Crimes")
                .document(mCrime.getCrime_id())
                .update(mHashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Updated Case Details", Toast.LENGTH_SHORT).show();
                            mCrime.getCaseUpdates().add(mCaseUpdate);
                            mCrimeViewModel.setCrimeLiveData(mCrime);
                            if (isStatusChanged) {
                                mHomeViewModel.setCasePending(
                                        mHomeViewModel.getCasePending().getValue() == null ?
                                                1 : mHomeViewModel.getCasePending().getValue() + 1);
                            }
                            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                                    .navigateUp();
                        }
                    }
                });
    }

}