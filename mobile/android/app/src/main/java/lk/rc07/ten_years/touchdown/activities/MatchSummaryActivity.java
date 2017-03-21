package lk.rc07.ten_years.touchdown.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.GroupDAO;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
import lk.rc07.ten_years.touchdown.models.Group;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.models.Team;
import lk.rc07.ten_years.touchdown.utils.AppHandler;

public class MatchSummaryActivity extends AppCompatActivity {

    //constants
    public static final String GAME_NAME = "%s Vs %s";
    public static final String EXTRA_MATCH_OBJECT = "match_extra";

    //instances
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Match match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_summary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageLoader = ImageLoader.getInstance();
        options = AppHandler.getImageOption(imageLoader, this, R.drawable.icon_book_placeholder);

        match = getIntent().getExtras().getParcelable(EXTRA_MATCH_OBJECT);
        loadScore.start();
    }

    Thread loadScore = new Thread(new Runnable() {
        @Override
        public void run() {
            DBManager dbManager = DBManager.initializeInstance(
                    DBHelper.getInstance(MatchSummaryActivity.this));
            dbManager.openDatabase();

            List<Score> scores = null;
            Group group = null;
            if (match != null) {
                scores = ScoreDAO.getScores(match.getIdmatch());
                group = GroupDAO.getGroupForId(match.getGroup());
            }

            Team tOne = null;
            Team tTwo = null;

            //set matchStart time
            if (match != null) {
                calculateScore(match, scores);

                tOne = TeamDAO.getTeam(match.getTeamOne());
                tTwo = TeamDAO.getTeam(match.getTeamTwo());
            }

            final Team teamOne = tOne;
            final Team teamTwo = tTwo;

            dbManager.closeDatabase();

//            final Group finalGroup = group;
//            final List<Score> temp_scores = scores;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (match != null) {
                        setViews(teamOne, teamTwo);
                    }
                }
            });

        }
    });

    private void calculateScore(Match match, List<Score> scores) {

        TextView txt_home_score = (TextView) findViewById(R.id.txt_home_score);
        TextView txt_opponent_score = (TextView) findViewById(R.id.txt_opponent_score);

        int leftScoreTotal = 0;
        int rightScoreTotal = 0;
        for (Score score : scores) {
            if (score.getTeamId() == match.getTeamOne())
                leftScoreTotal += score.getScore();
            else
                rightScoreTotal += score.getScore();
        }
        txt_home_score.setText(String.valueOf(leftScoreTotal));
        txt_opponent_score.setText(String.valueOf(rightScoreTotal));
    }

    private void setViews(Team tOne, Team tTwo) {

        ImageView img_home = (ImageView) findViewById(R.id.img_home_crest);
        ImageView img_opponent = (ImageView) findViewById(R.id.img_opponent_crest);
        TextView txt_game_name = (TextView) findViewById(R.id.txt_game_name);
        LinearLayout layout_scores = (LinearLayout) findViewById(R.id.layout_scores);
        LinearLayout layout_penalties = (LinearLayout) findViewById(R.id.layout_penalties);

        imageLoader.displayImage(tOne.getLogo_url(), img_home, options);
        imageLoader.displayImage(tTwo.getLogo_url(), img_opponent, options);

        txt_game_name.setText(String.format(Locale.getDefault(), GAME_NAME, tOne.getName(), tTwo.getName()));
    }

}
