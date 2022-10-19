package com.cnt.police.viewmodels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.cnt.police.models.FIR;
import com.cnt.police.models.NOC;
import com.cnt.police.storage.MyPrefs;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AppointmentsViewModel extends ViewModel {
    private MutableLiveData<ArrayList<FIR>> liveFIRList;
    private MutableLiveData<ArrayList<NOC>> liveNOCList;
    private FirebaseFirestore db;
    private MyPrefs mPrefs;

    public AppointmentsViewModel(Context context) {
        liveFIRList = new MutableLiveData<>();
        liveNOCList = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        mPrefs = new MyPrefs(context);
        getFIRList();
        getNOCList();
    }

    public MutableLiveData<ArrayList<FIR>> getLiveFIRList() {
        return liveFIRList;
    }

    public MutableLiveData<ArrayList<NOC>> getLiveNOCList() {
        return liveNOCList;
    }

    private void getNOCList() {
        db.collection("NOC")
                .whereEqualTo("police_station_id", mPrefs.getPS())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (value != null && !value.isEmpty()) {
                            ArrayList<NOC> nocArrayList = new ArrayList<>();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                nocArrayList.add(snapshot.toObject(NOC.class));
                            }
                            for (DocumentChange change : value.getDocumentChanges()) {
                                if (nocArrayList.contains(change.getDocument().toObject(NOC.class))) {
                                    nocArrayList.remove(change.getDocument().toObject(NOC.class));
                                    nocArrayList.add(change.getDocument().toObject(NOC.class));
                                }
                            }

                            liveNOCList.setValue(nocArrayList);

                        }

                    }
                });

    }

    private void getFIRList() {
        db.collection("FIR")
                .whereEqualTo("complainantID", mPrefs.getUID())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (value != null && !value.isEmpty()) {
                            ArrayList<FIR> firArrayList = new ArrayList<>();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                firArrayList.add(snapshot.toObject(FIR.class));
                            }
                            for (DocumentChange change : value.getDocumentChanges()) {
                                if (firArrayList.contains(change.getDocument().toObject(FIR.class))) {
                                    firArrayList.remove(change.getDocument().toObject(FIR.class));
                                    firArrayList.add(change.getDocument().toObject(FIR.class));
                                }
                            }

                            liveFIRList.setValue(firArrayList);

                        }

                    }
                });
    }

    public static class AppointmentViewModelFactory implements ViewModelProvider.Factory {

        private Context context;

        public AppointmentViewModelFactory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new AppointmentsViewModel(context);
        }

    }
}
