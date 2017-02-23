package lk.rc07.ten_years.touchdown.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Sabri on 2/21/2017. Image view to zoom drawable
 */

public class ImageViewZoomed extends ImageView {

    public ImageViewZoomed(Context context) {
        super(context);
    }

    public ImageViewZoomed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewZoomed(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (getDrawable() != null) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (View.MeasureSpec.getSize(widthMeasureSpec) * 1.5);
            setMeasuredDimension(width, height);
        } else {
            setMeasuredDimension(0, 0);
        }
    }
}
