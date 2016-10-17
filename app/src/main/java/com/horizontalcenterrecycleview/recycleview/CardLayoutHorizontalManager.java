package com.horizontalcenterrecycleview.recycleview;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 实现当item的宽度小于RecyclerView的宽度时候，则居中显示
 * Created by ferris on 2016年10月17日22:03:27
 */

public class CardLayoutHorizontalManager extends RecyclerView.LayoutManager {

    private int mHorizontalOffset;
    private int mVerticalOffset;
    private Pool<Rect> mItemFrames;
    private int mHGravityCenter;
    private int mVGravityCenter;
    private int mTotalWidth;
    private int mTotalHeight;
    public CardLayoutHorizontalManager() {

        mItemFrames = new Pool<>(new Pool.New<Rect>() {
            @Override
            public Rect get() { return new Rect();}
        });
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) { return;}

        detachAndScrapAttachedViews(recycler);
        View first = recycler.getViewForPosition(0);
        measureChildWithMargins(first, 0, 0);
        int itemWidth = getDecoratedMeasuredWidth(first);
        int itemHeight = getDecoratedMeasuredHeight(first);


        int left=0;
        int top=0;
        int itemcount=getItemCount();
        for (int i = 0; i < itemcount; i++) {
                if(i<3){
                    Rect item = mItemFrames.get(i);
                    mHGravityCenter=(getHorizontalSpace()-(Math.min(itemcount,3))*itemWidth)/(Math.min(itemcount+1,4));
                    mVGravityCenter=(getVerticalSpace()-itemHeight)/2;
                    item.set(left+mHGravityCenter, mVGravityCenter, left+mHGravityCenter+itemWidth,
                            mVGravityCenter + itemHeight);
                    left+=mHGravityCenter+itemWidth;
                }else{
                    Rect item = mItemFrames.get(i);
                    mHGravityCenter=(getHorizontalSpace()-(Math.min(itemcount,3))*itemWidth)/(Math.min(itemcount+1,4));
                    mVGravityCenter=(getVerticalSpace()-itemHeight)/2;
                    item.set(left+mHGravityCenter, mVGravityCenter, left+mHGravityCenter+itemWidth,
                            mVGravityCenter + itemHeight);
                    left+=mHGravityCenter+itemWidth;
                }

        }
       int  space=(getHorizontalSpace()-(Math.min(itemcount,3))*itemWidth)/(Math.min(itemcount+1,4));
        mTotalWidth = Math.max(itemcount*itemWidth+space*(itemcount+1), getHorizontalSpace());
        mTotalHeight = Math.max(itemHeight, getVerticalSpace());
        fill(recycler, state);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) { return;}
        Rect displayRect = new Rect(mHorizontalOffset, mVerticalOffset,
                getHorizontalSpace() + mHorizontalOffset,
                getVerticalSpace() + mVerticalOffset);

        Rect rect = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View item = getChildAt(i);
            rect.left = getDecoratedLeft(item);
            rect.top = getDecoratedTop(item);
            rect.right = getDecoratedRight(item);
            rect.bottom = getDecoratedBottom(item);
            if (!Rect.intersects(displayRect, rect)) {
                removeAndRecycleView(item, recycler);
            }
        }

        for (int i = 0; i < getItemCount(); i++) {
            Rect frame = mItemFrames.get(i);
            if (Rect.intersects(displayRect, frame)) {
                View scrap = recycler.getViewForPosition(i);
                addView(scrap);
                measureChildWithMargins(scrap, 0, 0);
                layoutDecorated(scrap, frame.left - mHorizontalOffset, frame.top - mVerticalOffset,
                        frame.right - mHorizontalOffset, frame.bottom - mVerticalOffset);
            }
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        if (mVerticalOffset + dy < 0) {
            dy = -mVerticalOffset;
        } else if (mVerticalOffset + dy > mTotalHeight - getVerticalSpace()) {
            dy = mTotalHeight - getVerticalSpace() - mVerticalOffset;
        }

        offsetChildrenVertical(-dy);
        fill(recycler, state);
        mVerticalOffset += dy;
        return dy;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        if (mHorizontalOffset + dx < 0) {
            dx = -mHorizontalOffset;
        } else if (mHorizontalOffset + dx > mTotalWidth - getHorizontalSpace()) {
            dx = mTotalWidth - getHorizontalSpace() - mHorizontalOffset;
        }

        offsetChildrenHorizontal(-dx);
        fill(recycler, state);
        mHorizontalOffset += dx;
        return dx;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

}