package com.example.antifurtoappbasic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import static android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

public class HomeKeyLocker {
    private OverlayDialog mOverlayDialog;

    public void lock(Activity activity) {
        if (mOverlayDialog == null) {
            mOverlayDialog = new OverlayDialog(activity);
            mOverlayDialog.show();
        }
    }

    public void unlock() {
        if (mOverlayDialog != null) {
            mOverlayDialog.dismiss();
            mOverlayDialog = null;
        }
    }

    private static class OverlayDialog extends AlertDialog {

        UnlockActivity unlockActivity = new UnlockActivity();

        public OverlayDialog(Activity activity) {
            super(activity, R.style.AppTheme);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.type = TYPE_SYSTEM_ERROR;
            params.dimAmount = 0.0F; // transparent
            params.width = 0;
            params.height = 0;
            params.gravity = Gravity.BOTTOM;
            getWindow().setAttributes(params);
            getWindow().addFlags(FLAG_SHOW_WHEN_LOCKED);
            getWindow().addFlags(FLAG_NOT_TOUCH_MODAL);
            setOwnerActivity(activity);
            setCancelable(false);
        }

        public final boolean dispatchTouchEvent(MotionEvent motionevent) {
            return true;
        }

        protected final void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            FrameLayout framelayout = new FrameLayout(getContext());
            framelayout.setBackgroundColor(0);
            setContentView(framelayout);
        }

        public final boolean onKeyDown(int keyCode, KeyEvent event) {
             return this.unlockActivity.onKeyDown(keyCode, event);
        }
    }
}
