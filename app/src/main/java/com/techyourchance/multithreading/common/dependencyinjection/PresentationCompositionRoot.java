package com.techyourchance.multithreading.common.dependencyinjection;

import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentActivity;

import com.techyourchance.multithreading.FragmentContainerWrapper;
import com.techyourchance.multithreading.common.FragmentHelper;
import com.techyourchance.multithreading.common.ScreensNavigator;
import com.techyourchance.multithreading.common.ToolbarManipulator;

import java.util.concurrent.ThreadPoolExecutor;

public class PresentationCompositionRoot {

    private final FragmentActivity mActivity;
    private final ApplicationCompositionRoot mApplicationCompositionRoot;

    public PresentationCompositionRoot(FragmentActivity activity, ApplicationCompositionRoot applicationCompositionRoot) {
        mActivity = activity;
        mApplicationCompositionRoot = applicationCompositionRoot;
    }

    public ScreensNavigator getScreensNavigator() {
        return new ScreensNavigator(getFragmentHelper());
    }

    private FragmentHelper getFragmentHelper() {
        return new FragmentHelper(mActivity, getFragmentContainerWrapper(), mActivity.getSupportFragmentManager());
    }

    private FragmentContainerWrapper getFragmentContainerWrapper() {
        return (FragmentContainerWrapper) mActivity;
    }

    public ToolbarManipulator getToolbarManipulator() {
        return (ToolbarManipulator) mActivity;
    }

    public Handler getUiHandler() {
        return new Handler(Looper.getMainLooper());
    }

    public ThreadPoolExecutor getThreadPool() {
        return mApplicationCompositionRoot.getThreadPool();
    }
}
