package com.dougfii.android.core.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dougfii.android.core.R;

/**
 * Created by momo on 15/11/1.
 */
public class Topbar extends LinearLayout {
    private Context context;
    private View view;

    // 左区
    private LinearLayout left;
    private ImageView back;
    private ImageView logo;
    private TextView title;
    // 右区
    private LinearLayout right;
    private ImageView nearby;
    private ImageView refresh;
    private ImageView add;
    private ImageView more;
    //底边
    private View line;
    // Refresh Button Animation
    private Animation refreshAnimation;
    private RefreshState refreshState;
    // 监听
    private OnTopbarBackClickListener onTopbarBackClickListener;
    private OnTopbarNearbyClickListener onTopbarNearbyClickListener;
    private OnTopbarRefreshClickListener onTopbarRefreshClickListener;
    private OnTopbarAddClickListener onTopbarAddClickListener;
    private OnTopbarMoreClickListener onTopbarMoreClickListener;

    public Topbar(Context context) {
        super(context);
        this.context = context;
        initViews();
    }

    public Topbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initViews();
    }

    private void initViews() {
        view = LayoutInflater.from(context).inflate(R.layout.view_topbar, null);
        addView(view);

        // 左区
        left = (LinearLayout) view.findViewById(R.id.topbar_left);
        back = (ImageView) view.findViewById(R.id.topbar_back);
        logo = (ImageView) view.findViewById(R.id.topbar_logo);
        title = (TextView) view.findViewById(R.id.topbar_title);
        // 右区
        right = (LinearLayout) view.findViewById(R.id.topbar_right);
        nearby = (ImageView) view.findViewById(R.id.topbar_nearby);
        refresh = (ImageView) view.findViewById(R.id.topbar_refresh);
        add = (ImageView) view.findViewById(R.id.topbar_add);
        more = (ImageView) view.findViewById(R.id.topbar_more);
        //底边
        line = (View) view.findViewById(R.id.topbar_line);
        //动画
        refreshAnimation = AnimationUtils.loadAnimation(context, R.anim.loading);
        refreshState = RefreshState.NONE;
    }

    public void addRightView(View view) {
        right.setVisibility(View.VISIBLE);
        right.addView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public Button addRightDefaultButton(CharSequence text) {
        Button button = (Button) LayoutInflater.from(context).inflate(R.layout.view_topbar_button_default, null);
        button.setText(text);
        addRightView(button);
        return button;
    }

    public Button addRightActionButton(CharSequence text) {
        Button button = (Button) LayoutInflater.from(context).inflate(R.layout.view_topbar_button_active, null);
        button.setText(text);
        addRightView(button);
        return button;
    }

    public ImageButton addRightImageButton(@DrawableRes int resId) {
        ImageButton button = (ImageButton) LayoutInflater.from(context).inflate(R.layout.view_topbar_imagebutton_default, null);
        button.setImageResource(resId);
        addRightView(button);
        return button;
    }

    public void setTitle(CharSequence text) {
        left.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText(text);
    }

    public void setLogo(boolean show) {
        if (show) {
            left.setVisibility(View.VISIBLE);
            logo.setVisibility(View.VISIBLE);
        } else {
            logo.setVisibility(View.GONE);
        }
    }

    public void setLine(boolean show) {
        if (show) {
            line.setVisibility(View.VISIBLE);
        } else {
            line.setVisibility(View.GONE);
        }
    }

    public void onTopbarRefreshComplete() {
        if (onTopbarRefreshClickListener != null && refreshState != RefreshState.NONE) {
            refreshState = RefreshState.NONE;
            refresh.clearAnimation();
        }
    }

    public void setOnTopbarBackClickListener(OnTopbarBackClickListener l) {
        if (l != null) {
            left.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            onTopbarBackClickListener = l;
            left.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTopbarBackClickListener != null) {
                        onTopbarBackClickListener.onTopbarBackClick();
                    }
                }
            });
        }
    }

    public void setOnTopbarNearbyClickListener(OnTopbarNearbyClickListener l) {
        if (l != null) {
            right.setVisibility(View.VISIBLE);
            nearby.setVisibility(View.VISIBLE);
            onTopbarNearbyClickListener = l;
            nearby.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTopbarNearbyClickListener != null) {
                        onTopbarNearbyClickListener.onTopbarNearbyClick();
                    }
                }
            });
        }
    }

    public void setOnTopbarRefreshClickListener(OnTopbarRefreshClickListener l) {
        if (l != null) {
            right.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.VISIBLE);
            onTopbarRefreshClickListener = l;
            refresh.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTopbarRefreshClickListener != null && refreshState != RefreshState.REFRESHING) {
                        refreshState = RefreshState.REFRESHING;
                        refresh.clearAnimation();
                        refresh.startAnimation(refreshAnimation);
                        onTopbarRefreshClickListener.onTopbarRefreshClick();
                    }
                }
            });
        }
    }

    public void setOnTopbarAddClickListener(OnTopbarAddClickListener l) {
        if (l != null) {
            right.setVisibility(View.VISIBLE);
            add.setVisibility(View.VISIBLE);
            onTopbarAddClickListener = l;
            add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTopbarAddClickListener != null) {
                        onTopbarAddClickListener.onTopbarAddClick();
                    }
                }
            });
        }
    }

    public void setOnTopbarMoreClickListener(OnTopbarMoreClickListener l) {
        if (l != null) {
            right.setVisibility(View.VISIBLE);
            more.setVisibility(View.VISIBLE);
            onTopbarMoreClickListener = l;
            more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTopbarMoreClickListener != null) {
                        onTopbarMoreClickListener.onTopbarMoreClick();
                    }
                }
            });
        }
    }

    public interface OnTopbarBackClickListener {
        void onTopbarBackClick();
    }

    public interface OnTopbarNearbyClickListener {
        void onTopbarNearbyClick();
    }

    public interface OnTopbarRefreshClickListener {
        void onTopbarRefreshClick();
    }

    public interface OnTopbarAddClickListener {
        void onTopbarAddClick();
    }

    public interface OnTopbarMoreClickListener {
        void onTopbarMoreClick();
    }

    public enum RefreshState {
        NONE, // 初始状态
        REFRESHING; // 正在刷新
    }
}