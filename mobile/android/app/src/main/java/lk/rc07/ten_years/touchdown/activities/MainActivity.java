package lk.rc07.ten_years.touchdown.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.HashMap;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.adapters.PageAdapter;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.config.Constant;
import lk.rc07.ten_years.touchdown.models.DownloadMeta;
import lk.rc07.ten_years.touchdown.utils.DownloadManager;
import lk.rc07.ten_years.touchdown.utils.PageBuilder;
import lk.rc07.ten_years.touchdown.utils.ReadSyncJson;
import lk.rc07.ten_years.touchdown.utils.SynchronizeData;

public class MainActivity extends AppCompatActivity {

    //constants
    private final String SERVER_ERROR_MESSAGE = "Server error: Code - %d : Message - %s";
    private final String[] TAB_TITLES = {"LIVE", "FIXTURE", "POINTS", "TEAM", "Bradby Express"};
    public static final int REFRESH_TABS = 1001;
    public static final int LIVE_STREAMING = 1002;

    //instances
    private PageAdapter adapter;
    public static Handler mHandler;

    //views
    private TextView txt_tab_title = null;
    private ImageView img_live;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");

        setTitle(TAB_TITLES[0]);

        ViewPager viewPager = setTabView();

        //on Push notification go to request tab
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Constant.EXTRA_FRAGMENT_ID)) {
            int fragmentId = getIntent().getExtras().getInt(Constant.EXTRA_FRAGMENT_ID);
            if (adapter.getCount() > fragmentId)
                viewPager.setCurrentItem(fragmentId, true);
        }

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == REFRESH_TABS) {
                    adapter.notifyDataSetChanged();
                } else if (message.what == LIVE_STREAMING) {
                    if (!(boolean) message.obj)
                        img_live.setVisibility(View.GONE);
                    else
                        img_live.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        String link = getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE).getString(Constant.PREFERENCES_LIVE_LINK, "");
        img_live = (ImageView) findViewById(R.id.live_icon);
        img_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE).getString(Constant.PREFERENCES_LIVE_LINK, "");

                Intent i = new Intent(getApplicationContext(), PlayerActivity.class);
                i.putExtra(PlayerActivity.EXTRAS_VIDEO_ID, link);
                startActivity(i);
            }
        });
        if (link.equals(""))
            img_live.setVisibility(View.GONE);
        else
            img_live.setVisibility(View.VISIBLE);

        syncData();
    }

    private void setTitle(String title) {
        if (txt_tab_title == null)
            txt_tab_title = (TextView) findViewById(R.id.txt_tab_title);
        txt_tab_title.setText(title);
    }

    private ViewPager setTabView() {
        PageBuilder pageBuilder = new PageBuilder();
        for (String title : TAB_TITLES) {
            pageBuilder.addPage(title);
        }
        adapter = new PageAdapter(
                getSupportFragmentManager(), pageBuilder);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpager_tab);
        viewPagerTab.setViewPager(viewPager);

        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(TAB_TITLES[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return viewPager;
    }

    private void syncData() {
        final long time = getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE)
                .getLong(Constant.PREFERENCES_LAST_SYNC, AppConfig.DEFAULT_TIME_STAMP);

        SynchronizeData syncData = new SynchronizeData(this);
        syncData.setOnDownloadListener(MainActivity.class.getSimpleName(), new SynchronizeData.DownloadListener() {
            @Override
            public void onDownloadSuccess(final String response, DownloadMeta meta, int code) {
                getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE).edit().
                        putLong(Constant.PREFERENCES_LAST_SYNC, System.currentTimeMillis()).apply();
                new ReadSyncJson(MainActivity.this, response, time);
            }

            @Override
            public void onDownloadFailed(String errorMessage, DownloadMeta meta, int code) {
                Log.e(MainActivity.class.getSimpleName(), String.format(SERVER_ERROR_MESSAGE, code, errorMessage));
            }
        });

        HashMap<String, String> urlParams = new HashMap<>();
        urlParams.put(Constant.PARAM_API_LAST_UPDATE, String.valueOf(time));
//        urlParams.put(Constant.PARAM_API_LAST_UPDATE, String.valueOf(AppConfig.DEFAULT_TIME_STAMP));

        DownloadMeta meta = new DownloadMeta();
        meta.setUrl(AppConfig.SYNCHRONIZE_URL);
        meta.setRequestMethod(DownloadManager.GET_REQUEST);
        meta.setUrlParams(urlParams);

        syncData.execute(meta);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_about:
                Intent i2 = new Intent(getApplicationContext(), AboutUsActivity.class);
                startActivity(i2);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
