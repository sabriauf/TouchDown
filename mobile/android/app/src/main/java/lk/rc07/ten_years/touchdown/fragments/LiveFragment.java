package lk.rc07.ten_years.touchdown.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.adapters.ScoreAdapter;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.MatchDAO;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.utils.AutoScaleTextView;
import lk.rc07.ten_years.touchdown.utils.ScoreListener;

/**
 * Created by Sabri on 12/13/2016. fragment to show the live score
 */

public class LiveFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);

        View view_team_one = view.findViewById(R.id.layout_score_team_one);
        final AutoScaleTextView txt_score_one = (AutoScaleTextView) view_team_one.findViewById(R.id.txt_score);

        View view_team_two = view.findViewById(R.id.layout_score_team_two);
        final AutoScaleTextView txt_score_two = (AutoScaleTextView) view_team_two.findViewById(R.id.txt_score);

        txt_score_two.setText("0");
        txt_score_one.setText("0");

        Score.setScoreListener(new ScoreListener() {
            @Override
            public void OnNewScoreUpdate(Score score) {
                txt_score_two.setText(String.valueOf(score.getScore()));
            }
        }, LiveFragment.class.getSimpleName());

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
            List<Match> matches = MatchDAO.getMatchesOnStatus(Match.Status.PROGRESS);
            List<Score> scores;
            if (matches.size() > 0)
                matches = MatchDAO.getMatchesOnStatus(Match.Status.DONE);
            final Match match = matches.get(matches.size() - 1);
            scores = ScoreDAO.getScores(match.getIdmatch());
            dbManager.closeDatabase();

            final List<Score> temp_scores = scores;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setAdapter(new ScoreAdapter(getContext(), temp_scores, match));
                    //TODO - update other info
                }
            });
        }
    });
}