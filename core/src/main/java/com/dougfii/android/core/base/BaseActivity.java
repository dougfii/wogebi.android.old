package com.dougfii.android.core.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dougfii.android.core.R;
import com.dougfii.android.core.dialog.BaseDialog;
import com.dougfii.android.core.dialog.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by momo on 15/11/1.
 */
public abstract class BaseActivity<T extends BaseApplication> extends FragmentActivity {
    protected T application;
    protected LoadingDialog loading;
    protected List<AsyncTask<Void, Void, Boolean>> tasks = new ArrayList<>();

    protected abstract void initViews();

    protected abstract void initEvents();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (T) getApplication();
        loading = new LoadingDialog(this, getString(R.string.loading_load));

        initViews();
        initEvents();
    }

    @Override
    protected void onDestroy() {
        clearTasks();
        super.onDestroy();
    }

    protected void addTask(AsyncTask<Void, Void, Boolean> task) {
        tasks.add(task.execute());
    }

    protected void clearTasks() {
        for (AsyncTask<Void, Void, Boolean> task : tasks) {
            if (task != null && !task.isCancelled()) {
                task.cancel(true);
            }
        }

        tasks.clear();
    }

    protected void showLoading() {
        loading.show();
    }

    protected void showLoading(String text) {
        loading.setText(text);
        showLoading();
    }

    protected void dismissLoading() {
        loading.dismiss();
    }

    protected void showToast(int resId) {
        showToast(getString(resId));
    }

    protected void showToast(String text) {
        View root = LayoutInflater.from(BaseActivity.this).inflate(R.layout.view_toast, null);
        ((TextView) root.findViewById(R.id.toast_message)).setText(text);
        Toast toast = new Toast(BaseActivity.this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(root);
        toast.show();
    }

    protected BaseDialog showDialog(String title, String message) {
        BaseDialog dialog = BaseDialog.getDialog(this, title, message);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return dialog;
    }

    protected BaseDialog showDialog(String title, String message, String positiveText, DialogInterface.OnClickListener onPositiveClickListener) {
        BaseDialog dialog = BaseDialog.getDialog(this, title, message, positiveText, onPositiveClickListener);
        dialog.show();
        return dialog;
    }

    protected BaseDialog showDialog(String title, String message, String positiveText, DialogInterface.OnClickListener onPositiveClickListener, String negativeText, DialogInterface.OnClickListener onNegativeClickListener) {
        BaseDialog dialog = BaseDialog.getDialog(this, title, message, positiveText, onPositiveClickListener, negativeText, onNegativeClickListener);
        dialog.show();
        return dialog;
    }

    protected BaseDialog showDialog(String title, String message, String positiveText, DialogInterface.OnClickListener onPositiveClickListener, String neutralText, DialogInterface.OnClickListener onNeutralClickListener, String negativeText, DialogInterface.OnClickListener onNegativeClickListener) {
        BaseDialog dialog = BaseDialog.getDialog(this, title, message, positiveText, onPositiveClickListener, neutralText, onNeutralClickListener, negativeText, onNegativeClickListener);
        dialog.show();
        return dialog;
    }

    protected void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void startActivity(String action) {
        startActivity(action, null);
    }

    protected void startActivity(String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
}