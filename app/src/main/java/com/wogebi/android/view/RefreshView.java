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

public class RefreshView extends ListView implements AbsListView.OnScrollListener {

    private final static int RELEASE = 0; // 释放
    private final static int PULL = 1; // 下拉
    private final static int REFRESHING = 2; // 刷新中
    private final static int LOADING = 3; //
    private final static int DONE = 4; // 初始状态

    private final static String HEADER_NOTICE = "最后刷新时间：";
    private final static String HEADER_FORMAT = "MM-dd HH:mm";

    private final static String FOOTER_NONE = "上拉加载更多";
    //private final static String FOOTER_PULL = "松开查看更多";
    private final static String FOOTER_REFRESHING = "正在加载...";
    private final static String FOOTER_NOMORE = "没有更多数据";

    private final static int HEADER_RATIO = 2; // 实际的padding的距离与界面上偏移距离的比例

    protected Context context;
    protected LayoutInflater inflater;

    protected int first;
    //protected int lastVisibleItem;
    //protected boolean isTop;
    //protected boolean isBottom;

    protected Point downPoint;
    protected Point movePoint;
    protected Point upPoint;

    // header
    private View header;
    private TextView notice;
    private ImageView loading;
    private ImageView arrow;

    private int headerHeight;
    private boolean isRefreshable;
    private boolean isCancelable;

    private int startY;
    private int state;
    private boolean isBack;
    private boolean isRecored; // 用于保证startY的值在一个完整的touch事件中只被记录一次

    // foot
    private View footer;
    private ImageView footerLoading;
    private TextView footerNotice;

    //private int footerHeight;
    //private boolean isFooterRefreshable;

    //private int footerStartY;
    //private FooterState footerState;
    //private boolean isFooterRecored;

    //
    private RotateAnimation animationNormal;
    private RotateAnimation animationReverse;
    private Animation headerAnimation;
    private Animation footerAnimation;

    private OnRefreshListener onRefreshListener;
    private OnManualRefreshListener onManualRefreshListener;
    private OnCancelListener onCancelListener;
    private OnMoreListener onFooterRefreshListener;

    public RefreshView(Context context) {
        super(context);
        init(context);
    }

    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        setOnScrollListener(this);

        headerAnimation = AnimationUtils.loadAnimation(context, R.anim.loading);
        footerAnimation = AnimationUtils.loadAnimation(context, R.anim.loading);

        animationNormal = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animationNormal.setInterpolator(new LinearInterpolator());
        animationNormal.setDuration(250);
        animationNormal.setFillAfter(true);

        animationReverse = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animationReverse.setInterpolator(new LinearInterpolator());
        animationReverse.setDuration(200);
        animationReverse.setFillAfter(true);

