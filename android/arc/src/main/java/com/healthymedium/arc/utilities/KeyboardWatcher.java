//
// KeyboardWatcher.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.utilities;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

public class KeyboardWatcher {

    private Activity activity;
    private View rootView;
    private OnKeyboardToggleListener listener;
    private ViewTreeObserver.OnGlobalLayoutListener viewTreeObserverListener;

    public KeyboardWatcher(Activity activity) {
        this.activity = activity;
    }

    public void setListener(OnKeyboardToggleListener listener) {
        this.listener = listener;
    }

    public void startWatch() {
        if (hasAdjustResizeInputMode()) {
            viewTreeObserverListener = new GlobalLayoutListener();
            rootView = activity.findViewById(Window.ID_ANDROID_CONTENT);
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(viewTreeObserverListener);
        } else {
            throw new IllegalArgumentException(String.format("Activity %s should have windowSoftInputMode=\"adjustResize\"" +
                    "to make KeyboardWatcher working. You can set it in AndroidManifest.xml", activity.getClass().getSimpleName()));
        }
    }

    private boolean hasAdjustResizeInputMode() {
        return (activity.getWindow().getAttributes().softInputMode & WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) != 0;
    }

    private class GlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        int initialValue;
        boolean hasSentInitialAction;
        boolean isKeyboardShown;

        @Override
        public void onGlobalLayout() {
            if (initialValue == 0) {
                initialValue = rootView.getHeight();
            } else {
                if (initialValue > rootView.getHeight()) {

                    if (!hasSentInitialAction || !isKeyboardShown) {
                        isKeyboardShown = true;
                        Log.i("KeyboardWatcher","keyboard shown");
                        if (listener != null) {
                            listener.onKeyboardShown(initialValue - rootView.getHeight());
                        }
                    }
                } else {
                    if (!hasSentInitialAction || isKeyboardShown) {
                        isKeyboardShown = false;
                        Log.i("KeyboardWatcher","keyboard closed");
                        rootView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onKeyboardClosed();
                                }
                            }
                        });
                    }
                }
                hasSentInitialAction = true;
            }
        }
    }

    public void stopWatch(){
        if (rootView != null) {
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(viewTreeObserverListener);
        }
    }

    public interface OnKeyboardToggleListener {
        void onKeyboardShown(int keyboardSize);

        void onKeyboardClosed();
    }
}
