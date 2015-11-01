package com.wogebi.android.activity;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.dougfii.android.core.base.BaseActivity;
import com.dougfii.android.core.dialog.CancelDatePickerDialog;
import com.dougfii.android.core.entity.ResultEntity;
import com.dougfii.android.core.entity.SimpleEntity;
import com.dougfii.android.core.log.L;
import com.dougfii.android.core.utils.DateTimeUtils;
import com.dougfii.android.core.utils.HardwareUtils;
import com.dougfii.android.core.utils.HttpUtils;
import com.dougfii.android.core.utils.Utils;
import com.dougfii.android.core.view.Topbar;
import com.wogebi.android.AppApplication;
import com.wogebi.android.R;
import com.wogebi.android.entity.ResolveEntity;
import com.wogebi.android.model.Constants;
import com.wogebi.android.model.Model;

public class CalendarAddActivity extends BaseActivity<AppApplication> {
    private static final String TAG = "CalendarAddActivity";

    private static final String URL = Constants.URL_SERVER + Constants.MODEL_CALENDAR_ADD;
    private ResultEntity<SimpleEntity> ret;

    private EditText date;
    private Spinner state;
    private EditText customer;
    private EditText contacts;
    private EditText content;
    private EditText plan;
    private EditText walkway;
    private EditText distance;
    private EditText memo;
    private Button save;

    private CancelDatePickerDialog dialog;

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_calendar_add);

        Topbar topbar = (Topbar) findViewById(R.id.calendar_add_topbar);
        topbar.setLogo(true);
        topbar.setLine(true);
        topbar.setTitle(getString(R.string.title_calendar_add));

        date = (EditText) findViewById(R.id.calendar_add_date);
        state = (Spinner) findViewById(R.id.calendar_add_state);
        customer = (EditText) findViewById(R.id.calendar_add_customer);
        contacts = (EditText) findViewById(R.id.calendar_add_contacts);
        content = (EditText) findViewById(R.id.calendar_add_content);
        plan = (EditText) findViewById(R.id.calendar_add_plan);
        walkway = (EditText) findViewById(R.id.calendar_add_walkway);
        distance = (EditText) findViewById(R.id.calendar_add_distance);
        memo = (EditText) findViewById(R.id.calendar_add_memo);
        save = topbar.addRightActionButton(getString(R.string.btn_save));

        date.setInputType(InputType.TYPE_NULL);//不显示输入法

        ArrayAdapter stateAdapter = ArrayAdapter.createFromResource(this, R.array.sel_calendar_state, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(stateAdapter);

        // init date dialog
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        setDate(year, month + 1, day);

        MyOnDateSetListener listener = new MyOnDateSetListener();
        dialog = new CancelDatePickerDialog(this, listener, year, month, day);
    }

    @Override
    protected void initEvents() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });

        state.setOnItemSelectedListener(new StateSelectedListener());

        date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    dialog.show();
                }

                return false;
            }
        });
    }

    private void add() {
        if (!HardwareUtils.isNetworkAvailable(this)) {
            showToast(R.string.no_network);
            return;
        }

        final int _uid = Model.My.getId();
        final String _date = date.getText().toString();
        final int _stateid = Utils.toInteger(state.getSelectedItemId()) + 1;
        final String _customer = customer.getText().toString();
        final String _contacts = contacts.getText().toString();
        final String _content = content.getText().toString();
        final String _plan = plan.getText().toString();
        final String _walkway = walkway.getText().toString();
        final String _distance = distance.getText().toString();
        final String _memo = memo.getText().toString();

        if (_customer.equals("")) {
            showToast("请输入客户");
            return;
        }
        if (_contacts.equals("")) {
            showToast("请输入联系人");
            return;
        }
        if (_content.equals("")) {
            showToast("请输入谈何事");
            return;
        }

        addTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading(getString(R.string.loading_load));
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    String url = URL;//+ "&uid=" + _uid + "&date=" + _date + "&place=" + _place + "&typeid=" + _typeid + "&stateid=" + _stateid + "&content=" + _content;
                    L.i(TAG, url);

                    Map<String, String> pl = new HashMap<>();
                    pl.put("uid", Utils.toString(_uid));
                    pl.put("date", _date);
                    pl.put("stateid", Utils.toString(_stateid));
                    pl.put("customer", _customer);
                    pl.put("contacts", _contacts);
                    pl.put("content", _content);
                    pl.put("plan", _plan);
                    pl.put("walkway", _walkway);
                    pl.put("distance", _distance);
                    pl.put("memo", _memo);

                    HttpUtils http = new HttpUtils();
                    //String json = http.get(url);
                    String json = http.post(url, pl);
                    L.i(TAG, json);

                    ret = (new ResolveEntity()).getSimple(json);
                    if (ret != null) {
                        return true;
                    }
                } catch (Exception ex) {
                    //
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dismissLoading();

                if (result && ret.getCode() == 1) {
                    showToast("增加成功");
                    finish();
                } else if (result && ret.getCode() == 0) {
                    showToast(ret.getMsg());
                } else {
                    showToast(R.string.submit_error);
                }
            }
        });
    }

    private void setDate(int year, int month, int day) {
        date.setText(DateTimeUtils.formatDate(year, month, day));
    }

    class StateSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    class MyOnDateSetListener implements DatePickerDialog.OnDateSetListener {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            setDate(year, monthOfYear + 1, dayOfMonth);
        }
    }
}