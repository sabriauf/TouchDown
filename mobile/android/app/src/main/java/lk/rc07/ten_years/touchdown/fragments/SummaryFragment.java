package lk.rc07.ten_years.touchdown.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.activities.MatchSummaryActivity;
import lk.rc07.ten_years.touchdown.adapters.SummaryPlayerAdapter;
import lk.rc07.ten_years.touchdown.config.AppConfig;
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
import lk.rc07.ten_years.touchdown.utils.AutoScaleTextView;

/**
 * Created by Sabri on 3/27/2017. Summary fragment
 */

public class SummaryFragment extends Fragment {

    //constants
    public static final int WHAT_SCORER_SIZE = 101;
    public static final int WHAT_PENALIZED_SIZE = 102;

    //instances
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Match match;
    public static Handler handler;
    private FragmentActivity activity;

    //views
    private RecyclerView recycler_scoreres;
    private RecyclerView recycler_penalized;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_match_summary, container, false);

        activity = getActivity();

        imageLoader = ImageLoader.getInstance();
        options = AppHandler.getImageOption(imageLoader, view.getContext(), R.drawable.icon_book_placeholder);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == WHAT_SCORER_SIZE) {
                    if (recycler_scoreres != null) {
                        ViewGroup.LayoutParams params = recycler_scoreres.getLayoutParams();
                        params.height = message.arg1;
                        recycler_scoreres.setLayoutParams(params);
                    }
                } else if (message.what == WHAT_PENALIZED_SIZE) {
                    if (recycler_penalized != null) {
                        ViewGroup.LayoutParams params = recycler_penalized.getLayoutParams();
                        params.height = message.arg1;
                        recycler_penalized.setLayoutParams(params);
                    }
                }
                return false;
            }
        });

        match = getArguments().getParcelable(MatchSummaryActivity.EXTRA_MATCH_OBJECT);
        loadScore.start();

        return view;
    }

    Thread loadScore = new Thread(new Runnable() {
        @Override
        public void run() {
            DBManager dbManager = DBManager.initializeInstance(
                    DBHelper.getInstance(activity));
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
                tOne = TeamDAO.getTeam(match.getTeamOne());
                tTwo = TeamDAO.getTeam(match.getTeamTwo());

                calculateScore(match, scores, group, tOne, tTwo);
            }

            final Team teamOne = tOne;
            final Team teamTwo = tTwo;

            dbManager.closeDatabase();

//            final Group finalGroup = group;
//            final List<Score> temp_scores = scores;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (match != null) {
                        setViews(teamOne, teamTwo);
                    }
                }
            });

        }
    });

    private void calculateScore(Match match, List<Score> scores, final Group group, final Team tOne, final Team tTwo) {

        int leftScoreTotal = 0;
        int rightScoreTotal = 0;
        for (Score score : scores) {
            if (score.getTeamId() == match.getTeamOne())
                leftScoreTotal += score.getScore();
            else
                rightScoreTotal += score.getScore();
        }

        final int score_left = leftScoreTotal;
        final int score_right = rightScoreTotal;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View layout_score = view.findViewById(R.id.layout_score);

                layout_score.findViewById(R.id.txt_match_time).setVisibility(View.GONE);
                AutoScaleTextView txt_league = (AutoScaleTextView) view.findViewById(R.id.txt_league_name);
                txt_league.setPreference(AutoScaleTextView.WIDTH_RESIZE);
                txt_league.setText(group.getLeagueName());
                ((AutoScaleTextView) view.findViewById(R.id.txt_round_name)).setText(group.getRoundName());

                View score_layout_home = layout_score.findViewById(R.id.layout_score_team_one);
                View score_layout_oppo = layout_score.findViewById(R.id.layout_score_team_two);
                ((TextView) score_layout_home.findViewById(R.id.txt_score)).setText(String.valueOf(score_left));
                ((TextView) score_layout_oppo.findViewById(R.id.txt_score)).setText(String.valueOf(score_right));

                imageLoader.displayImage(tOne.getLogo_url(), ((ImageView) score_layout_home.findViewById(R.id.img_college_crest)), options);
                imageLoader.displayImage(tTwo.getLogo_url(), ((ImageView) score_layout_oppo.findViewById(R.id.img_college_crest)), options);
            }
        });
    }

    private void setViews(Team tOne, Team tTwo) {
        LinearLayout layout_scores = (LinearLayout) view.findViewById(R.id.layout_scores);
        LinearLayout layout_penalties = (LinearLayout) view.findViewById(R.id.layout_penalties);
        recycler_scoreres = (RecyclerView) view.findViewById(R.id.recycler_scoreres);
        recycler_penalized = (RecyclerView) view.findViewById(R.id.recycler_penalized);

        DBManager dbManager = DBManager.initializeInstance(
                DBHelper.getInstance(activity));
        dbManager.openDatabase();

        LayoutInflater layoutInflater = (LayoutInflater)
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //scores
        layout_scores.addView(addScoreView(layoutInflater, layout_scores, tOne, tTwo, "GOALS", Score.Action.CONVERSION));
        layout_scores.addView(addTryView(layoutInflater, layout_scores, tOne, tTwo));
        layout_scores.addView(addScoreView(layoutInflater, layout_scores, tOne, tTwo, "Drop Goals",
                Score.Action.DROP_GOAL));
        layout_scores.addView(addScoreView(layoutInflater, layout_scores, tOne, tTwo, "Penalty Kicks",
                Score.Action.PENALTY_KICK));

        //penalties
        layout_penalties.addView(addScoreView(layoutInflater, layout_penalties, tOne, tTwo, "Red Cards",
                Score.Action.RED_CARD));
        layout_penalties.addView(addScoreView(layoutInflater, layout_penalties, tOne, tTwo, "Yellow Cards",
                Score.Action.YELLOW_CARD));

        setRecyclerViews(recycler_scoreres, new SummaryPlayerAdapter(activity,
                ScoreDAO.getScorers(match.getIdmatch(), AppConfig.HOME_TEAM_ID)));

        setRecyclerViews(recycler_penalized, new SummaryPlayerAdapter(activity,
                ScoreDAO.getPenalizedPlayers(match.getIdmatch(), AppConfig.HOME_TEAM_ID)));

        dbManager.closeDatabase();
    }

    private void setRecyclerViews(RecyclerView recyclerView, SummaryPlayerAdapter adapter) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
    }

    private View addScoreView(LayoutInflater layoutInflater, LinearLayout layout_scores, Team tOne,
                              Team tTwo, String title, Score.Action action) {
        View view = layoutInflater.inflate(R.layout.component_summary_row, layout_scores, false);

        int scoreCountHome = ScoreDAO.getActionScore(match.getIdmatch(), tOne.getIdTeam(), action).size();
        int scoreCountOpp = ScoreDAO.getActionScore(match.getIdmatch(), tTwo.getIdTeam(), action).size();

        ((TextView) view.findViewById(R.id.txt_title)).setText(title);
        ((TextView) view.findViewById(R.id.txt_left_value)).setText(String.valueOf(scoreCountHome));
        ((TextView) view.findViewById(R.id.txt_right_value)).setText(String.valueOf(scoreCountOpp));

        return view;
    }

    private View addTryView(LayoutInflater layoutInflater, LinearLayout layout_scores, Team tOne, Team tTwo) {
        View view = layoutInflater.inflate(R.layout.component_summary_row, layout_scores, false);

        int tryCountHome = ScoreDAO.getActionScore(match.getIdmatch(), tOne.getIdTeam(), Score.Action.TRY).size();
        int conversionCountHome = ScoreDAO.getActionScore(match.getIdmatch(), tOne.getIdTeam(), Score.Action.CONVERSION).size();
        int tryCountOpp = ScoreDAO.getActionScore(match.getIdmatch(), tTwo.getIdTeam(), Score.Action.TRY).size();
        int conversionCountOpp = ScoreDAO.getActionScore(match.getIdmatch(), tTwo.getIdTeam(), Score.Action.CONVERSION).size();

        ((TextView) view.findViewById(R.id.txt_title)).setText(R.string.tries);
        ((TextView) view.findViewById(R.id.txt_left_value)).setText(String.valueOf(tryCountHome - conversionCountHome));
        ((TextView) view.findViewById(R.id.txt_right_value)).setText(String.valueOf(tryCountOpp - conversionCountOpp));

        return view;
    }
}
