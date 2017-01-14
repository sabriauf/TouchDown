package lk.rc07.ten_years.touchdown.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.activities.MainActivity;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.models.Score;

/**
 * Created by Sabri on 12/13/2016. handling fcm
 */

public class TouchDownMessagingService extends FirebaseMessagingService {

    //constants
    private static final String TAG = TouchDownMessagingService.class.getSimpleName();
    private final String PARAM_PUSH_TITLE = "title";
    private final String PARAM_PUSH_MESSAGE = "message";
    private final String PARAM_PUSH_OBJECT = "object";
    private final String PARAM_OBJECT_SCORE = "score";

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
            sendNotification(remoteMessage.getData().get(PARAM_PUSH_TITLE), remoteMessage.getData().get(PARAM_PUSH_MESSAGE));

            String value = remoteMessage.getData().get(PARAM_PUSH_OBJECT);
            if (value != null && !value.equals("")) {
                try {
                    JSONObject respond = new JSONObject(value);
                    if (respond.has(PARAM_OBJECT_SCORE)) {
                        Score score = new Gson().fromJson(respond.getJSONObject(PARAM_OBJECT_SCORE).toString(), Score.class);


                        DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(this));
                        dbManager.openDatabase();

                        Message msg = new Message();
                        msg.obj = score;
                        if(score.getAction() != null) {
                            boolean inserted = ScoreDAO.addScore(score);

                            if (inserted)
                                msg.what = Score.WHAT_NEW_SCORE;
                            else
                                msg.what = Score.WHAT_UPDATE_SCORE;
                        } else {
                            ScoreDAO.deleteScore(score.getIdscore());
                            msg.what = Score.WHAT_REMOVE_SCORE;
                        }

                        dbManager.closeDatabase();
                        Score.handler.sendMessage(msg);

                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
