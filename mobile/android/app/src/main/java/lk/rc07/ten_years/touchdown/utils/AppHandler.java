package lk.rc07.ten_years.touchdown.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import lk.rc07.ten_years.touchdown.BuildConfig;
import lk.rc07.ten_years.touchdown.activities.MainActivity;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.config.Constant;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.MatchDAO;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
import lk.rc07.ten_years.touchdown.models.DownloadMeta;
import lk.rc07.ten_years.touchdown.models.GameTime;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Score;

/**
 * Created by Sabri on 1/13/2017. application general methods
 */

public class AppHandler {

    private static final String SHARE_STRING_PENDING = "%s vs %s match on %s at %s";
    private static final String SHARE_STRING_PROGRESS = "%s leading by %d points against %s, score %d-%d at %s";
    private static final String SHARE_STRING_PROGRESS_EAQUAL = "%s vs %s, score %d-%d at %s";
    private static final String SHARE_STRING_ENDED = "%s won by %d points against %s, final score %d-%d";
    private static final String SHARE_STRING_ENDED_DRAW = "%s vs %s match drawn, final score %d-%d";

    private static HashMap<String, String> getHeaders(Context context) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(Constant.PARAM_AUTH_KEY, AppConfig.APPLICATION_AUTHENTICATION_KEY);
        headers.put(Constant.PARAM_PLATFORM, String.valueOf(Constant.PLATFORM_ANDROID));
//        headers.put(Constant.PARAM_AUTH_DEVICE, Settings.Secure.getString(context.getContentResolver(),
//                Settings.Secure.ANDROID_ID));
        headers.put(Constant.PARAM_AUTH_PACKAGE, context.getApplicationContext().getPackageName());
        headers.put(Constant.PARAM_AUTH_VERSION, String.valueOf(BuildConfig.VERSION_CODE));
        headers.put(Constant.PARAM_API_VERSION, String.valueOf(AppConfig.API_VERSION));

        return headers;
    }

