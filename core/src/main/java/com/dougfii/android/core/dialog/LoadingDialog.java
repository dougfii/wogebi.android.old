package com.dougfii.android.core.dialog;

import android.content.Context;
import android.widget.TextView;

import com.dougfii.android.core.R;
import com.dougfii.android.core.view.FlippingImageView;

/**
 * Created by momo on 15/11/1.
 */
public class LoadingDialog extends BaseDialog {
    private FlippingImageView icon;
    private TextView message;

    public LoadingDialog(Context context, String text) {
        super(context);
        setText(text);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.view_dialog_loading);

        icon = (FlippingImageView) findViewById(R.id.dialog_loading_icon);
        message = (TextView) findViewById(R.id.dialog_loading_message);
        icon.startAnimation();
    }

    @Override
    protected void initEvents() {
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }

    public void setText(CharSequence text) {
        if (text != null) {
            message.setText(text);
        }
    }
}