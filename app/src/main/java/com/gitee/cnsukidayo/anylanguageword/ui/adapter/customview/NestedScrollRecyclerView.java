package com.gitee.cnsukidayo.anylanguageword.ui.adapter.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.anylanguageword.R;
import com.gitee.cnsukidayo.anylanguageword.utils.FlingUtil;

/**
 * 解决NestedScroll嵌套recyclerView不复用的问题
 *
 * @author cnsukidayo
 * @date 2023/2/14 13:20
 */
public class NestedScrollRecyclerView extends NestedScrollView {

    private final Context mContext;
    private RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private LinearLayout content;
    private int contentHeight = 0;

    public NestedScrollRecyclerView(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public NestedScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public NestedScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.relativeLayout = findViewById(R.id.fragment_post_comment_relative_layout);
        this.recyclerView = findViewById(R.id.fragment_post_comment_recycler_view);
        this.content = findViewById(R.id.fragment_post_content);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
        params.height = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        contentHeight = content.getMeasuredHeight();
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (getScrollY() < contentHeight) {
            if (getScrollY() + dy <= contentHeight) {
                scrollBy(0, dy);
                consumed[1] = dy;
            } else if (getScrollY() + dy > contentHeight) {
                int scrollViewNeedScrollY = contentHeight - getScrollY();
                scrollBy(0, scrollViewNeedScrollY);
                consumed[1] = scrollViewNeedScrollY;
            }
        }

    }

    @Override
    public void fling(int velocityY) {
        double dy = FlingUtil.getDistanceByVelocity(mContext, velocityY);
        if (getScrollY() < contentHeight) {
            if (getScrollY() + dy <= contentHeight) {
                super.fling(velocityY);
            } else if (getScrollY() + dy > contentHeight) {
                int scrollViewNeedScrollY = contentHeight - getScrollY();
                //让NestedScrollView先处理所有的滚动事件
                int scrollViewNeedVelocity =
                        FlingUtil.getVelocityByDistance(mContext, (double) scrollViewNeedScrollY);
                if (velocityY > 0) {
                    super.fling(scrollViewNeedVelocity);
                } else {
                    super.fling(-scrollViewNeedVelocity);
                }
                //把剩余的滚动事件交给RecyclerView处理
                double recyclerViewScrollY = dy - scrollViewNeedScrollY;
                int recyclerViewNeedVelocity =
                        FlingUtil.getVelocityByDistance(mContext, recyclerViewScrollY);
                if (velocityY > 0) {
                    recyclerView.fling(0, recyclerViewNeedVelocity);
                } else {
                    recyclerView.fling(0, -recyclerViewNeedVelocity);
                }
            }
        }
    }


}
