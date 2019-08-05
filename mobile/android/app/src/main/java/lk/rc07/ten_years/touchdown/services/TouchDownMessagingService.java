package lk.rc07.ten_years.touchdown.services;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.activities.MainActivity;
import lk.rc07.ten_years.touchdown.config.Constant;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.MatchDAO;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.utils.AppHandler;
import lk.rc07.ten_years.touchdown.utils.ScoreObserver;

/**
 * Created by Sabri on 12/13/2016. handling fcm
 */

public class TouchDownMessagingService extends FirebaseMessagingService {

    //constants
    private static final String TAG = TouchDownMessagingService.class.getSimpleName();
    private static final String PARAM_PUSH_TITLE = "title";
    private static final String PARAM_PUSH_MESSAGE = "message";
    private static final String PARAM_PUSH_FRAGMENT = "fragment";
    private static final String PARAM_PUSH_OBJECT = "object";
    private static final String PARAM_PUSH_SUMMARY = "summary";
    private static final String PARAM_PUSH_DESCRIPTION = "description";
    private static final String PARAM_PUSH_COVER = "cover";
    private static final String PARAM_PUSH_THUMB = "thumb";
    private static final String PARAM_PUSH_ID = "id";
    private static final String PARAM_PUSH_ACTIVITY = "activity";
    private static final String PARAM_PUSH_URL = "url";
    private static final String PARAM_OBJECT_SCORE = "score";
    private static final String PARAM_OBJECT_LIVE = "live";
    private static final String PARAM_LIVE_LINK = "link";
    private static final String PARAM_PUSH_SHOW = "show";
    private static final String PARAM_SYNC_VALUE = "sync";

    //instances
    private Gson gson = null;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (!remoteMessage.getData().containsKey(PARAM_PUSH_TITLE)) {
                String title = remoteMessage.getData().get(PARAM_PUSH_TITLE);
                if (title != null && title.equals(PARAM_SYNC_VALUE)) {
                    MainActivity.mHandler.sendEmptyMessage(MainActivity.FORCE_SYNC);
                    return;
                }
            }

            NotificationData data = new NotificationData(remoteMessage);

