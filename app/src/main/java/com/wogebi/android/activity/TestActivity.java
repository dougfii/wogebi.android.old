package com.wogebi.android.activity;

import android.os.AsyncTask;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dougfii.android.core.base.BaseActivity;
import com.dougfii.android.core.utils.HttpUtils;
import com.wogebi.android.AppApplication;
import com.wogebi.android.R;

public class TestActivity extends BaseActivity<AppApplication> {
    private TextView text;
    private Button button;
    private String val;

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_test);

        text = (TextView) findViewById(R.id.test_text);
        text.setMovementMethod(ScrollingMovementMethod.getInstance());
        button = (Button) findViewById(R.id.test_button);
    }

    @Override
    protected void initEvents() {
        MyOnClickListener my = new MyOnClickListener();
        button.setOnClickListener(my);
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.test_button:
                    doit();
                    break;
            }
        }
    }

    private void doit() {
        addTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                HttpUtils http = new HttpUtils();
                val = http.get("http://www.baidu.com");

                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);

                text.setText(val);
            }
        });
    }
}
