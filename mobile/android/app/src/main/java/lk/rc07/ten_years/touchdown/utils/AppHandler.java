package lk.rc07.ten_years.touchdown.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import lk.rc07.ten_years.touchdown.BuildConfig;
import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.config.Constant;

/**
 * Created by Sabri on 1/13/2017. application general methods
 */

public class AppHandler {

    static HashMap<String, String> getHeaders(Context context) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(Constant.PARAM_AUTH_KEY, AppConfig.APPLICATION_AUTHENTICATION_KEY);
        headers.put(Constant.PARAM_PLATFORM, String.valueOf(Constant.PLATFORM_ANDROID));
        headers.put(Constant.PARAM_AUTH_DEVICE, Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID));
        headers.put(Constant.PARAM_AUTH_PACKAGE, context.getApplicationContext().getPackageName());
        headers.put(Constant.PARAM_AUTH_VERSION, String.valueOf(BuildConfig.VERSION_CODE));
        headers.put(Constant.PARAM_API_VERSION, String.valueOf(AppConfig.API_VERSION));

        return headers;
    }

    public static DisplayImageOptions getImageOption(ImageLoader imageLoader, Context context, int resource) {
        @SuppressWarnings("deprecation") DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnFail(resource)
                .cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
        if (!imageLoader.isInited()) {
            ImageLoaderConfiguration imageConfig = new ImageLoaderConfiguration.Builder(context)
                    .memoryCache(new WeakMemoryCache()).diskCacheExtraOptions(480, 320, null).threadPoolSize(5)
                    .denyCacheImageMultipleSizesInMemory().build();
            imageLoader.init(imageConfig);
        }
        return options;
    }

    public static Drawable getDrawable(Context context, int resource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(resource, context.getTheme());
        } else {
            //noinspection deprecation
            return context.getResources().getDrawable(resource);
        }
    }

    public static int getColor(Context context, int resource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            return context.getResources().getColor(resource, context.getTheme());
            return ContextCompat.getColor(context, resource);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(resource);
        }
    }

    public static Spanned getHtmlString(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        else
            //noinspection deprecation
            return Html.fromHtml(source);
    }

    public static Spanned getLinkText(String source) {
        return getHtmlString("<span style=\"color:#1155cc\"> <u>" + source + " </u></span>");
    }

    public static Bitmap getImageBitmap(Context context, String link) {

        try {
            link = URLDecoder.decode(link, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(new ImageLoaderConfiguration.Builder(context)
                    .memoryCache(new WeakMemoryCache()).threadPoolSize(10)
                    .denyCacheImageMultipleSizesInMemory()
                    .imageDownloader(new BaseImageDownloader(context)).build());
            bm = imageLoader.loadImageSync(link);
        } catch (Exception | Error e) {
            e.printStackTrace();
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_launcher, options);
        }
        return bm;
    }
}
