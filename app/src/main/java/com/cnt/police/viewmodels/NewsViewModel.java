package com.cnt.police.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cnt.police.models.News;

import java.util.ArrayList;

public class NewsViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<News>> mNewsLiveData;

    public NewsViewModel() {
        mNewsLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<News>> getNewsLiveData() {
        return mNewsLiveData;
    }

    public void setNewsLiveData(ArrayList<News> newsList) {
        mNewsLiveData.setValue(newsList);
    }
}
