package com.gitee.cnsukidayo.traditionalenglish.activity.adapter;

import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.gitee.cnsukidayo.traditionalenglish.activity.adapter.listener.MoveAndSwipedListener;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private MoveAndSwipedListener mAdapter;

    public SimpleItemTouchHelperCallback(MoveAndSwipedListener listener) {
        mAdapter = listener;
    }

    /**
     * 支持长按进入拖动
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 支持左滑和右划删除
     *
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    /**
     * 指定拖动和滑动支持的方向
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //设置拖拽方向为上下
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //设置侧滑方向为从左到右和从右到左都可以
        final int swipeFlags = ItemTouchHelper.START;
        //将方向参数设置进去
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

/*
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder instanceof StateChangedListener) {
            StateChangedListener listener = (StateChangedListener) viewHolder;
            listener.onItemSelected();
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof StateChangedListener) {
            StateChangedListener listener = (StateChangedListener) viewHolder;
            listener.onItemClear();
        }
    }

 */

    //限制ImageView长度所能增加的最大值
    private double ICON_MAX_SIZE = 50;
    //ImageView的初始长宽
    private int fixedWidth = 150;

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //重置改变，防止由于复用而导致的显示问题
        viewHolder.itemView.setScrollX(0);
        ((StartSingleCategoryAdapter.RecyclerViewHolder) viewHolder).tv.setText("左滑删除");
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ((StartSingleCategoryAdapter.RecyclerViewHolder) viewHolder).iv.getLayoutParams();
        params.width = 150;
        params.height = 150;
        ((StartSingleCategoryAdapter.RecyclerViewHolder) viewHolder).iv.setLayoutParams(params);
        ((StartSingleCategoryAdapter.RecyclerViewHolder) viewHolder).iv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //仅对侧滑状态下的效果做出改变
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //如果dX小于等于删除方块的宽度，那么我们把该方块滑出来
            if (Math.abs(dX) <= getSlideLimitation(viewHolder)) {
                viewHolder.itemView.scrollTo(-(int) dX, 0);
            }
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    /**
     * 获取删除方块的宽度
     */
    public int getSlideLimitation(RecyclerView.ViewHolder viewHolder) {
        ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
        return viewGroup.getChildAt(1).getLayoutParams().width;
    }

}