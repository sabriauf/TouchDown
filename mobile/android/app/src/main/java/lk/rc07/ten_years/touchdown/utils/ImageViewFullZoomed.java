package lk.rc07.ten_years.touchdown.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Sabri on 2/21/2017. Image view for Player dialog zoom view
 */

public class ImageViewFullZoomed extends ImageView {

    public ImageViewFullZoomed(Context context) {
        super(context);
    }

    public ImageViewFullZoomed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewFullZoomed(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (getDrawable() != null) {
            Drawable drawable = getDrawable();
            int height =  (int) Math.ceil(drawable.getIntrinsicHeight() * 1.0f / 2);
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width, height);
        } else {
            setMeasuredDimension(0, 0);
        }
    }
}
