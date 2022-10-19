package com.cnt.police.viewmodels;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cnt.police.models.Crime;

public class CrimeViewModel extends ViewModel {
    private static final String TAG = "CrimeViewModel";
    private final MutableLiveData<Crime> mCrimeMutableLiveData = new MutableLiveData<>();


    public void setCrimeLiveData(Crime crime) {
        Log.i(TAG, "setCrimeLiveData: " + crime.toString());
        mCrimeMutableLiveData.setValue(crime);
    }

    public LiveData<Crime> getCrimeMutableLiveData() {
        Log.i(TAG, "getCrimeMutableLiveData: " + mCrimeMutableLiveData.getValue().toString());
        return mCrimeMutableLiveData;
    }

}
