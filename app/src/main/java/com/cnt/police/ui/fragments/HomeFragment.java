package com.cnt.police.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cnt.police.R;
import com.cnt.police.adapters.CaseAdapter;
import com.cnt.police.models.Crime;
import com.cnt.police.viewmodels.HomeViewModel;
import com.google.android.material.button.MaterialButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private MaterialButton btnAddCriminal, btnCreateFIR;

    private TextView txtCrimeNum, txtCriminalNum, txtPendingNum, txtSolvedNum,
            txtPendingCasesRatio, txtSolvedCasesCount;
    private ProgressBar mProgressBarCases;
    private ImageView imgUpArrow1, imgUpArrow2;
    private RecyclerView mRecyclerPendingCases, mRecyclerSolvedCases;
    private CardView mCardPending, mCardSolved;
    private ArrayList<Crime> mPendingCases, mSolvedCases;
    private CaseAdapter mCaseAdapterPending, mCaseAdapterSolved;
    private boolean isPendingOpen = false, isSolvedOpen = false;
    private HomeViewModel mHomeViewModel;
    private TextView txtNoPendingCases, txtNoSolvedCases;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        mHomeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        mPendingCases = new ArrayList<>();
        mSolvedCases = new ArrayList<>();
        Log.i(TAG, "onViewCreated: " + mHomeViewModel.getCasePending().getValue());
        Log.i(TAG, "onViewCreated: " + !mHomeViewModel.isPendingOpen());
        if (mHomeViewModel.getCasePending().getValue() != null
                && mHomeViewModel.getCasePending().getValue() > 0
                && !mHomeViewModel.isPendingOpen()) {
            isPendingOpen = false;
            mCardPending.performClick();
        }
        mCaseAdapterPending = new CaseAdapter(getActivity(), mPendingCases);
        mCaseAdapterSolved = new CaseAdapter(getActivity(), mSolvedCases);

        mRecyclerPendingCases.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPendingCases.setItemAnimator(new DefaultItemAnimator());
        mRecyclerPendingCases.setAdapter(mCaseAdapterPending);

        mRecyclerSolvedCases.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerSolvedCases.setItemAnimator(new DefaultItemAnimator());
        mRecyclerSolvedCases.setAdapter(mCaseAdapterSolved);
        mRecyclerPendingCases.setNestedScrollingEnabled(false);
        mRecyclerSolvedCases.setNestedScrollingEnabled(false);

        btnAddCriminal.setOnClickListener(v -> openCropImageActivity());

        btnCreateFIR.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_bottom_nav_home_to_createFirActivity);
        });


        mCardPending.setOnClickListener(v -> {
            if (isPendingOpen) {
                isPendingOpen = false;
                imgUpArrow1.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                mRecyclerPendingCases.setVisibility(View.GONE);
                if (mPendingCases.isEmpty()) {
                    txtNoPendingCases.setVisibility(View.GONE);
                }
            } else {
                isPendingOpen = true;
                imgUpArrow1.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                mRecyclerPendingCases.setVisibility(View.VISIBLE);
                if (mPendingCases.isEmpty()) {
                    txtNoPendingCases.setVisibility(View.VISIBLE);
                }
            }
            mHomeViewModel.setPendingOpen(!isPendingOpen);
        });

        mCardSolved.setOnClickListener(v -> {
            if (isSolvedOpen) {
                isSolvedOpen = false;
                imgUpArrow2.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                mRecyclerSolvedCases.setVisibility(View.GONE);
                if (mSolvedCases.isEmpty()) {
                    txtNoSolvedCases.setVisibility(View.GONE);
                }
            } else {
                isSolvedOpen = true;
                imgUpArrow2.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                mRecyclerSolvedCases.setVisibility(View.VISIBLE);
                if (mSolvedCases.isEmpty()) {
                    txtNoSolvedCases.setVisibility(View.VISIBLE);
                }
            }
        });

        mHomeViewModel.getNumGCrimes().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                txtCrimeNum.setText(String.valueOf(integer));
            }
        });
        mHomeViewModel.getNumGCriminals().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                txtCriminalNum.setText(String.valueOf(integer));
            }
        });
        mHomeViewModel.getNumGPending().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                txtPendingNum.setText(String.valueOf(integer));
            }
        });
        mHomeViewModel.getNumGClosed().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                txtSolvedNum.setText(String.valueOf(integer));
            }
        });

        mHomeViewModel.getCaseSolved().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.i(TAG, "onChanged: " + integer);
                txtSolvedCasesCount.setText(String.valueOf(integer));
                if (integer == 0) {
                    txtNoSolvedCases.setVisibility(View.VISIBLE);
                } else {
                    txtNoSolvedCases.setVisibility(View.GONE);
                }
            }
        });
        mHomeViewModel.getCaseTotal().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                int pendingCases = mHomeViewModel.getCasePending().getValue();
                txtPendingCasesRatio.setText(String.valueOf(pendingCases)
                        .concat("/").concat(String.valueOf(integer)));
                if (mHomeViewModel.getCasePending().getValue() > 0) {
                    mCardPending.performClick();
                    mProgressBarCases.setProgress(pendingCases);
                    mProgressBarCases.setMax(integer);
                }
                if (integer == 0) {
                    txtNoPendingCases.setVisibility(View.VISIBLE);
                } else {
                    txtNoPendingCases.setVisibility(View.GONE);
                }
            }
        });

        mHomeViewModel.getCasePending().observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                txtPendingCasesRatio.setText(String.valueOf(integer)
                        .concat("/").concat(String.valueOf(mHomeViewModel.getCaseTotal().getValue())));
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBarCases.setProgress(integer);
                    }
                });

            }
        });
        mHomeViewModel.getPendingCasesList().observe(getActivity(), new Observer<ArrayList<Crime>>() {
            @Override
            public void onChanged(ArrayList<Crime> crimes) {
                if (!mPendingCases.isEmpty())
                    mPendingCases.clear();
                mPendingCases.addAll(crimes);
                mCaseAdapterPending.notifyDataSetChanged();
            }
        });
        mHomeViewModel.getSolvedCasesList().observe(getActivity(), new Observer<ArrayList<Crime>>() {
            @Override
            public void onChanged(ArrayList<Crime> crimes) {
                if (!mSolvedCases.isEmpty())
                    mSolvedCases.clear();
                mSolvedCases.addAll(crimes);
                mCaseAdapterSolved.notifyDataSetChanged();
            }
        });

    }

    private void openCropImageActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.OFF)
                .setAspectRatio(1, 1)
                .start(requireActivity());
    }

    private void initViews(View view) {
        btnAddCriminal = view.findViewById(R.id.home_btnAddCriminal);
        txtCrimeNum = view.findViewById(R.id.txtCrimeNum);
        txtCriminalNum = view.findViewById(R.id.txtCriminalNum);
        txtPendingNum = view.findViewById(R.id.txtPendingNum);
        txtSolvedNum = view.findViewById(R.id.txtSolvedNum);
        imgUpArrow1 = view.findViewById(R.id.imgArrUp1);
        imgUpArrow2 = view.findViewById(R.id.imgArrUp2);
        mRecyclerPendingCases = view.findViewById(R.id.recyclerPendingCases);
        mRecyclerSolvedCases = view.findViewById(R.id.recyclerSolvedCases);
        mCardPending = view.findViewById(R.id.card_Pending);
        mCardSolved = view.findViewById(R.id.card_Solved);
        txtPendingCasesRatio = view.findViewById(R.id.txtPendingCasesRatio);
        txtSolvedCasesCount = view.findViewById(R.id.txtSolvedCasesCount);
        mProgressBarCases = view.findViewById(R.id.progressCases);
        txtNoPendingCases = view.findViewById(R.id.txtNoPendingCases);
        txtNoSolvedCases = view.findViewById(R.id.txtNoSolvedCases);
        btnCreateFIR = view.findViewById(R.id.btnCreateFIR);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }



}