package com.dougfii.android.core.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dougfii.android.core.R;

/**
 * Created by momo on 15/11/1.
 */
public class BaseDialog extends Dialog implements View.OnClickListener {
    private static BaseDialog instance;

    protected LinearLayout root;
    protected LinearLayout subject;
    protected TextView title;
    protected View line;
    protected LinearLayout content;
    protected TextView message;
    protected LinearLayout buttons;
    protected Button positive;
    protected Button negative;
    protected Button neutral;

    protected OnClickListener onClickListenerPositive;
    protected OnClickListener onClickListenerNegative;
    protected OnClickListener onClickListenerNeutral;

    public BaseDialog(Context context) {
        super(context, R.style.DefaultBaseDialog);
        initViews();
        initEvents();
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    protected void initViews() {
        setContentView(R.layout.view_dialog_base);

        root = (LinearLayout) findViewById(R.id.dialog_base_root);
        subject = (LinearLayout) findViewById(R.id.dialog_base_subject);
        title = (TextView) findViewById(R.id.dialog_base_title);
        line = findViewById(R.id.dialog_base_line);
        content = (LinearLayout) findViewById(R.id.dialog_base_content);
        message = (TextView) findViewById(R.id.dialog_base_message);
        buttons = (LinearLayout) findViewById(R.id.dialog_base_buttons);
        positive = (Button) findViewById(R.id.dialog_base_positive);
        negative = (Button) findViewById(R.id.dialog_base_negative);
        neutral = (Button) findViewById(R.id.dialog_base_neutral);
        root.setVisibility(View.VISIBLE);
        setLineVisibility(View.VISIBLE);
    }

    protected void initEvents() {
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
        neutral.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_base_positive) {
            if (onClickListenerPositive != null) {
                onClickListenerPositive.onClick(instance, 0);
            }
        } else if (id == R.id.dialog_base_negative) {
            if (onClickListenerNegative != null) {
                onClickListenerNegative.onClick(instance, 1);
            }
        } else if (id == R.id.dialog_base_neutral) {
            if (onClickListenerNeutral != null) {
                onClickListenerNeutral.onClick(instance, 2);
            }
        }
    }

    public static BaseDialog getDialog(Context context, CharSequence title, CharSequence message) {
        return getDialog(context, title, message, null, null, null, null, null, null);
    }

    public static BaseDialog getDialog(Context context, CharSequence title, CharSequence message, CharSequence buttonPositive, DialogInterface.OnClickListener listenerPositive) {
        return getDialog(context, title, message, buttonPositive, listenerPositive, null, null, null, null);
    }

    public static BaseDialog getDialog(Context context, CharSequence title, CharSequence message, CharSequence buttonPositive, DialogInterface.OnClickListener listenerPositive, CharSequence buttonNegative, DialogInterface.OnClickListener listenerNegative) {
        return getDialog(context, title, message, buttonPositive, listenerPositive, buttonNegative, listenerNegative, null, null);
    }

    public static BaseDialog getDialog(Context context, CharSequence title, CharSequence message, CharSequence buttonPositive, DialogInterface.OnClickListener listenerPositive, CharSequence buttonNegative, DialogInterface.OnClickListener listenerNegative, CharSequence buttonNeutral, DialogInterface.OnClickListener listenerNeutral) {
        instance = new BaseDialog(context);

        instance.setTitle(title);
        instance.setMessage(message);

        if (instance.buttonExists(buttonPositive, listenerPositive, buttonNegative, listenerNegative, buttonNeutral, listenerNeutral)) {
            instance.setPositive(buttonPositive, listenerPositive);
            instance.setNegative(buttonNegative, listenerNegative);
            instance.setNeutral(buttonNeutral, listenerNeutral);
        }

        instance.setCancelable(false);
        instance.setCanceledOnTouchOutside(false);

        return instance;
    }

    public void setDialogContentView(int resource, LinearLayout.LayoutParams params) {
        View view = LayoutInflater.from(this.getContext()).inflate(resource, null);
        if (content.getChildCount() > 0) {
            content.removeAllViews();
        }

        if (params == null) {
            content.addView(view);
        } else {
            content.addView(view, params);
        }
    }

    @Override
    public void setTitle(CharSequence text) {
        if (text != null) {
            subject.setVisibility(View.VISIBLE);
            title.setText(text);
        } else {
            subject.setVisibility(View.GONE);
        }
    }

    public void setMessage(CharSequence text) {
        if (text != null) {
            content.setVisibility(View.VISIBLE);
            message.setText(text);
        } else {

            content.setVisibility(View.GONE);
        }
    }

    public boolean buttonExists(CharSequence buttonPositive, DialogInterface.OnClickListener listenerPositive, CharSequence buttonNegative, DialogInterface.OnClickListener listenerNegative, CharSequence buttonNeutral, DialogInterface.OnClickListener listenerNeutral) {
        if ((buttonPositive != null && listenerPositive != null) || (buttonNegative != null && listenerNegative != null) || (buttonNeutral != null && listenerNeutral != null)) {
            buttons.setVisibility(View.VISIBLE);
            return true;
        } else {
            buttons.setVisibility(View.GONE);
            return false;
        }
    }

    public void setPositive(CharSequence text, DialogInterface.OnClickListener listener) {
        if (text != null && listener != null) {
            buttons.setVisibility(View.VISIBLE);
            positive.setVisibility(View.VISIBLE);
            positive.setText(text);
            onClickListenerPositive = listener;
        } else {
            positive.setVisibility(View.GONE);
        }
    }

    public void setNegative(CharSequence text, DialogInterface.OnClickListener listener) {
        if (text != null && listener != null) {
            buttons.setVisibility(View.VISIBLE);
            negative.setVisibility(View.VISIBLE);
            negative.setText(text);
            onClickListenerNegative = listener;
        } else {
            negative.setVisibility(View.GONE);
        }
    }

    public void setNeutral(CharSequence text, DialogInterface.OnClickListener listener) {
        if (text != null && listener != null) {
            buttons.setVisibility(View.VISIBLE);
            neutral.setVisibility(View.VISIBLE);
            neutral.setText(text);
            onClickListenerNeutral = listener;
        } else {
            neutral.setVisibility(View.GONE);
        }
    }

    public void setLineVisibility(int visibility) {
        line.setVisibility(visibility);
    }
}