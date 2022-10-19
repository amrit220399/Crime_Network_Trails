package com.cnt.police.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnt.police.R;
import com.cnt.police.adapters.InternalFeedsAdapter;
import com.cnt.police.models.Feed;
import com.cnt.police.storage.MyPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class InternalFeedsFragment extends Fragment {

    private ExtendedFloatingActionButton fabCreateFeed;
    private RecyclerView recyclerInternalFeeds;
    private InternalFeedsAdapter mInternalFeedsAdapter;
    private ArrayList<Feed> mFeeds;
    private MyPrefs mPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_internal_feeds, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabCreateFeed = view.findViewById(R.id.fabCreateFeed);
        recyclerInternalFeeds = view.findViewById(R.id.recyclerInternalFeeds);
        mPrefs = new MyPrefs(requireContext());
        mFeeds = new ArrayList<>();
        mInternalFeedsAdapter = new InternalFeedsAdapter(getContext(), mFeeds);
        recyclerInternalFeeds.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerInternalFeeds.setItemAnimator(new DefaultItemAnimator());
        recyclerInternalFeeds.setAdapter(mInternalFeedsAdapter);

        getFeeds();
        fabCreateFeed.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_bottom_nav_feeds_to_createFeedFragment);
        });
    }

    private void getFeeds() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Feeds")
                .whereEqualTo("creatorLocation", mPrefs.getCity())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                                mFeeds.add(snapshot.toObject(Feed.class));
                            }
                            mInternalFeedsAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}