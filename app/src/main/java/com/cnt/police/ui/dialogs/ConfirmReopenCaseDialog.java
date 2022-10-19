package com.cnt.police.ui.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.cnt.police.R;
import com.cnt.police.status.CaseStatus;
import com.cnt.police.viewmodels.HomeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ConfirmReopenCaseDialog extends AppCompatDialogFragment {

    private Chip chipReopen;
    private String crimeID;
    private HomeViewModel mHomeViewModel;

    public ConfirmReopenCaseDialog(Chip chipReopen, String crimeID) {
        this.chipReopen = chipReopen;
        this.crimeID = crimeID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_confirm_reopen_case, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mHomeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        view.findViewById(R.id.btnReopenYes).setOnClickListener(v -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("case_status", CaseStatus.PENDING.name());
            map.put("updated_at", FieldValue.serverTimestamp());
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Crimes")
                    .document(crimeID)
                    .update(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dismiss();
                                Toast.makeText(getActivity(), "Case Reopened", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                                        .navigateUp();
                                mHomeViewModel.getMyCases();
//                                ArrayList<Crime> list = mHomeViewModel.getSolvedCasesList().getValue();
//                                for (int i=0;i<list.size();i++){
//                                    if(list.get(i).getCrime_id().equals(crimeID)){
//                                        list.get(i).setCase_status(CaseStatus.PENDING.name());
//                                        mHomeViewModel.getPendingCasesList().getValue().add(list.get(i));
//                                        list.remove(i);
//                                        break;
//                                    }
//                                }
//
//                                mHomeViewModel.setSolvedCasesList(list);
//                                mHomeViewModel.setCaseTotal(mHomeViewModel.getCaseTotal().getValue()+1);
//                                mHomeViewModel.setCasePending(mHomeViewModel.getCasePending().getValue()+1);
//                                mHomeViewModel.setCaseSolved(mHomeViewModel.getCaseSolved().getValue()-1);
                            }
                        }
                    });
        });

        view.findViewById(R.id.btnReopenCancel).setOnClickListener(v -> {
            chipReopen.setChecked(false);
            dismiss();
        });
    }
}
