package lk.rc07.ten_years.touchdown.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.HashMap;

import lk.rc07.ten_years.touchdown.BuildConfig;
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

    public static DisplayImageOptions getImageOption(ImageLoader imageLoader, Context context) {
        @SuppressWarnings("deprecation") DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new SimpleBitmapDisplayer()).build();
        if (!imageLoader.isInited()) {
            ImageLoaderConfiguration imageConfig = new ImageLoaderConfiguration.Builder(context).memoryCache(new WeakMemoryCache()).threadPoolSize(10).denyCacheImageMultipleSizesInMemory().build();
            imageLoader.init(imageConfig);
        }
        return options;
    }
}
