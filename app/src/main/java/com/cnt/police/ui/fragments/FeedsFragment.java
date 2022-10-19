package com.cnt.police.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cnt.police.R;
import com.cnt.police.adapters.FeedsTabbedAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class FeedsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feeds, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.feeds_tabLayout);
        viewPager2 = view.findViewById(R.id.feeds_viewPager2);
        setUpViewPager();
    }

    private void setUpViewPager() {
        FeedsTabbedAdapter homeTabbedAdapter = new FeedsTabbedAdapter(getActivity());
        viewPager2.setAdapter(homeTabbedAdapter);
        viewPager2.setOffscreenPageLimit(2);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("News Updates");
                    break;
                case 1:
                    tab.setText("Internal Updates");
                    break;
            }
        }).attach();

    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
        bottomNavigationView.getMenu().findItem(R.id.bottom_nav_feeds).setChecked(true);
    }
}