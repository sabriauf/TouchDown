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
import lk.rc07.ten_years.touchdown.adapters.FixtureAdapter;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.MatchDAO;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.utils.SimpleDividerItemDecoration;

/**
 * Created by Sabri on 1/14/2017. fragment to show fixture
 */

public class FixtureFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fixture, container, false);

        DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(getContext()));
        dbManager.openDatabase();
        List<Match> matches = MatchDAO.getAllMatches();
        dbManager.closeDatabase();

        RecyclerView recycler_fixture = (RecyclerView) view.findViewById(R.id.recycler_fixtures);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_fixture.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_fixture.setLayoutManager(mLayoutManager);
        recycler_fixture.setAdapter(new FixtureAdapter(getContext(), matches));

        return view;
    }
}