package com.cnt.police.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cnt.police.models.Crime;
import com.cnt.police.status.CaseStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    private static final String TAG = "HomeViewModel";
    //    private final MyPrefs mPrefs;
    private final FirebaseFirestore db;
    private MutableLiveData<Integer> numGCrimes, numGCriminals, numGPending, numGClosed,
            casePending, caseTotal, caseSolved;
    private MutableLiveData<ArrayList<Crime>> mPendingCasesList, mSolvedCasesList;
    private ListenerRegistration mListenerCrimes;
    private ListenerRegistration mListenerCriminals;
    private boolean isPendingOpen;


    public HomeViewModel() {
//        super(application);
//        mPrefs = new MyPrefs(application.getApplicationContext());
        db = FirebaseFirestore.getInstance();
        numGCrimes = new MutableLiveData<>();
        numGCriminals = new MutableLiveData<>();
        numGPending = new MutableLiveData<>();
        numGClosed = new MutableLiveData<>();
        casePending = new MutableLiveData<>();
        caseTotal = new MutableLiveData<>();
        caseSolved = new MutableLiveData<>();
        mPendingCasesList = new MutableLiveData<>();
        mSolvedCasesList = new MutableLiveData<>();
        setCasePending(0);
        setCaseTotal(0);
        setCaseSolved(0);
        getMyCases();
        updateDashboard();
    }

//    public HomeViewModel() {
//
//        mPrefs = new MyPrefs();
//        db = FirebaseFirestore.getInstance();
//        numGCrimes = new MutableLiveData<>();
//        numGCriminals = new MutableLiveData<>();
//        numGPending = new MutableLiveData<>();
//        numGClosed = new MutableLiveData<>();
//        casePending = new MutableLiveData<>();
//        caseTotal = new MutableLiveData<>();
//        caseSolved = new MutableLiveData<>();
//        mPendingCasesList = new MutableLiveData<>();
//        mSolvedCasesList = new MutableLiveData<>();
//    }

    public boolean isPendingOpen() {
        return isPendingOpen;
    }

    public void setPendingOpen(boolean pendingOpen) {
        isPendingOpen = pendingOpen;
    }

    public MutableLiveData<Integer> getNumGCrimes() {
        return numGCrimes;
    }

    public void setNumGCrimes(int i) {
        numGCrimes.setValue(i);
    }

    public MutableLiveData<Integer> getNumGCriminals() {
        return numGCriminals;
    }

    public void setNumGCriminals(int i) {
        numGCriminals.setValue(i);
    }

    public MutableLiveData<Integer> getNumGPending() {
        return numGPending;
    }

    public void setNumGPending(int i) {
        numGPending.setValue(i);
    }

    public MutableLiveData<Integer> getNumGClosed() {
        return numGClosed;
    }

    public void setNumGClosed(int i) {
        numGClosed.setValue(i);
    }

    public MutableLiveData<Integer> getCasePending() {
        return casePending;
    }

    public void setCasePending(int i) {
        casePending.setValue(i);
    }

    public MutableLiveData<Integer> getCaseTotal() {
        return caseTotal;
    }

    public void setCaseTotal(int i) {
        caseTotal.setValue(i);
    }

    public MutableLiveData<Integer> getCaseSolved() {
        return caseSolved;
    }

    public void setCaseSolved(int i) {
        caseSolved.setValue(i);
    }

    public MutableLiveData<ArrayList<Crime>> getPendingCasesList() {
        return mPendingCasesList;
    }

    public void setPendingCasesList(ArrayList<Crime> crimes) {
        mPendingCasesList.setValue(crimes);
    }

    public MutableLiveData<ArrayList<Crime>> getSolvedCasesList() {
        return mSolvedCasesList;
    }

    public void setSolvedCasesList(ArrayList<Crime> crimes) {
        mSolvedCasesList.setValue(crimes);
    }

    public void getMyCases() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser() == null ? "" : mAuth.getCurrentUser().getUid();
        db.collection("Crimes")
                .whereEqualTo("crime_adder_authority_id", uid)
                .orderBy("created_at", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            int caseInProgress = 0, caseSolved = 0, caseTotal = 0;
                            ArrayList<Crime> mPendingCasesArrayList = new ArrayList<>();
                            ArrayList<Crime> mSolvedCasesArrayList = new ArrayList<>();
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                                Crime crime = snapshot.toObject(Crime.class);
                                if (crime == null)
                                    continue;
                                if (crime.getCase_status().equals(CaseStatus.SOLVED.name())) {
                                    mSolvedCasesArrayList.add(crime);
                                    Log.i(TAG, "onComplete: " + crime.toString());
                                    caseSolved++;
                                    Log.i(TAG, "onComplete: " + caseSolved);
                                } else {
                                    if (crime.getCase_status().equals(CaseStatus.IN_PROGRESS.name())) {
                                        caseInProgress++;
                                    }
                                    caseTotal++;
                                    mPendingCasesArrayList.add(crime);
                                }
                            }
                            setCaseSolved(caseSolved);
                            setCasePending(caseInProgress);
                            setCaseTotal(caseTotal);
                            setPendingCasesList(mPendingCasesArrayList);
                            setSolvedCasesList(mSolvedCasesArrayList);
                        }
                    }
                });
    }


    private void updateDashboard() {
        mListenerCrimes = db.collection("Crimes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.i(TAG, "onEvent: " + error.getMessage());
                    return;
                }
                setNumGCrimes(value.getDocuments().size());
                int pendingCount = 0, solvedCount = 0;
                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    if (snapshot.getString("case_status") == null) {
                        return;
                    }
                    if (snapshot.getString("case_status").equals(CaseStatus.SOLVED.name())) {
                        solvedCount++;
                    } else {
                        pendingCount++;
                    }
                }
                setNumGPending(pendingCount);
                setNumGClosed(solvedCount);
            }
        });
        mListenerCriminals = db.collection("Criminals").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                setNumGCriminals(value.getDocuments().size());
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mListenerCrimes != null) {
            mListenerCrimes.remove();
        }
        if (mListenerCriminals != null) {
            mListenerCriminals.remove();
        }
    }
}