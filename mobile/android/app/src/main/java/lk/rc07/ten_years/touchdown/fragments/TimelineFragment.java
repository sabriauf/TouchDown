package lk.rc07.ten_years.touchdown.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.SortedMap;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.activities.MatchSummaryActivity;
import lk.rc07.ten_years.touchdown.adapters.ScoreAdapter;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.models.GameTime;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.utils.AppHandler;

/**
 * Created by Sabri on 4/1/2017. fragment for timeline
 */

public class TimelineFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_timeline, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_timeline);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        if (getArguments() != null) {
            Match match = getArguments().getParcelable(MatchSummaryActivity.EXTRA_MATCH_OBJECT);
            if (match != null) {
                DBManager dbManager = DBManager.initializeInstance(
                        DBHelper.getInstance(getContext()));
                dbManager.openDatabase();
                List<Score> scores = ScoreDAO.getScores(match.getIdmatch());
                SortedMap<Long, GameTime> gameTimes = AppHandler.getMatchGameTimes(match.getIdmatch());
                dbManager.closeDatabase();
                ScoreAdapter adapter = new ScoreAdapter(getContext(), scores, match, gameTimes);
                recyclerView.setAdapter(adapter);
            }
        }

        return view;
    }
}
