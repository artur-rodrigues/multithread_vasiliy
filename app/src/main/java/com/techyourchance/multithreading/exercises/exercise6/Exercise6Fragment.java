package com.techyourchance.multithreading.exercises.exercise6;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.techyourchance.multithreading.DefaultConfiguration;
import com.techyourchance.multithreading.R;
import com.techyourchance.multithreading.common.BaseFragment;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.fragment.app.Fragment;

public class Exercise6Fragment extends BaseFragment {

    public static Fragment newInstance() {
        return new Exercise6Fragment();
    }

    // UI thread
    private EditText mEdtArgument;
    private EditText mEdtTimeout;
    private Button mBtnStartWork;
    private TextView mTxtResult;

    private Exercise6UseCase useCase;
    private ComputationResult result;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useCase = new Exercise6UseCase();
        result = result -> {
            if (!Exercise6Fragment.this.isStateSaved()) {
                mTxtResult.setText(result);
                mBtnStartWork.setEnabled(true);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_6, container, false);

        mEdtArgument = view.findViewById(R.id.edt_argument);
        mEdtTimeout = view.findViewById(R.id.edt_timeout);
        mBtnStartWork = view.findViewById(R.id.btn_compute);
        mTxtResult = view.findViewById(R.id.txt_result);

        mBtnStartWork.setOnClickListener(v -> {
            if (mEdtArgument.getText().toString().isEmpty()) {
                return;
            }

            mTxtResult.setText("");
            mBtnStartWork.setEnabled(false);


            InputMethodManager imm =
                    (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mBtnStartWork.getWindowToken(), 0);

            int argument = Integer.parseInt(mEdtArgument.getText().toString());

            useCase.computeFactorial(argument, mEdtTimeout.getText().toString());
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        useCase.registerListener(result);
    }

    @Override
    public void onStop() {
        super.onStop();
        useCase.setAbortComputation(true);
        useCase.unregisterListener(result);
    }

    @Override
    protected String getScreenTitle() {
        return "Exercise 6";
    }


}
