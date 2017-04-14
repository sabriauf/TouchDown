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
import lk.rc07.ten_years.touchdown.activities.MatchSummaryActivity;
import lk.rc07.ten_years.touchdown.adapters.ScoreAdapter;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Score;

/**
 * Created by Sabri on 4/1/2017. fragment for timeline
 */

public class TimelineFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_timeline, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_timeline);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        Match match = getArguments().getParcelable(MatchSummaryActivity.EXTRA_MATCH_OBJECT);
        if(match != null) {
            DBManager dbManager = DBManager.initializeInstance(
                    DBHelper.getInstance(getContext()));
            dbManager.openDatabase();
            List<Score> scores = ScoreDAO.getScores(match.getIdmatch());
            dbManager.closeDatabase();
            ScoreAdapter adapter = new ScoreAdapter(getContext(), scores, match);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }
}
