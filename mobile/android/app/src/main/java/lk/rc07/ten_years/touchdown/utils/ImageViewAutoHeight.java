package lk.rc07.ten_years.touchdown.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Sabri on 2/4/2017. ImageView for height match for width
 */

public class ImageViewAutoHeight extends AppCompatImageView {

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
            Drawable drawable = getDrawable();
            if (drawable.getIntrinsicHeight() > drawable.getIntrinsicWidth())
                setFullHeight(drawable, heightMeasureSpec);
            else
                setFullWidth(drawable, widthMeasureSpec);
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    private void setFullWidth(Drawable drawable, int measureSpec) {
        int width = View.MeasureSpec.getSize(measureSpec);
        int height = (int) Math.ceil(width * drawable.getIntrinsicHeight() * 1.0f / drawable.getIntrinsicWidth());
        setMeasuredDimension(width, height);
    }

    private void setFullHeight(Drawable drawable, int measureSpec) {
        int height = View.MeasureSpec.getSize(measureSpec);
        int width = (int) Math.ceil(height * drawable.getIntrinsicWidth() * 1.0f / drawable.getIntrinsicHeight());
        setMeasuredDimension(width, height);
    }
}