//    public static DisplayImageOptions getImageOption(ImageLoader imageLoader, Context context, int resource) {
//        @SuppressWarnings("deprecation") DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnFail(resource)
//                .cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565)
//                .displayer(new SimpleBitmapDisplayer()).build();
//        if (!imageLoader.isInited()) {
//            ImageLoaderConfiguration imageConfig = new ImageLoaderConfiguration.Builder(context)
//                    .memoryCache(new WeakMemoryCache()).diskCacheExtraOptions(480, 320, null).threadPoolSize(5)
//                    .denyCacheImageMultipleSizesInMemory().build();
//            imageLoader.init(imageConfig);
//        }
//        return options;
//    }
//
//    public static DisplayImageOptions getImageOptionBestQuality() {
//        return new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.NONE).bitmapConfig(Bitmap.Config.ARGB_8888).build();
//    }

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

        Bitmap bm = null;
        FutureTarget<Bitmap> futureBitmap = Glide.with(context)
                .asBitmap()
                .load(link)
                .submit();
        try {
            bm = futureBitmap.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bm;
    }

    public static String getResultString(Context context, Match match) {
        String homeTeam;
        String opponentTeam;
//        String time = TimeFormatter.millisToGameTime(getContext(), matchStartTime);
        if (match.getTeamOne() == AppConfig.HOME_TEAM_ID) {
            opponentTeam = TeamDAO.getTeam(match.getTeamTwo()).getName();
            homeTeam = TeamDAO.getTeam(match.getTeamOne()).getName();
        } else {
            opponentTeam = TeamDAO.getTeam(match.getTeamOne()).getName();
            homeTeam = TeamDAO.getTeam(match.getTeamTwo()).getName();
        }

        int leftScoreTotal = 0;
        int rightScoreTotal = 0;
        List<Score> scores = ScoreDAO.getScores(match.getIdmatch());
        for (Score score : scores) {
            if (score.getTeamId() == match.getTeamOne())
                leftScoreTotal += score.getScore();
            else
                rightScoreTotal += score.getScore();
        }

        switch (match.getStatus()) {
            case PENDING:
                Date date = new Date(match.getMatchDate());
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
                String day = dateFormat.format(date);
                dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                String time = dateFormat.format(date);
                return String.format(Locale.getDefault(), SHARE_STRING_PENDING, homeTeam,
                        opponentTeam, day, time);
            case FULL_TIME:
            case DONE:
                if (leftScoreTotal == rightScoreTotal) {
                    return String.format(Locale.getDefault(), SHARE_STRING_ENDED_DRAW, homeTeam,
                            opponentTeam, leftScoreTotal, rightScoreTotal);
                } else if (leftScoreTotal > rightScoreTotal) {
                    return String.format(Locale.getDefault(), SHARE_STRING_ENDED, homeTeam,
                            (leftScoreTotal - rightScoreTotal), opponentTeam, leftScoreTotal, rightScoreTotal);
                } else {
                    return String.format(Locale.getDefault(), SHARE_STRING_ENDED, opponentTeam,
                            (rightScoreTotal - leftScoreTotal), homeTeam, rightScoreTotal, leftScoreTotal);
                }
            case FIRST_HALF:
            case SECOND_HALF:
                return progressReport(homeTeam, opponentTeam, TimeFormatter.millisToGameTime(context,
                        AppHandler.getMatchStartTime(match)), leftScoreTotal, rightScoreTotal);

            case HALF_TIME:
                return progressReport(homeTeam, opponentTeam, match.getStatus().toStringValue(), leftScoreTotal, rightScoreTotal);
        }

        return "";
    }

    private static String progressReport(String homeTeam, String opponentTeam, String time, int leftScoreTotal, int rightScoreTotal) {
        if (leftScoreTotal == rightScoreTotal) {
            return String.format(Locale.getDefault(), SHARE_STRING_PROGRESS_EAQUAL, homeTeam,
                    opponentTeam, leftScoreTotal, rightScoreTotal, time);
        } else if (leftScoreTotal > rightScoreTotal) {
            return String.format(Locale.getDefault(), SHARE_STRING_PROGRESS, homeTeam,
                    (leftScoreTotal - rightScoreTotal), opponentTeam, leftScoreTotal, rightScoreTotal,
                    time);
        } else {
            return String.format(Locale.getDefault(), SHARE_STRING_PROGRESS, opponentTeam,
                    (rightScoreTotal - leftScoreTotal), homeTeam, rightScoreTotal, leftScoreTotal,
                    time);
        }
    }

    public static long getMatchStartTime(Match match) {
        List<Score> startScores = ScoreDAO.getActionScore(match.getIdmatch(), Score.Action.SECOND_HALF);
        if (startScores.size() > 0)
            return (startScores.get(0).getTime() - AppConfig.SECOND_HALF_START_TIME);
        else {
            startScores = ScoreDAO.getActionScore(match.getIdmatch(), Score.Action.START);
            if (startScores.size() > 0)
                return startScores.get(0).getTime();
        }
        return 0;
    }

    public static SortedMap<Long, GameTime> getMatchGameTimes(int matchId) {
        SortedMap<Long, GameTime> times = new TreeMap<>();
        List<Score> timeScores = ScoreDAO.getAllGameTimes(matchId);
        long point = 0L;
        long prevTime = 0L;
        boolean prevStart = false;
        for (Score time : timeScores) {
            if (time.getAction() == Score.Action.SECOND_HALF) {
                point = AppConfig.SECOND_HALF_START_TIME;
                prevTime = time.getTime();
            }

            GameTime gameTime = createGameTime(time, prevTime, point, prevStart);
            times.put(time.getTime(), gameTime);
            prevTime = time.getTime();
            point = gameTime.getRelativeTime();
            prevStart = gameTime.getType() == 1;
        }
        return times;
    }

    private static GameTime createGameTime(Score score, long prevTime, long point, boolean prevStart) {

        boolean start = score.getAction() == Score.Action.START ||
                score.getAction() == Score.Action.SECOND_HALF ||
                score.getAction() == Score.Action.GAME_RESTART;

        GameTime gameTime = new GameTime();
        gameTime.setScoreId(score.getIdscore());
        gameTime.setName(score.getActionString());
        gameTime.setRealTime(score.getTime());
//        if(point == 0)
//            point = score.getTime();
        gameTime.setRelativeTime((prevStart ? (score.getTime() - prevTime) : 0) + point);
        gameTime.setType(start ? 1 : 0);
        return gameTime;
    }

    public static void addGameTimeObj(SortedMap<Long, GameTime> times, Score score) {
        GameTime gameTime;
        long prevTime = 0;
        long point = 0;
        boolean prevStart = false;
        if (times == null) {
            times = new TreeMap<>();
        } else if (times.size() > 0) {
            GameTime lastGameTime = times.get(times.lastKey());
            if (lastGameTime != null) {
                prevTime = lastGameTime.getRealTime();
                point = lastGameTime.getRelativeTime();
                prevStart = lastGameTime.getType() == 1;
            }
        }

        gameTime = AppHandler.createGameTime(score, prevTime, point, prevStart);
        times.put(gameTime.getRealTime(), gameTime);
    }

    public static void removeGameTimeObj(SortedMap<Long, GameTime> times, Score score) {
        for (long time : times.keySet()) {
            GameTime gameTime = times.get(time);
            if (gameTime != null) {
                if (gameTime.getScoreId() == score.getIdscore()) {
                    times.remove(time);
                    break;
                }
            }
        }
    }

    public static List<String> callSync(final Context context, final long time) {
        SynchronizeData syncData = new SynchronizeData(context);
        syncData.setOnDownloadListener(MainActivity.class.getSimpleName(), new SynchronizeData.DownloadListener() {
            @Override
            public void onDownloadSuccess(final String response, DownloadMeta meta, int code) {
                context.getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE).edit()
                        .putLong(Constant.PREFERENCES_LAST_SYNC, System.currentTimeMillis()).apply();
                new ReadSyncJson(context, response, time);
            }

            @Override
            public void onDownloadFailed(String errorMessage, DownloadMeta meta, int code) {
                Log.e(MainActivity.class.getSimpleName(), String.format(Constant.SERVER_ERROR_MESSAGE, code, errorMessage));
            }
        });

        HashMap<String, String> urlParams = new HashMap<>();
        DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(context));
        dbManager.openDatabase();
        if (MatchDAO.getAllMatches().size() > 0) {
            urlParams.put(Constant.PARAM_API_LAST_UPDATE, String.valueOf(time));
        } else {
            urlParams.put(Constant.PARAM_API_LAST_UPDATE, String.valueOf(AppConfig.DEFAULT_TIME_STAMP));
        }
        List<String> years = MatchDAO.getYears();
        dbManager.closeDatabase();

        DownloadMeta meta = new DownloadMeta();
        meta.setUrl(BuildConfig.DEFAULT_URL + AppConfig.SYNCHRONIZE_URL);
        meta.setRequestMethod(DownloadManager.GET_REQUEST);
        meta.setUrlParams(urlParams);
        meta.setHeaders(AppHandler.getHeaders(context));

        syncData.execute(meta);

        return years;
    }

