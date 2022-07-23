package lk.rc07.ten_years.touchdown.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import lk.rc07.ten_years.touchdown.BuildConfig;
import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.adapters.PageAdapter;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.config.Constant;
import lk.rc07.ten_years.touchdown.fragments.BradbyExpressFragment;
import lk.rc07.ten_years.touchdown.fragments.FixtureFragment;
import lk.rc07.ten_years.touchdown.fragments.LiveFragment;
import lk.rc07.ten_years.touchdown.fragments.PlayersFragment;
import lk.rc07.ten_years.touchdown.fragments.StandingFragment;
import lk.rc07.ten_years.touchdown.utils.AppHandler;
import lk.rc07.ten_years.touchdown.utils.PageBuilder;

public class MainActivity extends AppCompatActivity {

    //constants
    private final String[] TAB_TITLES = {"LIVE", "FIXTURE", "POINTS", "TEAM", "BradEx"};
    public static final int REFRESH_TABS = 1001;
    public static final int LIVE_STREAMING = 1002;
    public static final int FORCE_SYNC = 1003;

    //instances
    private PageAdapter adapter;
    public static Handler mHandler;
    private SharedPreferences sharedPreferences;

    //views
    private TextView txt_tab_title = null;
    private ImageView img_live;
    public Spinner spnSelector;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");

        setTitle(TAB_TITLES[0]);

        spnSelector = findViewById(R.id.spn_selector_toolbar);
        viewPager = setTabView();

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == REFRESH_TABS) {
                    adapter.notifyDataSetChanged();
                } else if (message.what == LIVE_STREAMING) {
                    boolean isLive = (boolean) message.obj;
                    setLiveLink(isLive);
                } else if (message.what == FORCE_SYNC) {
                    syncData();
                }
                return false;
            }
        });

        String link = sharedPreferences.getString(Constant.PREFERENCES_LIVE_LINK, "");
        img_live = findViewById(R.id.live_icon);
        img_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = sharedPreferences.getString(Constant.PREFERENCES_LIVE_LINK, "");

                Intent i = new Intent(getApplicationContext(), PlayerActivity.class);
                i.putExtra(PlayerActivity.EXTRAS_VIDEO_ID, link);
                startActivity(i);
            }
        });
        setLiveLink(!link.equals(""));

        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            Toast.makeText(this, "Developer Build", Toast.LENGTH_LONG).show();
            FirebaseMessaging.getInstance().subscribeToTopic("test2");
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.TOPIC);
            FirebaseMessaging.getInstance().subscribeToTopic("Other_matches");
            FirebaseMessaging.getInstance().subscribeToTopic("General");
            FirebaseMessaging.getInstance().subscribeToTopic("settings");
        }

        syncData();
        readExtras(getIntent());

//        AppHandler.readData(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        readExtras(getIntent());
    }

    private void readExtras(Intent intent) {
        //on Push notification go to request tab
        if (intent.getExtras() != null && intent.getExtras().containsKey(Constant.EXTRA_FRAGMENT_ID)) {
            int fragmentId = intent.getExtras().getInt(Constant.EXTRA_FRAGMENT_ID);
            if (adapter.getCount() > fragmentId)
                viewPager.setCurrentItem(fragmentId, true);
        }
    }

    private void setLiveLink(boolean isLive) {
        if (isLive)
            img_live.setVisibility(View.VISIBLE);
        else
            img_live.setVisibility(View.GONE);
    }

    private void setTitle(String title) {
        if (txt_tab_title == null)
            txt_tab_title = findViewById(R.id.txt_tab_title);
        txt_tab_title.setText(title);
    }

    private ViewPager setTabView() {
        PageBuilder pageBuilder = new PageBuilder();
        for (String title : TAB_TITLES) {
            pageBuilder.addPage(title);
        }
        Fragment[] fragments = new Fragment[]{new LiveFragment(), new FixtureFragment(), new StandingFragment(),
                new PlayersFragment(), new BradbyExpressFragment()};
        pageBuilder.setFragments(fragments);
        adapter = new PageAdapter(
                getSupportFragmentManager(), pageBuilder);

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpager_tab);
        viewPagerTab.setViewPager(viewPager);

        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(TAB_TITLES[position]);
                img_live.setVisibility(View.GONE);
                spnSelector.setVisibility(View.GONE);
                switch (position) {
                    case 0:
                        setLiveLink(!sharedPreferences.getString(Constant.PREFERENCES_LIVE_LINK, "").equals(""));
                        spnSelector.setVisibility(View.GONE);
                        break;
                    case 1:
                        img_live.setVisibility(View.GONE);
                        spnSelector.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return viewPager;
    }

    private void setSpinner(final List<String> years) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> yearArray = new ArrayList<>();
                String lastYear = "";
                for (String year : years) {
                    if (!lastYear.equals(year)) {
                        lastYear = year;
                        yearArray.add(year);
                    }
                }

                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_spinner_item, yearArray);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spnSelector.setAdapter(dataAdapter);
                    }
                });
            }
        }).start();

    }

    private void syncData() {

        boolean isFirstSync = sharedPreferences.getBoolean(Constant.SHEARED_PREFEREANCE_SERVER_CHANGE_ACCEPTED, false);

        long time;
        if (!isFirstSync) {
            sharedPreferences.edit().putBoolean(Constant.SHEARED_PREFEREANCE_SERVER_CHANGE_ACCEPTED, true).apply();
            time = AppConfig.DEFAULT_TIME_STAMP;
        } else
            time = sharedPreferences.getLong(Constant.PREFERENCES_LAST_SYNC, AppConfig.DEFAULT_TIME_STAMP);

        setSpinner(AppHandler.callSync(this, time));
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
            case R.id.menu_sync:
                syncData();
                break;
            case R.id.menu_share:
                try {
                    Intent intent1 = new Intent();
                    intent1.setClassName("com.facebook.katana", "com.facebook.katana.activity.composer.ImplicitShareIntentHandler");
                    intent1.setAction("android.intent.action.SEND");
                    intent1.setType("text/plain");
                    intent1.putExtra("android.intent.extra.TEXT", AppConfig.APP_DOWNLOAD_LINK);
                    startActivity(intent1);
                } catch (ActivityNotFoundException e) {
                    // If we failed (not native FB app installed), try share through SEND
                    String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + AppConfig.APP_DOWNLOAD_LINK;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                    startActivity(intent);
                }
                break;
            case R.id.menu_profile:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
