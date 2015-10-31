package com.dougfii.android.core.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dougfii.android.core.R;

import java.text.NumberFormat;

/**
 * Created by momo on 15/11/1.
 */
public class DownloadDialog extends BaseDialog
{
    private static DownloadDialog instance;

    private static final int PROGRESS_CHANGED = 0;

    private ProgressBar bar;
    private TextView percent;
    private TextView value;
    private TextView tip;

    private String formatValue = "%1.2fM/%2.2fM";
    private NumberFormat formatPercent = NumberFormat.getPercentInstance();

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case PROGRESS_CHANGED:
                    update();
                    break;

                default:
                    break;
            }
        }
    };

    private DownloadDialog(Context context)
    {
        super(context);
        init();
    }

    public static DownloadDialog getDialog(Context context, CharSequence title, CharSequence message)
    {
        return getDialog(context, title, message, null, null);
    }

    public static DownloadDialog getDialog(Context context, CharSequence title, CharSequence message, CharSequence buttonPositive, DialogInterface.OnClickListener listenerPositive)
    {
        instance = new DownloadDialog(context);

        instance.setTitle(title);
        instance.setMessage(message);

        if (instance.buttonExists(buttonPositive, listenerPositive, null, null, null, null))
        {
            instance.setPositive(buttonPositive, listenerPositive);
            instance.setNegative(null, null);
            instance.setNeutral(null, null);
        }

        instance.setCancelable(false);
        instance.setCanceledOnTouchOutside(false);

        return instance;
    }

    private void init()
    {
        setDialogContentView(R.layout.view_dialog_download, null);
        bar = (ProgressBar) findViewById(R.id.dialog_download_bar);
        percent = (TextView) findViewById(R.id.dialog_download_percent);
        value = (TextView) findViewById(R.id.dialog_download_value);
        message = (TextView) findViewById(R.id.dialog_download_message);

        formatPercent.setMaximumFractionDigits(0);
    }

    private void onProgressChanged()
    {
        handler.sendEmptyMessage(PROGRESS_CHANGED);
    }

    public void setIndeterminate(boolean indeterminate)
    {
        bar.setIndeterminate(indeterminate);
    }

    public void setProgress(int val)
    {
        bar.setProgress(val);
        onProgressChanged();
    }

    public void setMax(int val)
    {
        bar.setMax(val);
    }

    @Override
    public void setMessage(CharSequence text)
    {
        if (text != null)
        {
            message.setVisibility(View.VISIBLE);
            message.setText(text);
        }
        else
        {

            message.setVisibility(View.GONE);
        }
    }

    private void update()
    {

        int progress = bar.getProgress();
        int max = bar.getMax();
        double dProgress = (double) progress / (double) (1024 * 1024);
        double dMax = (double) max / (double) (1024 * 1024);
        if (formatValue != null)
        {
            String format = formatValue;
            value.setText(String.format(format, dProgress, dMax));
        }
        else
        {
            value.setText("");
        }

        if (formatPercent != null)
        {
            double perc = (double) progress / (double) max;
            SpannableString tmp = new SpannableString(formatPercent.format(perc));
            tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            percent.setText(tmp);
        }
        else
        {
            percent.setText("");
        }
    }
}
