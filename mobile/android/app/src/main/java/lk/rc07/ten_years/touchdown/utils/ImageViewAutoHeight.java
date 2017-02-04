package lk.rc07.ten_years.touchdown.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Sabri on 2/4/2017. ImageView for height match for width
 */

public class ImageViewAutoHeight extends ImageView {

    public ImageViewAutoHeight(Context context) {
        super(context);
    }

    public ImageViewAutoHeight(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewAutoHeight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (getDrawable() != null) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            Drawable drawable = getDrawable();
            int height = (int) Math.ceil(width * drawable.getIntrinsicHeight() * 1.0f / drawable.getIntrinsicWidth());
            setMeasuredDimension(width, height);
        } else {
            setMeasuredDimension(0, 0);
        }
    }
}
