package lk.rc07.ten_years.touchdown.activities;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.BuildConfig;
import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.PositionDAO;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.data.PlayerTeamDAO;
import lk.rc07.ten_years.touchdown.models.Player;
import lk.rc07.ten_years.touchdown.models.Position;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.utils.AppHandler;
import lk.rc07.ten_years.touchdown.utils.TimeFormatter;

public class PlayerDialogActivity extends AppCompatActivity {

    //constants
    public static final String EXTRA_PLAYER_OBJECT = "player_extra";
    public static final String EXTRA_PLAYER_POSITION = "player_pos_extra";
    public static final String EXTRA_PLAYER_YEAR = "player_year";
    public static final String EXTRA_PLAYER_TEAM = "player_team";
    //    public static final String EXTRA_PLAYER_POSITION_ID = "player_pos_id_extra";
    //values
    private static final String PLAYER_AGE_VALUE = "Age : %d";
    private static final String PLAYER_COLORS_VALUE = "%d%s year";
    private static final String PLAYER_WEIGHT_VALUE = "W : %d kg";
    private static final String PLAYER_HEIGHT_VALUE = "H : %s";

    //instances
    private String[] ORDINAL_INDICATOR = new String[]{"st", "nd", "rd", "th"};

    //views
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_player_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Player player = null;
        if (getIntent().getExtras() != null)
            player = getIntent().getExtras().getParcelable(EXTRA_PLAYER_OBJECT);
        String player_pos = String.format(Locale.getDefault(), "%02d", getIntent().getExtras().getInt(EXTRA_PLAYER_POSITION));
        int year = getIntent().getExtras().getInt(EXTRA_PLAYER_YEAR);
        int team = getIntent().getExtras().getInt(EXTRA_PLAYER_TEAM);

        if (player != null) {
            DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(this));
            dbManager.openDatabase();
            String currentYear = TimeFormatter.millisecondsToString(System.currentTimeMillis(), "yyyy");
            Position position = PositionDAO.getPositionForNo(Integer.parseInt(player_pos));
            int yellowCards = ScoreDAO.getPlayerAction(player.getIdPlayer(), Score.Action.YELLOW_CARD, currentYear);
            int redCards = ScoreDAO.getPlayerAction(player.getIdPlayer(), Score.Action.RED_CARD, currentYear);
            int tries = ScoreDAO.getPlayerAction(player.getIdPlayer(), Score.Action.TRY, currentYear);
            int conversions = ScoreDAO.getPlayerAction(player.getIdPlayer(), Score.Action.CONVERSION, currentYear);
            int penalties = ScoreDAO.getPlayerAction(player.getIdPlayer(), Score.Action.PENALTY_KICK, currentYear);
            int dropGoals = ScoreDAO.getPlayerAction(player.getIdPlayer(), Score.Action.DROP_GOAL, currentYear);
            int colors = PlayerTeamDAO.getPlayerTeam(player.getIdPlayer(), team, year).getColors();
            dbManager.closeDatabase();

            ((TextView) findViewById(R.id.txt_player_name)).setText(player.getName());
            ((TextView) findViewById(R.id.txt_player_no)).setText(player_pos);
            ((TextView) findViewById(R.id.txt_player_age)).setText(String.format(Locale.getDefault(),
                    PLAYER_AGE_VALUE, calculateAge(player.getBirthDay())));
            ((TextView) findViewById(R.id.txt_player_colors)).setText(getOrdinalString(colors));
            ((TextView) findViewById(R.id.txt_player_weight)).setText(String.format(Locale.getDefault(),
                    PLAYER_WEIGHT_VALUE, (int) player.getWeight()));
            ((TextView) findViewById(R.id.txt_player_height)).setText(getHeightString(player.getHeight()));
            ((TextView) findViewById(R.id.txt_player_position)).setText(position.getPosName());
            ((TextView) findViewById(R.id.txt_yellow_stat)).setText(String.valueOf(yellowCards));
            ((TextView) findViewById(R.id.txt_red_stat)).setText(String.valueOf(redCards));

            ((TextView) findViewById(R.id.txt_tries_stat)).setText(String.valueOf(tries));
            ((TextView) findViewById(R.id.txt_conversions_stat)).setText(String.valueOf(conversions));
            ((TextView) findViewById(R.id.txt_penalty_stat)).setText(String.valueOf(penalties));
            ((TextView) findViewById(R.id.txt_drop_stat)).setText(String.valueOf(dropGoals));

            profilePic = findViewById(R.id.img_player_pic);

            String img_link = BuildConfig.DEFAULT_URL + player.getImg_url();

            Glide.with(this).load(img_link).placeholder(R.drawable.default_profile_pic)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            profilePic.setImageDrawable(AppHandler.getDrawable(PlayerDialogActivity.this, R.drawable.default_profile_pic));
                            scheduleStartPostponedTransition(profilePic, PlayerDialogActivity.this);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            profilePic.setImageDrawable(resource);
                            scheduleStartPostponedTransition(profilePic, PlayerDialogActivity.this);
                            return false;
                        }
                    }).into(profilePic);
        }

    }

    public static void scheduleStartPostponedTransition(final View sharedElement, final Activity mActivity) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mActivity.startPostponedEnterTransition();
                        }
                        return true;
                    }
                });
    }

    private String getOrdinalString(int years) {
        String ordinal = ORDINAL_INDICATOR[ORDINAL_INDICATOR.length - 1];
        if (years < ORDINAL_INDICATOR.length && years > 0)
            ordinal = ORDINAL_INDICATOR[years - 1];

        return String.format(Locale.getDefault(), PLAYER_COLORS_VALUE, years, ordinal);
    }

    private String getHeightString(double height) {
        String ordinal = String.format(Locale.getDefault(), "%.2f", height);
        ordinal = ordinal.replace(".", "'");
        ordinal += "''";

        return String.format(Locale.getDefault(), PLAYER_HEIGHT_VALUE, ordinal);
    }

    private int calculateAge(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar birthday = Calendar.getInstance();
        birthday.setTime(date);

        return today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);

    }
}
