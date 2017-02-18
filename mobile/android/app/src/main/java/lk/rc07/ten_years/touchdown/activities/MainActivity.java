package lk.rc07.ten_years.touchdown.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.adapters.PageAdapter;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.config.Constant;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.GroupDAO;
import lk.rc07.ten_years.touchdown.data.MatchDAO;
import lk.rc07.ten_years.touchdown.data.PlayerDAO;
import lk.rc07.ten_years.touchdown.data.PlayerPositionDAO;
import lk.rc07.ten_years.touchdown.data.PointsDAO;
import lk.rc07.ten_years.touchdown.data.PositionDAO;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
import lk.rc07.ten_years.touchdown.models.DownloadMeta;
import lk.rc07.ten_years.touchdown.models.Group;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Player;
import lk.rc07.ten_years.touchdown.models.PlayerPosition;
import lk.rc07.ten_years.touchdown.models.Points;
import lk.rc07.ten_years.touchdown.models.Position;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.models.Team;
import lk.rc07.ten_years.touchdown.utils.DownloadManager;
import lk.rc07.ten_years.touchdown.utils.JsonDateSerializer;
import lk.rc07.ten_years.touchdown.utils.PageBuilder;
import lk.rc07.ten_years.touchdown.utils.SynchronizeData;

public class MainActivity extends AppCompatActivity {

    //constants
    private final String SERVER_ERROR_MESSAGE = "Server error: Code - %d : Message - %s";
    private final String[] TAB_TITLES = {"LIVE", "FIXTURE", "POINTS", "TEAM", "Bradby Express"};

    //instances
    private PageAdapter adapter;

    //views
    private TextView txt_tab_title = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);

        setTitle(TAB_TITLES[0]);

        ViewPager viewPager = setTabView();

        //on Push notification go to request tab
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Constant.EXTRA_FRAGMENT_ID)) {
            int fragmentId = getIntent().getExtras().getInt(Constant.EXTRA_FRAGMENT_ID);
            if (adapter.getCount() > fragmentId)
                viewPager.setCurrentItem(fragmentId, true);
        }

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
        SynchronizeData syncData = new SynchronizeData(this);
        syncData.setOnDownloadListener(MainActivity.class.getSimpleName(), new SynchronizeData.DownloadListener() {
            @Override
            public void onDownloadSuccess(final String response, DownloadMeta meta, int code) {
                getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE).edit().
                        putLong(Constant.PREFERENCES_LAST_SYNC, System.currentTimeMillis()).apply();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveData(response);
                    }
                }).start();
            }

            @Override
            public void onDownloadFailed(String errorMessage, DownloadMeta meta, int code) {
                Log.e(MainActivity.class.getSimpleName(), String.format(SERVER_ERROR_MESSAGE, code, errorMessage));
            }
        });

        HashMap<String, String> urlParams = new HashMap<>();
        urlParams.put(Constant.PARAM_API_LAST_UPDATE, String.valueOf(getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE)
                .getLong(Constant.PREFERENCES_LAST_SYNC, AppConfig.DEFAULT_TIME_STAMP)));
//        urlParams.put(Constant.PARAM_API_LAST_UPDATE, String.valueOf(AppConfig.DEFAULT_TIME_STAMP));

        DownloadMeta meta = new DownloadMeta();
        meta.setUrl(AppConfig.SYNCHRONIZE_URL);
        meta.setRequestMethod(DownloadManager.GET_REQUEST);
        meta.setUrlParams(urlParams);

        syncData.execute(meta);
    }

    private void saveData(String response) {
        DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(this));
        dbManager.openDatabase();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDateSerializer("yyyy-MM-dd kk:mm"));
        Gson gson = gsonBuilder.create();

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has(Constant.JSON_OBJECT_GROUPS))
                saveGroups(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_GROUPS).toString());

            if (jsonObject.has(Constant.JSON_OBJECT_MATCH))
                saveMatches(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_MATCH).toString());

            if (jsonObject.has(Constant.JSON_OBJECT_TEAMS))
                saveTeams(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_TEAMS).toString());

            if (jsonObject.has(Constant.JSON_OBJECT_POSITIONS))
                savePositions(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_POSITIONS).toString());

            if (jsonObject.has(Constant.JSON_OBJECT_POINTS))
                savePoints(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_POINTS).toString());

            if (jsonObject.has(Constant.JSON_OBJECT_PLAYERS))
                savePlayers(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_PLAYERS).toString());

            if (jsonObject.has(Constant.JSON_OBJECT_PLAYER_POS))
                savePlayerPos(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_PLAYER_POS).toString());

            if(jsonObject.has(Constant.JSON_OBJECT_SCORES))
                saveScores(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_SCORES).toString());

            if (jsonObject.has(Constant.JSON_OBJECT_EXPRESS)) {
                SharedPreferences.Editor editor = getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE).edit();

                JSONObject object = jsonObject.getJSONObject(Constant.JSON_OBJECT_EXPRESS);
                editor.putString(Constant.PREFERENCES_EXPRESS_IMAGE, object.getString(Constant.JSON_OBJECT_EXPRESS_IMAGE));
                editor.putString(Constant.PREFERENCES_EXPRESS_LINK, object.getString(Constant.JSON_OBJECT_EXPRESS_REDIRECT));

                editor.apply();
            }


        } catch (JSONException ex) {
            ex.printStackTrace();
        } finally {
            dbManager.closeDatabase();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void saveGroups(Gson gson, String response) {
        Type messageType = new TypeToken<List<Group>>() {
        }.getType();

        List<Group> groups = gson.fromJson(response, messageType);

        for (Group group : groups)
            GroupDAO.addGroup(group);
    }

    private void saveMatches(Gson gson, String response) {
        Type messageType = new TypeToken<List<Match>>() {
        }.getType();

        List<Match> matches = gson.fromJson(response, messageType);

        for (Match match : matches)
            MatchDAO.addMatch(match);
    }

    private void saveTeams(Gson gson, String response) {
        Type messageType = new TypeToken<List<Team>>() {
        }.getType();

        List<Team> teams = gson.fromJson(response, messageType);

        for (Team team : teams)
            TeamDAO.addTeam(team);
    }

    private void savePositions(Gson gson, String response) {
        Type messageType = new TypeToken<List<Position>>() {
        }.getType();

        List<Position> positions = gson.fromJson(response, messageType);

        for (Position position : positions)
            PositionDAO.addPosition(position);
    }

    private void savePoints(Gson gson, String response) {
        Type messageType = new TypeToken<List<Points>>() {
        }.getType();

        List<Points> points = gson.fromJson(response, messageType);

        for (Points point : points)
            PointsDAO.addPoints(point);
    }

    private void savePlayers(Gson gson, String response) {
        Type messageType = new TypeToken<List<Player>>() {
        }.getType();

        List<Player> players = gson.fromJson(response, messageType);

        for (Player player : players)
            PlayerDAO.addPlayer(player);
    }

    private void savePlayerPos(Gson gson, String response) {
        Type messageType = new TypeToken<List<PlayerPosition>>() {
        }.getType();

        List<PlayerPosition> players = gson.fromJson(response, messageType);

        for (PlayerPosition player : players)
            PlayerPositionDAO.addPlayerPosition(player);
    }

    private void saveScores(Gson gson, String response) {
        Type messageType = new TypeToken<List<Score>>() {
        }.getType();

        List<Score> scores = gson.fromJson(response, messageType);

        for (Score score : scores)
            ScoreDAO.addScore(score);
    }
}
