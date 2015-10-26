package com.wogebi.android.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.wogebi.android.R;

public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener
{
    private final static String HEADER_NOTICE = "最后刷新时间：";
    private final static String HEADER_FORMAT = "MM-dd HH:mm";
    private final static String FOOTER_NONE = "上拉加载更多";
    private final static String FOOTER_PULL = "松开查看更多";
    private final static String FOOTER_REFRESHING = "正在加载...";
    private final static String FOOTER_NOMORE = "没有更多数据";

    private final static int HEADER_RATIO = 3;

    protected Context context;
    protected LayoutInflater inflater;

    protected int firstVisibleItem;
    protected int lastVisibleItem;
    protected boolean isTop;
    protected boolean isBottom;

    protected Point downPoint;
    protected Point movePoint;
    protected Point upPoint;

    // header
    private View header;
    private TextView headerNotice;
    private ImageView headerLoading;
    private ImageView headerArrow;

    private int headerHeight;
    private boolean isHeaderRefreshable;
    private boolean isHeaderCancelable;

    private int headerStartY;
    private HeaderState headerState;
    private boolean isHeaderBack;
    private boolean isHeaderRecored;

    // foot
    private View footer;
    private ImageView footerLoading;
    private TextView footerNotice;

    private int footerHeight;
    private boolean isFooterRefreshable;

    private int footerStartY;
    private FooterState footerState;
    private boolean isFooterRecored;

    //
    private RotateAnimation headerPullAnimation;
    private RotateAnimation headerReverseAnimation;
    private Animation headerLoadingAnimation;
    private Animation footerLoadingAnimation;

    private OnHeaderRefreshListener onHeaderRefreshListener;
    private OnHeaderManualRefreshListener onHeaderManualRefreshListener;
    private OnHeaderCancelListener onHeaderCancelListener;
    private OnFooterRefreshListener onFooterRefreshListener;

    public PullToRefreshListView(Context context)
    {
        super(context);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        setOnScrollListener(this);

        headerLoadingAnimation = AnimationUtils.loadAnimation(context, R.anim.loading);
        footerLoadingAnimation = AnimationUtils.loadAnimation(context, R.anim.loading);

        headerPullAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        headerPullAnimation.setInterpolator(new LinearInterpolator());
        headerPullAnimation.setDuration(250);
        headerPullAnimation.setFillAfter(true);

        headerReverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        headerReverseAnimation.setInterpolator(new LinearInterpolator());
        headerReverseAnimation.setDuration(200);
        headerReverseAnimation.setFillAfter(true);

        initHeader();
        // initFooter();
    }

    private void initHeader()
    {
        // header
        header = inflater.inflate(R.layout.view_pull_to_refresh_list_view_header, this, false);
        headerNotice = (TextView) header.findViewById(R.id.pull_to_refresh_list_view_header_notice);
        headerLoading = (ImageView) header.findViewById(R.id.pull_to_refresh_list_view_header_loading);
        headerArrow = (ImageView) header.findViewById(R.id.pull_to_refresh_list_view_header_arrow);

        header.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                if (onHeaderCancelListener != null && isHeaderCancelable)
                {
                    onHeaderCancelListener.onHeaderCancel();
                }
            }
        });

        measureView(header);
        addHeaderView(header);

        headerHeight = header.getMeasuredHeight();
        header.setPadding(0, -1 * headerHeight, 0, 0);
        header.invalidate();

        SimpleDateFormat format = new SimpleDateFormat(HEADER_FORMAT);
        String date = format.format(new Date());
        headerNotice.setText(HEADER_NOTICE + date);

        headerState = HeaderState.NONE;
        isHeaderRefreshable = false;
    }

    private void initFooter()
    {
        // footer
        footer = inflater.inflate(R.layout.view_pull_to_refresh_list_view_footer, this, false);
        footerLoading = (ImageView) footer.findViewById(R.id.pull_to_refresh_list_view_footer_loading);
        footerNotice = (TextView) footer.findViewById(R.id.pull_to_refresh_list_view_footer_notice);

        measureView(footer);
        addFooterView(footer);
        footerHeight = footer.getMeasuredHeight();
        // footer.setPadding(0, 0, 0, -1 * footerHeight);
        footer.invalidate();

        footerLoading.setVisibility(View.GONE);
        footerNotice.setText(FOOTER_NONE);

        footerState = FooterState.NONE;
        // isFooterRefreshable = false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        this.firstVisibleItem = firstVisibleItem;
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;

        if (view.getFirstVisiblePosition() == 1)
        {
            isTop = true;
        }
        else if (view.getLastVisiblePosition() == view.getCount() - 1)
        {
            isBottom = true;
        }
        else
        {
            isTop = false;
            isBottom = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        int action = ev.getAction();
        int x = 0;
        int y = 0;
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                x = (int) ev.getX();
                y = (int) ev.getY();
                downPoint = new Point(x, y);
                onDown(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                x = (int) ev.getX();
                y = (int) ev.getY();
                movePoint = new Point(x, y);
                onMove(ev);
                break;

            case MotionEvent.ACTION_UP:
                x = (int) ev.getX();
                y = (int) ev.getY();
                upPoint = new Point(x, y);
                onUp(ev);
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void onDown(MotionEvent ev)
    {
        if (isTop && isHeaderRefreshable)
        {
            if (firstVisibleItem == 0 && !isHeaderRecored)
            {
                isHeaderRecored = true;
                headerStartY = downPoint.y;
            }
        }

        if (isBottom && isFooterRefreshable)
        {
            if (!isFooterRecored)
            {
                isFooterRecored = true;
                footerStartY = downPoint.y;
            }
        }
    }

    public void onMove(MotionEvent ev)
    {
        if (isHeaderRefreshable)
        {
            if (!isHeaderRecored && firstVisibleItem == 0)
            {
                isHeaderRecored = true;
                headerStartY = movePoint.y;
            }

            if (headerState != HeaderState.REFRESHING && isHeaderRecored)
            {
                if (headerState == HeaderState.RELEASE)
                {
                    setSelection(0);
                    if (((movePoint.y - headerStartY) / HEADER_RATIO < headerHeight) && (movePoint.y - headerStartY) > 0)
                    {
                        headerState = HeaderState.RELEASE;
                        changeHeaderViewByState();
                    }
                    else if (movePoint.y - headerStartY <= 0)
                    {
                        headerState = HeaderState.NONE;
                        changeHeaderViewByState();
                    }
                }

                if (headerState == HeaderState.PULL)
                {
                    setSelection(0);
                    if ((movePoint.y - headerStartY) / HEADER_RATIO >= headerHeight)
                    {
                        headerState = HeaderState.RELEASE;
                        isHeaderBack = true;
                        changeHeaderViewByState();
                    }
                    else if (movePoint.y - headerStartY <= 0)
                    {
                        headerState = HeaderState.NONE;
                        changeHeaderViewByState();
                    }
                }

                if (headerState == HeaderState.NONE)
                {
                    if (movePoint.y - headerStartY > 0)
                    {
                        headerState = HeaderState.PULL;
                        changeHeaderViewByState();
                    }
                }

                if (headerState == HeaderState.PULL)
                {
                    header.setPadding(0, -1 * headerHeight + (movePoint.y - headerStartY) / HEADER_RATIO, 0, 0);
                }
                if (headerState == HeaderState.RELEASE)
                {
                    header.setPadding(0, (movePoint.y - headerStartY) / HEADER_RATIO - headerHeight, 0, 0);
                }

            }

        }

        if (isFooterRefreshable)
        {
            if (!isFooterRecored)
            {
                isFooterRecored = true;
                footerStartY = movePoint.y;
            }

            if (footerState != FooterState.REFRESHING && isFooterRecored)
            {
                if (footerState == FooterState.RELEASE)
                {
                    setSelection(lastVisibleItem);
                    if (((footerStartY - movePoint.y) / HEADER_RATIO < footerHeight) && (footerStartY - movePoint.y) > 0)
                    {
                        footerState = FooterState.RELEASE;
                        changeFooterViewByState();
                    }
                    else if (footerStartY - movePoint.y <= 0)
                    {
                        footerState = FooterState.NONE;
                        changeFooterViewByState();
                    }
                }

                if (footerState == FooterState.PULL)
                {
                    setSelection(lastVisibleItem);
                    if ((footerStartY - movePoint.y) / HEADER_RATIO >= footerHeight)
                    {
                        footerState = FooterState.RELEASE;
                        changeFooterViewByState();
                    }
                    else if (footerStartY - movePoint.y <= 0)
                    {
                        footerState = FooterState.NONE;
                        changeFooterViewByState();
                    }
                }

                if (footerState == FooterState.NONE)
                {
                    if (footerStartY - movePoint.y >= 0)
                    {
                        footerState = FooterState.PULL;
                        changeFooterViewByState();
                    }
                }
            }
        }
    }

    public void onUp(MotionEvent ev)
    {
        if (isHeaderRefreshable)
        {
            if (headerState != HeaderState.REFRESHING)
            {
                if (headerState == HeaderState.PULL)
                {
                    headerState = HeaderState.NONE;
                    changeHeaderViewByState();
                }
                if (headerState == HeaderState.RELEASE)
                {
                    headerState = HeaderState.REFRESHING;
                    changeHeaderViewByState();
                    onHeaderRefresh();

                }
            }

            isHeaderRecored = false;
            isHeaderBack = false;
        }

        if (isFooterRefreshable)
        {
            if (footerState != FooterState.REFRESHING && footerState != FooterState.NOMORE)
            {
                if (footerState == FooterState.PULL)
                {
                    footerState = FooterState.NONE;
                    changeFooterViewByState();
                }
                if (footerState == FooterState.RELEASE)
                {
                    footerState = FooterState.REFRESHING;
                    changeFooterViewByState();
                    onFooterRefresh();
                }
            }

            isFooterRecored = false;
        }
    }

    private void measureView(View child)
    {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null)
        {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0)
        {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        }
        else
        {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    private void changeHeaderViewByState()
    {
        switch (headerState)
        {
            case NONE:
                header.setPadding(0, -1 * headerHeight, 0, 0);
                headerLoading.setVisibility(View.GONE);
                headerArrow.clearAnimation();
                headerLoading.clearAnimation();
                headerArrow.setImageResource(R.mipmap.arrow_down);
                headerNotice.setVisibility(View.VISIBLE);
                break;

            case PULL:
                headerArrow.setVisibility(View.VISIBLE);
                headerLoading.setVisibility(View.GONE);
                headerNotice.setVisibility(View.VISIBLE);
                headerLoading.clearAnimation();
                headerArrow.clearAnimation();
                if (isHeaderBack)
                {
                    isHeaderBack = false;
                    headerArrow.clearAnimation();
                    headerArrow.startAnimation(headerReverseAnimation);
                }
                break;

            case RELEASE:
                headerArrow.setVisibility(View.VISIBLE);
                headerLoading.setVisibility(View.GONE);
                headerNotice.setVisibility(View.VISIBLE);
                headerArrow.clearAnimation();
                headerArrow.startAnimation(headerPullAnimation);
                headerLoading.clearAnimation();
                break;

            case REFRESHING:
                header.setPadding(0, 0, 0, 0);
                headerLoading.setVisibility(View.VISIBLE);
                headerArrow.setVisibility(View.GONE);
                headerLoading.clearAnimation();
                headerLoading.startAnimation(headerLoadingAnimation);
                headerArrow.clearAnimation();
                headerNotice.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void onHeaderRefreshComplete()
    {
        headerState = HeaderState.NONE;
        SimpleDateFormat format = new SimpleDateFormat(HEADER_FORMAT);
        String date = format.format(new Date());
        headerNotice.setText(HEADER_NOTICE + date);
        changeHeaderViewByState();

        invalidateViews();
    }

    private void onHeaderRefresh()
    {
        if (onHeaderRefreshListener != null)
        {
            onHeaderRefreshListener.onHeaderRefresh();
        }
    }

    public void onHeaderManualRefresh()
    {
        if (isHeaderRefreshable)
        {
            headerState = HeaderState.REFRESHING;
            changeHeaderViewByState();
            onHeaderRefresh();
        }
    }

    private void changeFooterViewByState()
    {
        switch (footerState)
        {
            case NONE:
                footerLoading.setVisibility(View.GONE);
                footerNotice.setText(FOOTER_NONE);
                footerNotice.setVisibility(View.VISIBLE);
                footerLoading.clearAnimation();
                isFooterRefreshable = true;
                break;

            case PULL:
                footerLoading.setVisibility(View.GONE);
                footerNotice.setText(FOOTER_PULL);
                footerNotice.setVisibility(View.VISIBLE);
                footerLoading.clearAnimation();
                break;

            case RELEASE:
                footerLoading.setVisibility(View.GONE);
                footerNotice.setText(FOOTER_PULL);
                footerNotice.setVisibility(View.VISIBLE);
                footerLoading.clearAnimation();
                break;

            case REFRESHING:
                footerLoading.setVisibility(View.VISIBLE);
                footerLoading.clearAnimation();
                footerLoading.startAnimation(footerLoadingAnimation);
                footerNotice.setText(FOOTER_REFRESHING);
                footerNotice.setVisibility(View.VISIBLE);
                break;

            case NOMORE:
                footerLoading.setVisibility(View.GONE);
                footerLoading.clearAnimation();
                footerNotice.setText(FOOTER_NOMORE);
                footerNotice.setVisibility(View.VISIBLE);
                isFooterRefreshable = false;
                break;
        }
    }

    public void onFooterRefreshComplete()
    {
        footerState = FooterState.NONE;
        changeFooterViewByState();

        invalidateViews();
    }

    private void onFooterRefresh()
    {
        if (onFooterRefreshListener != null)
        {
            onFooterRefreshListener.onFooterRefresh();
        }
    }

    public void onFooterManualRefresh()
    {
        if (isFooterRefreshable)
        {
            footerState = FooterState.REFRESHING;
            changeFooterViewByState();
            onFooterRefresh();
        }
    }

    public void onFooterNoMore()
    {
        if (isFooterRefreshable)
        {
            footerState = FooterState.NOMORE;
            changeFooterViewByState();
        }
    }

    public void onFooterReset()
    {
        //if (isFooterRefreshable)
        //{
        footerState = FooterState.NONE;
        changeFooterViewByState();
        //}
    }

    public void setOnHeaderRefreshListener(OnHeaderRefreshListener l)
    {
        onHeaderRefreshListener = l;
        isHeaderRefreshable = true;
    }

    public void setOnHeaderManualRefreshListener(OnHeaderManualRefreshListener l)
    {
        onHeaderManualRefreshListener = l;
    }

    public void setOnHeaderCancelListener(OnHeaderCancelListener l)
    {
        onHeaderCancelListener = l;
        isHeaderCancelable = true;
    }

    public void setOnFooterRefreshListener(OnFooterRefreshListener l)
    {
        onFooterRefreshListener = l;
        isFooterRefreshable = true;

        if (footer == null)
        {
            initFooter();
        }
    }

    public interface OnHeaderRefreshListener
    {
        void onHeaderRefresh();
    }

    public interface OnHeaderManualRefreshListener
    {
        void onHeaderManualRefresh();
    }

    public interface OnHeaderCancelListener
    {
        void onHeaderCancel();
    }

    public interface OnFooterRefreshListener
    {
        void onFooterRefresh();
    }

    public enum HeaderState
    {
        NONE, // 初始状态
        PULL, // 下拉
        RELEASE, // 释放
        REFRESHING // 刷新中
    }

    public enum FooterState
    {
        NONE, // 初始状态
        PULL, // 上拉
        RELEASE, // 释放
        REFRESHING, // 刷新中
        NOMORE // 没有更多
    }
}