        initHeader();
        // initFooter();
    }

    private void initHeader() {
        // header
        header = inflater.inflate(R.layout.view_refresh_header, this, false);
        notice = (TextView) header.findViewById(R.id.refresh_notice);
        loading = (ImageView) header.findViewById(R.id.refresh_loading);
        arrow = (ImageView) header.findViewById(R.id.refresh_arrow);

        header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (onCancelListener != null && isCancelable) {
                    onCancelListener.onHeaderCancel();
                }
            }
        });

        measureView(header);

        headerHeight = header.getMeasuredHeight();
        header.setPadding(0, -1 * headerHeight, 0, 0);
        header.invalidate();

        addHeaderView(header, null, false);

        SimpleDateFormat format = new SimpleDateFormat(HEADER_FORMAT);
        String date = format.format(new Date());
        notice.setText(HEADER_NOTICE + date);

        state = DONE;
        isRefreshable = false;
    }

    private void initFooter() {
        // footer
        footer = inflater.inflate(R.layout.view_refresh_footer, this, false);
        footerLoading = (ImageView) footer.findViewById(R.id.pull_to_refresh_list_view_footer_loading);
        footerNotice = (TextView) footer.findViewById(R.id.pull_to_refresh_list_view_footer_notice);

        measureView(footer);

        //footerHeight = footer.getMeasuredHeight();
        // footer.setPadding(0, 0, 0, -1 * footerHeight);
        footer.invalidate();

        addFooterView(footer);

        footerLoading.setVisibility(View.GONE);
        footerNotice.setVisibility(View.GONE);
        footerNotice.setText(FOOTER_NONE);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        first = firstVisibleItem;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = 0;
        int y = 0;
        switch (action) {
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

    public void onDown(MotionEvent ev) {
        if (isRefreshable) {
            if (first == 0 && !isRecored) {
                isRecored = true;
                startY = downPoint.y; // 触摸屏幕的位置
                //在down时候记录当前位置" + " startY:" + startY
            }
        }
    }

    public void onMove(MotionEvent ev) {
        if (isRefreshable) {
            /**
             * 手指移动过程中tempY数据会不断变化,当滑动到firstItemIndex,即到达顶部, 需要记录手指所在屏幕的位置:
             * startY = tempY ,后面作位置比较使用
             *
             * 如果手指继续向下推,tempY继续变化,当tempY-startY>0,即是需要显示header部分
             *
             * 此时需要更改状态：state = PULL_To_REFRESH
             */
            if (!isRecored && first == 0) {
                isRecored = true;
                startY = movePoint.y;
            }

            if (state != REFRESHING && isRecored && state != LOADING) {
                /**
                 * 保证在设置padding的过程中，当前的位置一直是在head，
                 * 否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
                 */

                // 可以松手去刷新了
                if (state == RELEASE) {
                    setSelection(0);

                    if (((movePoint.y - startY) / HEADER_RATIO < headerHeight) && (movePoint.y - startY) > 0) {
                        // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                        //TODO:
                        state = PULL;
                        //state = HeaderState.RELEASE;
                        changeHeaderViewByState();
                        //由松开刷新状态转变到下拉刷新状态
                    } else if (movePoint.y - startY <= 0) {
                        // 一下子推到顶了,没有显示header部分时,应该恢复DONE状态,这里机率很小
                        state = DONE;
                        changeHeaderViewByState();
                        //---由松开刷新状态转变到done状态
                    } else {
                        // 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
                        // 不用进行特别的操作，只用更新paddingTop的值就行了
                    }
                }

                // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
                if (state == PULL) {
                    setSelection(0);

                    /**
                     * 下拉到可以进入RELEASE_TO_REFRESH的状态
                     *
                     * 等于headContentHeight时,即是正好完全显示header部分
                     * 大于headContentHeight时,即是超出header部分更多
                     *
                     * 当header部分能够完全显示或者超出显示, 需要更改状态: state =
                     * RELEASE_To_REFRESH
                     */
                    if ((movePoint.y - startY) / HEADER_RATIO >= headerHeight) {
                        state = RELEASE;
                        isBack = true;
                        changeHeaderViewByState();
                        //由done或者下拉刷新状态转变到松开刷新
                    } else if (movePoint.y - startY <= 0) {
                        state = DONE;
                        changeHeaderViewByState();
                        //由done或者下拉刷新状态转变到done状态
                    }
                }

                // done状态下
                if (state == DONE) {
                    if (movePoint.y - startY > 0) {
                        /**
                         * 手指移动过程中tempY数据会不断变化,当滑动到firstItemIndex,即到达顶部,
                         * 需要记录手指所在屏幕的位置: startY = tempY ,后面作位置比较使用
                         *
                         * 如果手指继续向下推,tempY继续变化,当tempY-startY>0,
                         * 即是需要显示header部分
                         *
                         * 此时需要更改状态：state = PULL_To_REFRESH
                         */
                        // Log.v(TAG, "----------------PULL_To_REFRESH " +
                        // (tempY - startY));
                        state = PULL;
                        changeHeaderViewByState();
                    }
                }

                /**
                 * ********** 更新headView的paddingTop ************
                 * 根据不同的top值来设置组件的位置
                 */
                if (state == PULL) {
                    // Log.v(TAG, "----------------PULL_To_REFRESH2 " +
                    // (tempY - startY));
                    // headView.setPadding(0, -1 * headContentHeight
                    // + (tempY - startY) / RATIO, 0, 0);
                    header.setPadding(0, -1 * headerHeight + (movePoint.y - startY) / HEADER_RATIO, 0, 0);
                }

                /**
                 * 继续更新headView的paddingTop
                 */
                if (state == RELEASE) {
                    header.setPadding(0, (movePoint.y - startY) / HEADER_RATIO - headerHeight, 0, 0);
                }
            }
        }
    }

    public void onUp(MotionEvent ev) {
        if (isRefreshable) {
            if (state != REFRESHING && state != LOADING) {
                if (state == DONE) {
                    // 什么都不做
                }

                if (state == PULL) {
                    state = DONE;
                    changeHeaderViewByState();
                    //由下拉刷新状态，到done状态
                }

                if (state == RELEASE) {
                    state = REFRESHING;
                    changeHeaderViewByState();
                    onRefresh();
                    //由松开刷新状态，到done状态
                }
            }

            isRecored = false;
            isBack = false;
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    private void changeHeaderViewByState() {
        switch (state) {
            case DONE:
                header.setPadding(0, -1 * headerHeight, 0, 0);
                loading.setVisibility(View.GONE);
                arrow.clearAnimation();
                loading.clearAnimation();
                arrow.setImageResource(R.mipmap.arrow_down);
                notice.setVisibility(View.VISIBLE);
                break;

            case PULL:
                arrow.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                notice.setVisibility(View.VISIBLE);
                loading.clearAnimation();
                arrow.clearAnimation();

                /**
                 * 是否向下滑回，是由RELEASE_To_REFRESH状态转变来的
                 */
                if (isBack) {
                    isBack = false;
                    arrow.clearAnimation();
                    arrow.startAnimation(animationReverse);
                }

                //TODO:
                //notice.setText("下拉刷新");

                break;

            case RELEASE:
                arrow.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                notice.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.startAnimation(animationNormal);
                loading.clearAnimation();

                //TODO:
                //notice.setText("松开刷新");

                break;

            case REFRESHING:
                header.setPadding(0, 0, 0, 0);
                loading.setVisibility(View.VISIBLE);
                arrow.setVisibility(View.GONE);
                loading.clearAnimation();
                loading.startAnimation(headerAnimation);
                arrow.clearAnimation();
                notice.setVisibility(View.VISIBLE);

                //TODO:
                //notice.setText("正在刷新");

                break;
        }
    }

    public void onRefreshComplete() {
        state = DONE;
        SimpleDateFormat format = new SimpleDateFormat(HEADER_FORMAT);
        String date = format.format(new Date());
        notice.setText(HEADER_NOTICE + date);
        changeHeaderViewByState();

        invalidateViews();
    }

    private void onRefresh() {
        if (onRefreshListener != null) {
            onRefreshListener.onRefresh();
        }
    }

    public void onManualRefresh() {
        if (isRefreshable) {
            state = REFRESHING;
            changeHeaderViewByState();
            onRefresh();
        }
    }

    private void onFooterRefresh() {
        if (onFooterRefreshListener != null) {
            onFooterRefreshListener.onMoreRefresh();
        }
    }

    public void setOnRefreshListener(OnRefreshListener l) {
        onRefreshListener = l;
        isRefreshable = true;
    }

    public void setOnManualRefreshListener(OnManualRefreshListener l) {
        onManualRefreshListener = l;
    }

    public void setOnCancelListener(OnCancelListener l) {
        onCancelListener = l;
        isCancelable = true;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnManualRefreshListener {
        void onHeaderManualRefresh();
    }

    public interface OnCancelListener {
        void onHeaderCancel();
    }

    public interface OnMoreListener {
        void onMoreRefresh();
    }
}