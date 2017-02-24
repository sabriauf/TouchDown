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
import lk.rc07.ten_years.touchdown.models.Group;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Player;
import lk.rc07.ten_years.touchdown.models.PlayerPosition;
import lk.rc07.ten_years.touchdown.models.Points;
import lk.rc07.ten_years.touchdown.models.Position;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.models.Team;

/**
 * Created by Sabri on 2/24/2017. read sync json object and saves in db
 */

public class ReadSyncJson {

    private Context context;

    private long time;

    public ReadSyncJson(Context context, final String response, long time) {
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

            if (jsonObject.has(Constant.JSON_OBJECT_EXPRESS)) {
                SharedPreferences.Editor editor = context.getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE).edit();

                JSONObject object = jsonObject.getJSONObject(Constant.JSON_OBJECT_EXPRESS);
                editor.putString(Constant.PREFERENCES_EXPRESS_IMAGE, object.getString(Constant.JSON_OBJECT_EXPRESS_IMAGE));
                editor.putString(Constant.PREFERENCES_EXPRESS_LINK, object.getString(Constant.JSON_OBJECT_EXPRESS_REDIRECT));

                editor.apply();
            }

            if (jsonObject.has(Constant.JSON_OBJECT_ADVERTISEMENT)) {
                JSONObject object = jsonObject.getJSONObject(Constant.JSON_OBJECT_ADVERTISEMENT);
                String img_url = object.getString(Constant.JSON_OBJECT_ADVERTISEMENT_IMAGE);

                Intent intent = new Intent(context, AdvertisementActivity.class);
                intent.putExtra(AdvertisementActivity.EXTRAS_IMAGE_LINK, img_url);

                context.startActivity(intent);
            }

            if (jsonObject.has(Constant.JSON_OBJECT_VERSION)) {
                try {
                    JSONObject object = jsonObject.getJSONObject(Constant.JSON_OBJECT_VERSION);
                    int version = Integer.parseInt(object.getString(Constant.JSON_OBJECT_VERSION));

                    if (version > getAppVersion()) {
                        showUpdateAlert();
                    }
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbManager.closeDatabase();

            MainActivity.mHandler.sendEmptyMessage(MainActivity.REFRESH_TABS);
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
}
