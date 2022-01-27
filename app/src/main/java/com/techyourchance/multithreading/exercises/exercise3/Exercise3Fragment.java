package com.techyourchance.multithreading.exercises.exercise3;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.techyourchance.multithreading.R;
import com.techyourchance.multithreading.common.BaseFragment;

public class Exercise3Fragment extends BaseFragment {

    private static final int SECONDS_TO_COUNT = 3;

    public static Fragment newInstance() {
        return new Exercise3Fragment();
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    private Button mBtnCountSeconds;
    private TextView mTxtCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_3, container, false);

        mBtnCountSeconds = view.findViewById(R.id.btn_count_seconds);
        mTxtCount = view.findViewById(R.id.txt_count);

        mBtnCountSeconds.setOnClickListener(v -> countIterations());

        return view;
    }

    @Override
    protected String getScreenTitle() {
        return "Exercise 3";
    }

    private void countIterations() {
        mBtnCountSeconds.setEnabled(false);
        startJob();
    }

    private void startJob() {
        new Thread(() -> {
            int counter = 0;

            for (int i = 0; i < 10; i++) {
                postInformation(++counter);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

            postInformation("Done");
        }).start();
    }

    private <T> void postInformation(T t) {
        handler.post(() -> {
            if (t instanceof String) {
                mTxtCount.setText((String) t);
                mBtnCountSeconds.setEnabled(true);
            } else {
                mTxtCount.setText(String.valueOf(t));
            }
        });
    }
}
