package lk.rc07.ten_years.touchdown.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import lk.rc07.ten_years.touchdown.activities.AdvertisementActivity;
import lk.rc07.ten_years.touchdown.activities.MainActivity;
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
import lk.rc07.ten_years.touchdown.fragments.PlayerTeamDAO;
import lk.rc07.ten_years.touchdown.models.Group;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Player;
import lk.rc07.ten_years.touchdown.models.PlayerPosition;
import lk.rc07.ten_years.touchdown.models.PlayerTeam;
import lk.rc07.ten_years.touchdown.models.Points;
import lk.rc07.ten_years.touchdown.models.Position;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.models.Team;

/**
 * Created by Sabri on 2/24/2017. read sync json object and saves in db
 */

class ReadSyncJson {

    private Context context;

    private long time;

    ReadSyncJson(Context context, final String response, long time) {
        this.context = context;
        this.time = time;

        new Thread(new Runnable() {
            @Override
            public void run() {
                saveData(response);
            }
        }).start();
    }

    private void saveData(String response) {
        DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(context));
        dbManager.openDatabase();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDateSerializer("MM/dd/yyyy kk:mm"));
        Gson gson = gsonBuilder.create();

        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has(Constant.JSON_OBJECT_META)) {
                if (!readMetaData(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_META).toString())) {
                    dbManager.closeDatabase();
                    AppHandler.callSync(context, AppConfig.DEFAULT_TIME_STAMP);
                    return;
                }
            }

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

            if (jsonObject.has(Constant.JSON_OBJECT_SCORES))
                saveScores(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_SCORES).toString());

            if (jsonObject.has(Constant.JSON_OBJECT_PLAYER_TEAM)) {
                savePlayerTeam(gson, jsonObject.getJSONArray(Constant.JSON_OBJECT_PLAYER_TEAM).toString());
            }

//            if (jsonObject.has(Constant.JSON_OBJECT_ADVERTISEMENT)) {
//                JSONObject object = jsonObject.getJSONObject(Constant.JSON_OBJECT_ADVERTISEMENT);
//                String img_url = object.getString(Constant.JSON_OBJECT_ADVERTISEMENT_IMAGE);
//
//
//            }


            MainActivity.mHandler.sendEmptyMessage(MainActivity.REFRESH_TABS);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbManager.closeDatabase();
        }
    }

    private int getAppVersion() {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get app version: " + e);
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

        ScoreDAO.deleteScore(time);

        for (Score score : scores)
            ScoreDAO.addScore(score);
    }

    private void savePlayerTeam(Gson gson, String response) {
        Type messageType = new TypeToken<List<PlayerTeam>>() {
        }.getType();

        List<PlayerTeam> playerTeam = gson.fromJson(response, messageType);

        for (PlayerTeam pTeam : playerTeam)
            PlayerTeamDAO.addPlayerTeam(pTeam);
    }

    private boolean readMetaData(Gson gson, String response) {
        boolean continueSync = true;
        Type messageType = new TypeToken<List<MetaData>>() {
        }.getType();

        List<MetaData> dataList = gson.fromJson(response, messageType);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE);

        for (MetaData data : dataList)
            switch (data.getMeta_key()) {
                case Constant.JSON_OBJECT_EXPRESS_IMAGE:
                    savePreferences(Constant.PREFERENCES_EXPRESS_IMAGE, data.getMeta_value());
                    break;
                case Constant.JSON_OBJECT_EXPRESS_REDIRECT:
                    savePreferences(Constant.PREFERENCES_EXPRESS_LINK, data.getMeta_value());
                    break;
                case Constant.JSON_OBJECT_VERSION:
                    checkVersion(data.getMeta_value());
                    break;
                case Constant.JSON_OBJECT_POINTS_UPDATED:
                    try {
                        long date = Long.parseLong(data.getMeta_value());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong(Constant.JSON_OBJECT_POINTS_UPDATED, date);
                        editor.apply();
                    } catch (ClassCastException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case Constant.JSON_OBJECT_RESET_DATA:
                    long date = Long.parseLong(data.getMeta_value());
                    if (sharedPreferences.getLong(Constant.SHEARED_PREFEREANCE_LAST_REST, System.currentTimeMillis()) < date) {
                        sharedPreferences.edit().putLong(Constant.SHEARED_PREFEREANCE_LAST_REST, date).apply();
                        DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(context));
                        dbManager.openDatabase();
                        clearData();
                        dbManager.closeDatabase();
                        continueSync = false;
                    }

                case Constant.JSON_OBJECT_ADVERTISEMENT:
                    if (!data.getMeta_value().equals(""))
                        showAdvertisement(data.getMeta_value());
                    break;
            }
        return continueSync;
    }

    private void clearData() {
        PointsDAO.deleteAll();
        PlayerPositionDAO.deleteAll();
        PositionDAO.deleteAll();
        PlayerDAO.deleteAll();
        ScoreDAO.deleteAll();
        TeamDAO.deleteAll();
        MatchDAO.deleteAll();
        GroupDAO.deleteAll();
        PlayerTeamDAO.deleteAll();
    }

    private void savePreferences(String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void showAdvertisement(String img_url) {
        Intent intent = new Intent(context, AdvertisementActivity.class);
        intent.putExtra(AdvertisementActivity.EXTRAS_IMAGE_LINK, img_url);

        context.startActivity(intent);
    }

    private void checkVersion(String currentVersion) {
        try {
            int version = Integer.parseInt(currentVersion);

            if (version > getAppVersion()) {
                showUpdateAlert();
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
    }


    private void showUpdateAlert() {

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Update Available!");
                builder.setMessage("Please update your app to the newest version for the better experience.");
                builder.setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=lk.rc07.ten_years.touchdown"));
                        context.startActivity(intent);
                    }
                });

                builder.create().show();
            }
        });
    }

    private class MetaData {
        int id;
        String meta_key;
        String meta_value;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        String getMeta_key() {
            return meta_key;
        }

        String getMeta_value() {
            return meta_value;
        }
    }
}
