package com.stark.badge.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public class BadgeLayout extends FrameLayout {

    private BadgeDrawable mBadgeDrawable;

    public BadgeLayout(@NonNull Context context) {
        this(context, null);
    }

    public BadgeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BadgeLayout);
        String badgeText = a.getString(R.styleable.BadgeLayout_BadgeLayoutText);
        int badgeColor = a.getColor(R.styleable.BadgeLayout_BadgeLayoutColor, 0xffFF4081);
        int badgeHeight = a.getDimensionPixelSize(R.styleable.BadgeLayout_BadgeSize, (int) (getResources().getDisplayMetrics().density * 12));
        boolean badgeVisible = a.getBoolean(R.styleable.BadgeLayout_BadgeLayoutVisible, false);
        a.recycle();
        setWillNotDraw(false);
        mBadgeDrawable = new BadgeDrawable(badgeHeight, badgeColor);
        mBadgeDrawable.setVisible(badgeVisible);
        mBadgeDrawable.setText(badgeText);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mBadgeDrawable.layout(left, top, right, bottom);
    }

    public BadgeLayout setBadgeColor(int argb) {
        mBadgeDrawable.setColor(argb);
        return this;
    }

    public BadgeLayout setBadgeText(String text) {
        mBadgeDrawable.setText(text);
        invalidate();
        return this;
    }

    public BadgeLayout setBadgeVisible(boolean visible) {
        boolean mVisible = mBadgeDrawable.getVisible();
        mBadgeDrawable.setVisible(visible);
        if (mVisible != visible) {
            invalidate();
        }
        return this;
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        if (mBadgeDrawable != null) {
            mBadgeDrawable.draw(canvas);
        }
    }


    private static class BadgeDrawable extends GradientDrawable {
        private String mText;
        private boolean mIsVisible;
        private TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        private int mSize;
        private int mParentWidth;
        private int mWidth;
        private int mHeight;

        public BadgeDrawable(int size, int color) {
            setColor(color);
            setAlpha(0xFF);

            mPaint.setColor(0xffffffff);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(size * 0.75f);

            mSize = size;
            mWidth = mSize;
            mHeight = mSize;
            mParentWidth = mSize;
        }

        void layout(int left, int top, int right, int bottom) {
            mParentWidth = right - left;
            updateBounds();
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            Rect oldBounds = getBounds();

            if (oldBounds.left != left || oldBounds.top != top ||
                    oldBounds.right != right || oldBounds.bottom != bottom) {
                if (!oldBounds.isEmpty()) {
                    // first invalidate the previous bounds
                    invalidateSelf();
                }
                oldBounds.set(left, top, right, bottom);
                onBoundsChange(oldBounds);
            }
        }

        private void updateBounds() {
            Rect newRect = new Rect();
            newRect.right = mParentWidth;
            newRect.top = 0;
            newRect.left = mParentWidth - Math.min(mParentWidth, mWidth);
            newRect.bottom = mHeight;
            setBounds(newRect);
        }

        void resize(int w, int h) {
            mWidth = w;
            mHeight = h;
            updateBounds();
            invalidateSelf();
        }

        public void setText(String text) {
            mText = text;
            if (TextUtils.isEmpty(mText)) {
                int size = (int) (mSize * 0.75);
                resize(size, size);
            } else {
                int width = (int) (mPaint.measureText(mText) + 0.4 * mSize);
                resize(Math.max(width, mSize), mSize);
            }
        }

        public void setVisible(boolean visible) {
            if (mIsVisible != visible) {
                invalidateSelf();
            }
            mIsVisible = visible;
        }

        public boolean getVisible() {
            return mIsVisible;
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            setCornerRadius(getBounds().height() / 2f);
        }

        @Override
        public void draw(Canvas canvas) {
            if (!mIsVisible) {
                return;
            }
            super.draw(canvas);
            if (TextUtils.isEmpty(mText)) {
                return;
            }
            canvas.drawText(mText, getBounds().exactCenterX(), getBounds().exactCenterY() - (mPaint.descent() + mPaint.ascent()) / 2, mPaint);
        }
    }
}
