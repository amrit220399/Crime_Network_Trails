package com.cnt.police.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnt.police.BuildConfig;
import com.cnt.police.R;
import com.cnt.police.adapters.NewsAdapter;
import com.cnt.police.models.News;
import com.cnt.police.network.RetrofitClient;
import com.cnt.police.network.RetrofitInterface;
import com.cnt.police.storage.MyPrefs;
import com.cnt.police.viewmodels.NewsViewModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";
    private RetrofitInterface api;
    private MyPrefs mPrefs;
    private RecyclerView recyclerNews;
    private NewsAdapter mNewsAdapter;
    private ArrayList<String> news_sources;
    private ArrayList<News> mNews;
    private ProgressBar mProgressBar;
    private TextView txtFetchNews;
    private int currentItems, totalItems, scrollOutItems;
    private int totalResults = 1, pageNumber = 1;
    private boolean isScrolling = false;
    private NewsViewModel mNewsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerNews = view.findViewById(R.id.recyclerNews);
        txtFetchNews = view.findViewById(R.id.txtFetchingNews);
        mProgressBar = view.findViewById(R.id.progressFetchNews);

        mNewsViewModel = new ViewModelProvider(requireActivity()).get(NewsViewModel.class);
        mNews = new ArrayList<>();


        news_sources = new ArrayList<>();
        news_sources.add("google-news-in");
        news_sources.add("the-hindu");
        news_sources.add("the-times-of-india");
        mNewsAdapter = new NewsAdapter(getActivity(), mNews);
        mPrefs = new MyPrefs(getContext());
        api = RetrofitClient.getNewsInstance().create(RetrofitInterface.class);

        recyclerNews.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerNews.setItemAnimator(new DefaultItemAnimator());
        recyclerNews.setAdapter(mNewsAdapter);
        if (mNewsViewModel.getNewsLiveData().getValue() != null && !mNewsViewModel.getNewsLiveData().getValue().isEmpty()) {
            mNews.addAll(mNewsViewModel.getNewsLiveData().getValue());
            mNewsAdapter.notifyDataSetChanged();
        } else {
            getNews(pageNumber);
        }


        recyclerNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    //  hideProgressView();
                    isScrolling = true;
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = recyclerView.getLayoutManager().getChildCount(); //shown
                totalItems = recyclerView.getLayoutManager().getItemCount(); //total in adapter
                scrollOutItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems >= totalItems - 3)) {
                    isScrolling = false;
                    pageNumber = pageNumber + 1;
                    if (pageNumber < totalResults)
                        getNews(pageNumber);
                }

            }
        });


    }

    private void getNews(int pageNumber) {
        mProgressBar.setVisibility(View.VISIBLE);
        txtFetchNews.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = api.getLatestNews(BuildConfig.NEWS_API_KEY, "police", null, pageNumber);
        Log.i(TAG, "onViewCreated: " + call.request().url().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray articles = jsonObject.getJSONArray("articles");
                        totalResults = jsonObject.getInt("totalResults");
                        Log.i(TAG, "onResponse: " + articles.length() + " " + jsonObject.getInt("totalResults"));
                        for (int i = 0; i < articles.length(); i++) {
                            Log.i(TAG, "onResponse: " + articles.getJSONObject(i).toString());
                            mNews.add(new Gson().fromJson(articles.getJSONObject(i).toString(), News.class));
                        }
                        mProgressBar.setVisibility(View.GONE);
                        txtFetchNews.setVisibility(View.GONE);
                        mNewsAdapter.notifyDataSetChanged();
                        mNewsViewModel.setNewsLiveData(mNews);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Log.e(TAG, "onResponse: CODE" + response.code() + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}