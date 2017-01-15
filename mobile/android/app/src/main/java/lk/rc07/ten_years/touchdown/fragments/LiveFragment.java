package lk.rc07.ten_years.touchdown.fragments;

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
import lk.rc07.ten_years.touchdown.data.MatchDAO;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
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

    //instances
    private ScoreAdapter adapter;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ViewHolder holder;

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
        options = AppHandler.getImageOption(imageLoader, getContext());

        parentView = view;
        setData(view, null, null, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_timeline);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(false);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        loadScore.run();

        return view;
    }

    Thread loadScore = new Thread(new Runnable() {
        @Override
        public void run() {
            DBManager dbManager = DBManager.initializeInstance(
                    DBHelper.getInstance(getContext()));
            dbManager.openDatabase();

            final Match match = getDisplayMatch();
            List<Score> scores = null;
            if (match != null)
                scores = ScoreDAO.getScores(match.getIdmatch());

            Team tOne = null;
            Team tTwo = null;

            //set matchStart time
            if (match != null) {
                List<Score> startScores = ScoreDAO.getActionScore(match.getIdmatch(), Score.Action.START);
                if (startScores.size() > 0)
                    matchStartTime = startScores.get(0).getTime();
                calculateScore(match, scores);

                tOne = TeamDAO.getTeam(match.getTeamOne());
                tTwo = TeamDAO.getTeam(match.getTeamTwo());
            }

            final Team teamOne = tOne;
            final Team teamTwo = tTwo;

            dbManager.closeDatabase();

            final List<Score> temp_scores = scores;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (match != null) {
                        adapter = new ScoreAdapter(getContext(), temp_scores, match);
                        recyclerView.setAdapter(adapter);
                        setData(parentView, match, teamOne, teamTwo);
                    }
                }
            });

        }
    });

    private void setData(View view, final Match match, Team tOne, Team tTwo) {
        holder = new ViewHolder(view);

        Score.setScoreListener(new ScoreListener() {
            @Override
            public void OnNewScoreUpdate(Score score) {
                updateScore(score, match);
            }

            @Override
            public void OnScoreUpdate(Score score) {
                adapter.updateItem(score);
                calculateScore(match, adapter.getScores());
                holder.txt_score_one.setText(String.valueOf(leftScoreTotal));
                holder.txt_score_two.setText(String.valueOf(rightScoreTotal));
            }

            @Override
            public void OnScoreRemove(Score score) {
                adapter.removeItem(score);
                calculateScore(match, adapter.getScores());
                holder.txt_score_one.setText(String.valueOf(leftScoreTotal));
                holder.txt_score_two.setText(String.valueOf(rightScoreTotal));
            }


        }, LiveFragment.class.getSimpleName());

        if (match == null)
            setDefaultView();
        else
            setMatchView(match, tOne, tTwo);

        final Handler timer = new Handler();
        timer.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.txt_time.setText(TimeFormatter.millisToGameTime(getContext(), matchStartTime));
                timer.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void updateScore(Score score, Match match) {
        switch (score.getAction()) {
            case START:
                matchStartTime = score.getTime();
                break;
            default:
                adapter.addItem(score);
                if (score.getTeamId() == match.getTeamOne())
                    leftScoreTotal += score.getScore();
                else
                    rightScoreTotal += score.getScore();
                holder.txt_score_one.setText(String.valueOf(leftScoreTotal));
                holder.txt_score_two.setText(String.valueOf(rightScoreTotal));
        }
    }

//    private List<Score> getScores(int matchId) {
//        DBManager dbManager = DBManager.initializeInstance(
//                DBHelper.getInstance(getContext()));
//        dbManager.openDatabase();
//        List<Score> scores = ScoreDAO.getScores(matchId);
//        dbManager.closeDatabase();
//        return scores;
//    }

    private void setDefaultView() {
        holder.txt_league.setText("");
        holder.txt_round.setText("");
        holder.txt_score_two.setText("0");
        holder.txt_score_one.setText("0");
        holder.txt_live.setVisibility(View.GONE);
        holder.txt_time.setText(R.string.default_live_match_time);
    }

    private void setMatchView(Match match, Team tOne, Team tTwo) {
        holder.txt_league.setText(match.getLeague());
        holder.txt_round.setText(match.getRound());
        holder.txt_score_two.setText(String.valueOf(rightScoreTotal));
        holder.txt_score_one.setText(String.valueOf(leftScoreTotal));
        imageLoader.displayImage(tOne.getLogo_url(), holder.img_team_one_logo, options);
        imageLoader.displayImage(tTwo.getLogo_url(), holder.img_team_two_logo, options);
        if (match.getStatus() == Match.Status.PROGRESS) {
            holder.txt_live.setVisibility(View.VISIBLE);
            holder.txt_time.setText(TimeFormatter.millisToGameTime(getContext(), matchStartTime));
        } else {
            holder.txt_live.setVisibility(View.GONE);
            holder.txt_time.setText(match.getResult());
        }
    }

    private Match getDisplayMatch() {

        List<Match> matches = MatchDAO.getMatchesOnStatus(Match.Status.PROGRESS);
        if (matches.size() > 0)
            return matches.get(matches.size() - 1);
        else {
            matches = MatchDAO.getMatchesOnStatus(Match.Status.DONE);
            if (matches.size() > 0)
                return matches.get(matches.size() - 1);
            else
                return null;
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
