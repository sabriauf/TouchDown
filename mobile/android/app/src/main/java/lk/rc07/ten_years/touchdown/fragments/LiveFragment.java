package lk.rc07.ten_years.touchdown.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.adapters.ScoreAdapter;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.GroupDAO;
import lk.rc07.ten_years.touchdown.data.MatchDAO;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
import lk.rc07.ten_years.touchdown.models.Group;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.models.Team;
import lk.rc07.ten_years.touchdown.utils.AppHandler;
import lk.rc07.ten_years.touchdown.utils.AutoScaleTextView;
import lk.rc07.ten_years.touchdown.utils.ScoreListener;
import lk.rc07.ten_years.touchdown.utils.TimeFormatter;

/**
 * Created by Sabri on 12/13/2016. fragment to show the live score
 */

public class LiveFragment extends Fragment {

    private static final String DIGITAL_CLOCK_FONT = "fonts/digital_7.ttf";
    //instances
    private ScoreAdapter adapter;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ViewHolder holder;
    private Handler timer;

    //primary data
    private long matchStartTime = 0;
    private int leftScoreTotal = 0;
    private int rightScoreTotal = 0;

    //views
    private RecyclerView recyclerView;
    private View parentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);

        this.imageLoader = ImageLoader.getInstance();
        options = AppHandler.getImageOption(imageLoader, getContext(), R.drawable.icon_book_placeholder);

        parentView = view;

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_timeline);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(false);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadScore.run();
    }

    Thread loadScore = new Thread(new Runnable() {
        @Override
        public void run() {
            DBManager dbManager = DBManager.initializeInstance(
                    DBHelper.getInstance(getContext()));
            dbManager.openDatabase();

            final Match match = MatchDAO.getDisplayMatch();
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
                getMatchStartTime(match);
                calculateScore(match, scores);

                tOne = TeamDAO.getTeam(match.getTeamOne());
                tTwo = TeamDAO.getTeam(match.getTeamTwo());
            }

            final Team teamOne = tOne;
            final Team teamTwo = tTwo;

            dbManager.closeDatabase();

            final Group finalGroup = group;
            final List<Score> temp_scores = scores;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (match != null) {
                        adapter = new ScoreAdapter(getContext(), temp_scores, match);
                        recyclerView.setAdapter(adapter);
                        setData(parentView, match, finalGroup, teamOne, teamTwo);
                        if (temp_scores.size() > 0)
                            recyclerView.smoothScrollToPosition(temp_scores.size() - 1);
                    } else {
                        setData(parentView, null, null, null, null);
                    }
                }
            });

        }
    });

    private void getMatchStartTime(Match match) {
        List<Score> startScores = ScoreDAO.getActionScore(match.getIdmatch(), Score.Action.SECOND_HALF);
        if (startScores.size() > 0)
            matchStartTime = startScores.get(0).getTime();
        else {
            startScores = ScoreDAO.getActionScore(match.getIdmatch(), Score.Action.START);
            if (startScores.size() > 0)
                matchStartTime = startScores.get(0).getTime();
        }
    }


    private void setData(View view, final Match match, Group group, Team tOne, Team tTwo) {
        holder = new ViewHolder(view);

        Score.setScoreListener(new ScoreListener() {
            @Override
            public void OnNewScoreUpdate(Score score) {
                if (score.getMatchid() != match.getIdmatch())
                    loadScore.run();
                else
                    updateScore(score, getUpdatedMatch(score, match));
            }

            @Override
            public void OnScoreUpdate(Score score) {
                if (score.getMatchid() == match.getIdmatch()) {
                    if (score.getAction() == Score.Action.START)
                        setTimer(getUpdatedMatch(score, match));
                    adapter.updateItem(score);
                    calculateScore(match, adapter.getScores());
                    holder.txt_score_one.setText(String.valueOf(leftScoreTotal));
                    holder.txt_score_two.setText(String.valueOf(rightScoreTotal));
                }
            }

            @Override
            public void OnScoreRemove(Score score) {
                if (score.getMatchid() == match.getIdmatch()) {
                    adapter.removeItem(score);
                    calculateScore(match, adapter.getScores());
                    holder.txt_score_one.setText(String.valueOf(leftScoreTotal));
                    holder.txt_score_two.setText(String.valueOf(rightScoreTotal));
                }
            }


        }, LiveFragment.class.getSimpleName());

        if (match == null)
            setDefaultView();
        else
            setMatchView(match, group, tOne, tTwo);

        setTimer(match);
    }

    private Match getUpdatedMatch(Score score, Match match) {
        if (score.getActionType() == Score.WHAT_ACTION_TIME) {
            DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(getContext()));
            dbManager.openDatabase();
            match = MatchDAO.getMatchForId(score.getMatchid());
            dbManager.closeDatabase();
        }
        return match;
    }

    private void setTimer(final Match match) {
        timer = new Handler();
        timer.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (match != null && (match.getStatus() == Match.Status.FIRST_HALF || match.getStatus() == Match.Status.SECOND_HALF)
                        && matchStartTime != 0) {
                    holder.txt_time.setText(TimeFormatter.millisToGameTime(getContext(), matchStartTime));
                    timer.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    private void updateScore(Score score, Match match) {
        adapter.addItem(score);
        switch (score.getAction()) {
            case START:
                matchStartTime = score.getTime();
                holder.txt_live.setVisibility(View.VISIBLE);
                setTimerFont(true);
                setTimer(match);
                break;
            case HALF_TIME:
                matchStartTime = 0;
                break;
            case SECOND_HALF:
                matchStartTime = score.getTime();
                holder.txt_live.setVisibility(View.VISIBLE);
                setTimerFont(true);
                setTimer(match);
                break;
            case FULL_TIME:
                matchStartTime = 0;
                break;
            default:
                if (score.getTeamId() == match.getTeamOne())
                    leftScoreTotal += score.getScore();
                else
                    rightScoreTotal += score.getScore();
                holder.txt_score_one.setText(String.valueOf(leftScoreTotal));
                holder.txt_score_two.setText(String.valueOf(rightScoreTotal));
        }
    }

    private void setDefaultView() {
        holder.txt_league.setText("");
        holder.txt_round.setText("");
        holder.txt_score_two.setText("0");
        holder.txt_score_one.setText("0");
        holder.txt_live.setVisibility(View.GONE);
        setTimerFont(true);
        holder.txt_time.setText(R.string.default_live_match_time);
    }

    private void setMatchView(Match match, Group group, Team tOne, Team tTwo) {
        if (group != null) {
            holder.txt_league.setText(group.getLeagueName());
            holder.txt_round.setText(group.getRoundName());
        } else {
            holder.txt_league.setText("");
            holder.txt_round.setText("");
        }
        holder.txt_score_two.setText(String.valueOf(rightScoreTotal));
        holder.txt_score_one.setText(String.valueOf(leftScoreTotal));
        imageLoader.displayImage(tOne.getLogo_url(), holder.img_team_one_logo, options);
        imageLoader.displayImage(tTwo.getLogo_url(), holder.img_team_two_logo, options);
        if (match.getStatus() == Match.Status.FIRST_HALF || match.getStatus() == Match.Status.SECOND_HALF) {
            holder.txt_live.setVisibility(View.VISIBLE);
            setTimerFont(true);
            holder.txt_time.setText(TimeFormatter.millisToGameTime(getContext(), matchStartTime));
        } else {
            holder.txt_live.setVisibility(View.GONE);
            setTimerFont(false);
            matchStartTime = 0;
            holder.txt_time.setText(match.getResult());
        }
    }

    private void setTimerFont(boolean isTimer) {
        if (isTimer) {
            holder.txt_time.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), DIGITAL_CLOCK_FONT));
            holder.txt_time.setTextSize(42);
            holder.txt_time.setTextColor(AppHandler.getColor(getActivity(), R.color.timer_clock));
        } else {
            holder.txt_time.setTypeface(Typeface.SANS_SERIF);
            holder.txt_time.setTextSize(22);
            holder.txt_time.setTextColor(AppHandler.getColor(getActivity(), R.color.text_title));
        }
    }

    private void calculateScore(Match match, List<Score> scores) {

        leftScoreTotal = 0;
        rightScoreTotal = 0;
        for (Score score : scores) {
            if (score.getTeamId() == match.getTeamOne())
                leftScoreTotal += score.getScore();
            else
                rightScoreTotal += score.getScore();
        }
    }

    class ViewHolder {
        AutoScaleTextView txt_league;
        AutoScaleTextView txt_round;
        AutoScaleTextView txt_score_one;
        AutoScaleTextView txt_score_two;
        TextView txt_live;
        TextView txt_time;
        ImageView img_team_one_logo;
        ImageView img_team_two_logo;

        ViewHolder(View view) {
            txt_league = (AutoScaleTextView) view.findViewById(R.id.txt_league_name);
            txt_round = (AutoScaleTextView) view.findViewById(R.id.txt_round_name);
            txt_live = (TextView) view.findViewById(R.id.txt_live_notifier);
            txt_time = (TextView) view.findViewById(R.id.txt_match_time);

            View view_team_one = view.findViewById(R.id.layout_score_team_one);
            txt_score_one = (AutoScaleTextView) view_team_one.findViewById(R.id.txt_score);
            img_team_one_logo = (ImageView) view_team_one.findViewById(R.id.img_college_crest);

            View view_team_two = view.findViewById(R.id.layout_score_team_two);
            txt_score_two = (AutoScaleTextView) view_team_two.findViewById(R.id.txt_score);
            img_team_two_logo = (ImageView) view_team_two.findViewById(R.id.img_college_crest);
        }
    }
}
