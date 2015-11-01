package com.wogebi.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dougfii.android.core.base.BaseFragment;
import com.dougfii.android.core.view.rounded.RoundedImageView;
import com.wogebi.android.AppApplication;
import com.wogebi.android.R;
import com.wogebi.android.model.Model;

public class TabPersonalFragment extends BaseFragment<AppApplication> {
    private LinearLayout topbar;

    // Items
    private RelativeLayout config;
    private RelativeLayout feedback;
    private RelativeLayout update;
    private RelativeLayout about;
    private RelativeLayout logout;

    public TabPersonalFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public TabPersonalFragment(AppApplication application, Activity activity, Context context) {
        super(application, activity, context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_personal, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initViews() {
        topbar = (LinearLayout) findViewById(R.id.personal_topbar);
        RoundedImageView avatar = (RoundedImageView) findViewById(R.id.personal_topbar_avatar);
        RelativeLayout info = (RelativeLayout) findViewById(R.id.personal_topbar_info);
        TextView user = (TextView) findViewById(R.id.personal_topbar_user);
        TextView serial = (TextView) findViewById(R.id.personal_topbar_serial);
        // Items
        config = (RelativeLayout) findViewById(R.id.personal_item_config);
        feedback = (RelativeLayout) findViewById(R.id.personal_item_feedback);
        update = (RelativeLayout) findViewById(R.id.personal_item_update);
        about = (RelativeLayout) findViewById(R.id.personal_item_about);
        config = (RelativeLayout) findViewById(R.id.personal_item_config);
        logout = (RelativeLayout) findViewById(R.id.personal_item_logout);

        TextView version = (TextView) findViewById(R.id.personal_detail_version);

        user.setText(Model.My.getName());
        serial.setText(Model.My.getSerial());
    }

    @Override
    protected void initEvents() {
        MyOnClickListener my = new MyOnClickListener();
        topbar.setOnClickListener(my);
        config.setOnClickListener(my);
        feedback.setOnClickListener(my);
        update.setOnClickListener(my);
        about.setOnClickListener(my);
        config.setOnClickListener(my);
        logout.setOnClickListener(my);
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            switch (id) {
                case R.id.personal_item_config:
                    break;

                case R.id.personal_item_feedback:
                    showToast("意见反馈");
                    break;

                case R.id.personal_item_update:
                    //update();
                    break;

                case R.id.personal_item_about:
                    showToast("关于我们");
                    break;

                case R.id.personal_item_logout:
                    logout();
                    break;
            }
        }
    }

    private void logout() {
        showDialog("提示", "您确认要退出登录吗？", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Model.My = null;

                SharedPreferences preferences = context.getSharedPreferences("gerp_login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                System.exit(0);
            }
        }, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
}