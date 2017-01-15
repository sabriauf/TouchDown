package lk.rc07.ten_years.touchdown.activities;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.adapters.PageAdapter;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.config.Constant;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.MatchDAO;
import lk.rc07.ten_years.touchdown.data.PositionDAO;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
import lk.rc07.ten_years.touchdown.models.DownloadMeta;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Position;
import lk.rc07.ten_years.touchdown.models.Team;
import lk.rc07.ten_years.touchdown.utils.DownloadManager;
import lk.rc07.ten_years.touchdown.utils.JsonDateSerializer;
import lk.rc07.ten_years.touchdown.utils.PageBuilder;
import lk.rc07.ten_years.touchdown.utils.SynchronizeData;

public class MainActivity extends AppCompatActivity {

    //constants
    private final String SERVER_ERROR_MESSAGE = "Server error: Code - %d : Message - %s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PageAdapter adapter = new PageAdapter(
                getSupportFragmentManager(), new PageBuilder()
                .addPage("LIVE")
                .addPage("FIXTURE")
                .addPage("POINTS")
                .addPage("TEAM")
                .addPage("Bradby Express"));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpager_tab);
        viewPagerTab.setViewPager(viewPager);

        syncData();
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

        DownloadMeta meta = new DownloadMeta();
        meta.setUrl(AppConfig.SYNCHRONIZE_URL);
        meta.setRequestMethod(DownloadManager.GET_REQUEST);

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
            if (jsonObject.has(Constant.JSON_OBJECT_MATCH))
                saveMatches(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_MATCH).toString());

            if (jsonObject.has(Constant.JSON_OBJECT_TEAMS))
                saveTeams(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_TEAMS).toString());

            if (jsonObject.has(Constant.JSON_OBJECT_POSITIONS))
                savePositions(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_POSITIONS).toString());

        } catch (JSONException ex) {
            ex.printStackTrace();
        } finally {
            dbManager.closeDatabase();
        }
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
}