            String value = data.object;
            if (value != null && !value.equals("")) {
                try {
                    JSONObject respond = new JSONObject(value);
                    if (respond.has(PARAM_OBJECT_SCORE)) {
                        try {
                            if (gson == null)
                                gson = new GsonBuilder().create();
                            Score score = gson.fromJson(respond.getJSONObject(PARAM_OBJECT_SCORE).toString(), Score.class);

                            DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(this));
                            dbManager.openDatabase();

                            Message msg = new Message();
                            msg.obj = score;
                            if (score.getAction() != null) {
                                boolean inserted = ScoreDAO.addScore(score);

                                if (inserted) {
                                    msg.what = ScoreObserver.WHAT_NEW_SCORE;
                                    if (data.showNotification)
                                        sendNotification(data, remoteMessage.getFrom(), getString(R.string.notification_score));
                                } else
                                    msg.what = ScoreObserver.WHAT_UPDATE_SCORE;
                            } else {
                                if (score.getIdscore() == 0) {
                                    ScoreDAO.deleteAllScores(score.getMatchid());
                                    MatchDAO.updateMatchStatus(score.getMatchid(), Match.Status.PENDING);
                                    msg.what = ScoreObserver.WHAT_REMOVE_MATCH;
                                } else {
                                    ScoreDAO.deleteScore(score.getIdscore());
                                    msg.what = ScoreObserver.WHAT_REMOVE_SCORE;
                                }

                            }

                            dbManager.closeDatabase();
                            ScoreObserver.handler.sendMessage(msg);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else if (respond.has(PARAM_OBJECT_LIVE)) {
                        SharedPreferences.Editor editor = getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE).edit();

                        JSONObject object = respond.getJSONObject(PARAM_OBJECT_LIVE);
                        String link = object.getString(PARAM_LIVE_LINK);
                        Message msg = new Message();
                        if (!link.equals("") && link.contains("=")) {
                            link = link.split("=")[1];
                            msg.obj = true;
                        } else
                            msg.obj = false;
                        editor.putString(Constant.PREFERENCES_LIVE_LINK, link);
//                        if (data.showNotification)
//                            sendNotification(data, remoteMessage.getFrom(), getString(R.string.notification_live));

                        editor.apply();
                        msg.what = MainActivity.LIVE_STREAMING;
                        MainActivity.mHandler.sendMessage(msg);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (data.showNotification) {
                sendNotification(data, remoteMessage.getFrom(), getString(R.string.notification_messages));
            }
        }
        if (remoteMessage.getNotification() != null) {
            try {
                Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
                NotificationData data = new NotificationData(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), 0);
                sendNotification(data, remoteMessage.getFrom(), getString(R.string.notification_messages));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param data FCM message body received.
     */
    private void sendNotification(NotificationData data, String topic, String name) {
        Intent intent = createIntent(this, data.activity, data.url);

        intent.putExtra(Constant.EXTRA_FRAGMENT_ID, data.fragment);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(this, topic, name);
            notificationBuilder = new NotificationCompat.Builder(this, topic);
        } else
            //noinspection deprecation
            notificationBuilder = new NotificationCompat.Builder(this);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher).setSound(defaultSoundUri);

        if (data.thumb != null && !data.thumb.equals(""))
            notificationBuilder.setLargeIcon(AppHandler.getImageBitmap(this, data.thumb));

        setDefaultSmallView(notificationBuilder, data.title, data.message);
        if ((data.desc != null && !data.desc.equals("")) || (data.cover != null && !data.cover.equals("")))
            setDefaultBigView(this, notificationBuilder, data);


        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationBuilder.setColor(getColor(R.color.colorAccent));
        }
        notificationManager.notify(data.id, notificationBuilder.build());
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel(Context context, String channelId, String name) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("");

                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void setDefaultSmallView(NotificationCompat.Builder notificationBuilder, String title, String message) {
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(message);
    }

    private void setDefaultBigView(Context context, NotificationCompat.Builder notificationBuilder, NotificationData data) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN &&
                data.cover != null && !data.cover.equals("")) {
            NotificationCompat.BigPictureStyle bigStyle = new NotificationCompat.BigPictureStyle();
            bigStyle.bigPicture(AppHandler.getImageBitmap(context, data.cover));

            if (data.title != null)
                bigStyle.setBigContentTitle(data.title);

            if (data.summary != null)
                bigStyle.setSummaryText(data.summary);

            if (data.thumb != null)
                bigStyle.bigLargeIcon(AppHandler.getImageBitmap(context, data.thumb));

            notificationBuilder.setStyle(bigStyle);
        } else {
            NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
            bigStyle.bigText(data.desc);

            if (data.title != null)
                bigStyle.setBigContentTitle(data.title);

            if (data.thumb != null)
                notificationBuilder.setLargeIcon(AppHandler.getImageBitmap(context, data.thumb));

            notificationBuilder.setStyle(bigStyle);
        }
    }

    private class NotificationData {
        int id = 0;
        String title = "";
        String message = "";
        String summary;
        String activity = "";
        String desc;
        String thumb;
        String cover;
        String object = "";
        String url = "";
        boolean showNotification = true;
        int fragment = 0;

        NotificationData(String title, String message, int id) {
            this.title = title;
            this.message = message;
            this.id = id;
        }

        NotificationData(RemoteMessage remoteMessage) {
            try {
                if (remoteMessage.getData().containsKey(PARAM_PUSH_SHOW) && remoteMessage.getData().get(PARAM_PUSH_SHOW).equals("false")) {
                    showNotification = false;
                }

                object = remoteMessage.getData().get(PARAM_PUSH_OBJECT);

                if (remoteMessage.getData().containsKey(PARAM_PUSH_TITLE))
                    title = remoteMessage.getData().get(PARAM_PUSH_TITLE);

                if (remoteMessage.getData().containsKey(PARAM_PUSH_FRAGMENT))
                    fragment = Integer.parseInt(remoteMessage.getData().get(PARAM_PUSH_FRAGMENT));

                if (remoteMessage.getData().containsKey(PARAM_PUSH_MESSAGE))
                    message = remoteMessage.getData().get(PARAM_PUSH_MESSAGE);

                if (remoteMessage.getData().containsKey(PARAM_PUSH_SUMMARY))
                    summary = remoteMessage.getData().get(PARAM_PUSH_SUMMARY);

                if (remoteMessage.getData().containsKey(PARAM_PUSH_DESCRIPTION))
                    desc = remoteMessage.getData().get(PARAM_PUSH_DESCRIPTION);

                if (remoteMessage.getData().containsKey(PARAM_PUSH_COVER))
                    cover = remoteMessage.getData().get(PARAM_PUSH_COVER);

                if (remoteMessage.getData().containsKey(PARAM_PUSH_THUMB))
                    thumb = remoteMessage.getData().get(PARAM_PUSH_THUMB);

                if (remoteMessage.getData().containsKey(PARAM_PUSH_ID))
                    id = Integer.parseInt(remoteMessage.getData().get(PARAM_PUSH_ID));

                if (remoteMessage.getData().containsKey(PARAM_PUSH_ACTIVITY))
                    activity = remoteMessage.getData().get(PARAM_PUSH_ACTIVITY);

                if (remoteMessage.getData().containsKey(PARAM_PUSH_URL))
                    url = remoteMessage.getData().get(PARAM_PUSH_URL);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private Intent createIntent(Context context, String activity, String url) {
        Class targetClass;
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(context.getApplicationContext().getPackageName());

        if (!url.equals("")) {
            try {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLDecoder.decode(url, "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (!activity.equals("")) {
            try {
                targetClass = Class.forName(activity);
                intent = new Intent(context, targetClass);
                intent.setAction(Intent.ACTION_SEND);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }
}
