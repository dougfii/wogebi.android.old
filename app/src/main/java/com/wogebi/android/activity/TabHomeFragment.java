package com.wogebi.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wogebi.android.BaseApplication;
import com.wogebi.android.BaseFragment;
import com.wogebi.android.R;
import com.wogebi.android.view.Topbar;

//注意：如果先BindService再StartService---就必须先Unbind然后Stop---所以一般会先StartService
public class TabHomeFragment extends BaseFragment
{
    private TextView calendar;
    private TextView note;
    private TextView passed;
    private TextView signed;

    public TabHomeFragment()
    {
        super();
        //init();
    }

    @SuppressLint("ValidFragment")
    public TabHomeFragment(BaseApplication application, Activity activity, Context context)
    {
        super(application, activity, context);
        //init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_tab_home, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initViews()
    {
        Topbar topbar = (Topbar) findViewById(R.id.home_topbar);
        topbar.setLogo(true);
        topbar.setLine(false);
        topbar.setTitle(getString(R.string.tab_home));

        calendar = (TextView) findViewById(R.id.home_calendar);
        note = (TextView) findViewById(R.id.home_note);
        passed = (TextView) findViewById(R.id.home_passed);
        signed = (TextView) findViewById(R.id.home_signed);
    }

    @Override
    protected void initEvents()
    {
        MyOnClickListener my = new MyOnClickListener();
        calendar.setOnClickListener(my);
        note.setOnClickListener(my);
        passed.setOnClickListener(my);
        signed.setOnClickListener(my);
    }

    private class MyOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.home_calendar:
                    startActivity(CalendarActivity.class);
                    break;

                case R.id.home_note:
                    startActivity(TestActivity.class);
                    break;

                case R.id.home_passed:
                    startActivity(WalkwayListActivity.class);
                    break;

                case R.id.home_signed:
                    startActivity(SignedActivity.class);
                    break;
            }
        }
    }


//***** 广播方式 *****
//    private BroadcastReceiver broadcast;
//    private static String LOCATION_BROADCAST = "local_bcr";
//    private void registerBroadcastReceiver()
//    {
//        broadcast = new BroadcastReceiver()
//        {
//            @Override
//            public void onReceive(Context context, Intent intent)
//            {
//                code.setText(String.valueOf(intent.getIntExtra("code", 0)));
//                lati.setText(String.valueOf(intent.getDoubleExtra("latitude", 0)));
//                lont.setText(String.valueOf(intent.getDoubleExtra("longitude", 0)));
//                radius.setText(String.valueOf(intent.getFloatExtra("radius", 0)));
//                addr.setText(intent.getStringExtra("addr"));
//                time.setText(intent.getStringExtra("time"));
//            }
//        };
//
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(LOCATION_BROADCAST);
//        getActivity().registerReceiver(broadcast, filter);
//    }
//
//    private class WalkwayListener implements BDLocationListener
//    {
//        @Override
//        public void onReceiveLocation(BDLocation bdLocation)
//        {
//            Intent intent = new Intent(TabHomeFragment.LOCATION_BROADCAST);
//            intent.putExtra("code", bdLocation.getLocType());
//            intent.putExtra("latitude", bdLocation.getLat());
//            intent.putExtra("longitude", bdLocation.getLng());
//            intent.putExtra("radius", bdLocation.getRadius());
//            intent.putExtra("addr", bdLocation.getAddrStr());
//            intent.putExtra("time", bdLocation.getTime());
//            application.sendBroadcast(intent);
//        }
//    }
//
//    @Override
//    public void onDestroy()
//    {
//        getActivity().unregisterReceiver(broadcast);
//    }
}