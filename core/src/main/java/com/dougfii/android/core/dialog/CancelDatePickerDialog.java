package com.dougfii.android.core.dialog;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * Created by momo on 15/11/1.
 */
//解决按返回键时触发 OnDateSetListener.onDateSet 问题
public class CancelDatePickerDialog extends DatePickerDialog {
    public CancelDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    @Override
    protected void onStop() {
        //super.onStop();
    }
}
