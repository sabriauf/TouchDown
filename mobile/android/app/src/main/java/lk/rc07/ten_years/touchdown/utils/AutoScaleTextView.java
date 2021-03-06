package lk.rc07.ten_years.touchdown.utils;

import android.content.Context;
import android.graphics.Rect;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import lk.rc07.ten_years.touchdown.R;

/**
 * Created by Sabri on 12/13/2016. custom text view to auto scale the text size
 */

public class AutoScaleTextView extends AppCompatTextView {

    public static final int AUTO_RESIZE = 1;
    public static final int WIDTH_RESIZE = 2;
    public static final int HEIGHT_RESIZE = 3;

    private Rect bounds = new Rect();
    private int preference = AUTO_RESIZE;

    public AutoScaleTextView(Context context) {
        this(context, null);
    }

    public AutoScaleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.autoScaleTextViewStyle);
        setIncludeFontPadding(false);
    }

    public AutoScaleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setIncludeFontPadding(false);
    }

    public void setPreference(int preference) {
        this.preference = preference;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int viewHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        int viewWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        float heightPerTextSize = getMaxTextSizeForHeight(viewHeight);
        float widthPerTextSize = getMaxTextSizeForWidth(viewWidth);

        switch (preference) {
            case AUTO_RESIZE:
                if (heightPerTextSize < widthPerTextSize) {
                    this.setTextSize(0, heightPerTextSize);
                    setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                } else {
                    this.setTextSize(0, widthPerTextSize);
                    setGravity(Gravity.CENTER);
                }
                break;
            case WIDTH_RESIZE:
                this.setTextSize(0, widthPerTextSize);
                setGravity(Gravity.CENTER);
                break;
            case HEIGHT_RESIZE:
                this.setTextSize(0, heightPerTextSize);
                setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                break;
        }

        setText(getText());
        setMeasuredDimension(viewWidth, viewHeight);
    }

    private float getMaxTextSizeForHeight(int viewHeight) {
        viewHeight = viewHeight - (getPaddingTop() + getPaddingBottom());
        viewHeight = viewHeight - (viewHeight / 4);

        TextPaint textPaint = this.getPaint();
        textPaint.getTextBounds(this.getText().toString(), 0, this.length(), bounds);
        int textHeight = bounds.height();

        return getTextSize() / textHeight * viewHeight;
    }

    private float getMaxTextSizeForWidth(int viewWidth) {
        viewWidth = viewWidth - (getPaddingRight() + getPaddingLeft());

        TextPaint textPaint = this.getPaint();
        textPaint.getTextBounds(this.getText().toString(), 0, this.length(), bounds);
        int textWidth = bounds.width();

        return getTextSize() / textWidth * viewWidth;
    }
}