//    private static boolean requestPermission(Activity context) {
//        boolean hasPermission = (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
//        if (!hasPermission) {
//            ActivityCompat.requestPermissions(context,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    101);
//            return false;
//        }
//        return true;
//    }

//    public static void readData(final Activity activity) {
//
//        if(!requestPermission(activity))
//            return;
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(activity));
//                dbManager.openDatabase();
//
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("groups", getListFromList(GroupDAO.getAllGroups()));
//                    jsonObject.put("matches", getListFromList(MatchDAO.getAllMatches()));
//                    jsonObject.put("players", getListFromList(PlayerDAO.getAllPlayer()));
//                    jsonObject.put("playerPosition", getListFromList(PlayerPositionDAO.getAllPositions()));
//                    jsonObject.put("points", getListFromList(PointsDAO.getAllPointTable()));
//                    jsonObject.put("positions", getListFromList(PositionDAO.getAllPositions()));
//                    jsonObject.put("scores", getListFromList(ScoreDAO.getAllScores()));
//                    jsonObject.put("staffs", getListFromList(StaffDAO.getAll()));
//                    jsonObject.put("teams", getListFromList(TeamDAO.getAllTeams()));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                writeOnFile("database_backup.txt", jsonObject.toString());
//
//                dbManager.closeDatabase();
//
//            }
//        }).start();
//    }

//    private static JSONArray getListFromList(List<?> list) {
//        try {
//            String jsonString = new Gson().toJson(list);
//            return new JSONArray(jsonString);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return new JSONArray();
//    }
//
//    private static void writeOnFile(String fileName, String data) {
//
//        File root = android.os.Environment.getExternalStorageDirectory();
//
//
//        File dir = new File(root.getAbsolutePath() + "/download");
//        dir.mkdirs();
//        File file = new File(dir, fileName);
//
//        try {
//            FileOutputStream f = new FileOutputStream(file);
//            PrintWriter pw = new PrintWriter(f);
//            pw.println(data);
//            pw.flush();
//            pw.close();
//            f.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
