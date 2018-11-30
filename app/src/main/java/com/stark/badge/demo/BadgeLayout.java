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
        int badgeHeight = a.getDimensionPixelSize(R.styleable.BadgeLayout_BadgeLayoutHeight, (int) (getResources().getDisplayMetrics().density * 12));
        int badgePadding = a.getDimensionPixelSize(R.styleable.BadgeLayout_BadgeLayoutPadding, (int) (getResources().getDisplayMetrics().density * 12));
        boolean badgeVisible = a.getBoolean(R.styleable.BadgeLayout_BadgeLayoutVisible, false);
        a.recycle();

        setWillNotDraw(false);
        mBadgeDrawable = new BadgeDrawable(badgeHeight, badgeColor, badgePadding);
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

    public BadgeLayout setBadgeText(String text) {
        mBadgeDrawable.setText(text);
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

        private int mSize = 0;
        int mPadding = 0;

        public BadgeDrawable(int height, int color, int padding) {

            setColor(color);
            setAlpha(0xFF);

            mPaint.setColor(0xffffffff);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(height * 0.75f);

            mSize = height;
            mPadding = padding;
        }

        void layout(int left, int top, int right, int bottom) {
            int width = right - left;
            Rect rect = getBounds();
            int dotWidth = rect.width();
            rect.offset(width - dotWidth, 0);
            setBounds(rect);
        }

        void resize(int w, int h) {
            Rect rect = getBounds();
            setBounds(rect.left, rect.top, rect.left + w, rect.top + h);
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
