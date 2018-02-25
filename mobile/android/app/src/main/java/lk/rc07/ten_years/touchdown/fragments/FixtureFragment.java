package lk.rc07.ten_years.touchdown.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.activities.MainActivity;
import lk.rc07.ten_years.touchdown.adapters.FixtureAdapter;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.MatchDAO;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.utils.SimpleDividerItemDecoration;
import lk.rc07.ten_years.touchdown.utils.TimeFormatter;

/**
 * Created by Sabri on 1/14/2017. fragment to show fixture
 */

public class FixtureFragment extends Fragment {

    //instances
    private LinearLayoutManager mLayoutManager;
    private Activity activity;
    private FixtureAdapter adapter;
    private List<Match> matches = new ArrayList<>();

    //primary data
    private String currentYear;

    //views
    private RecyclerView recycler_fixture;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_fixture, container, false);

            recycler_fixture = view.findViewById(R.id.recycler_fixtures);
            adapter = null;
        }
        setAdapter(TimeFormatter.millisecondsToString(System.currentTimeMillis(), "yyyy"));

//        if(getActivity() != null) {
//            Activity activity = getActivity();
//            RelativeLayout.LayoutParams lps = (RelativeLayout.LayoutParams) recycler_fixture.getLayoutParams();
//            lps.addRule(RelativeLayout.CENTER_IN_PARENT);
//
//            ViewTarget target = new ViewTarget(R.id.recycler_fixtures, activity);
//            ShowcaseView sv = new ShowcaseView.Builder(activity)
//                    .withMaterialShowcase()
//                    .setTarget(target)
//                    .setContentTitle("More Details")
//                    .setContentText("Select a match for more details.")
//                    .build();
//            sv.setButtonPosition(lps);
//        }

        return view;
    }

    Thread searchCurrentMatch = new Thread(new Runnable() {
        @Override
        public void run() {
            int pos = 0;
            long dateDifference = System.currentTimeMillis();
            for (int i = 0; i < matches.size(); i++) {
                Match match = matches.get(i);
                if (dateDifference > (match.getMatchDate() - System.currentTimeMillis()) &&
                        (match.getMatchDate() - System.currentTimeMillis()) > 0) {
                    dateDifference = match.getMatchDate() - System.currentTimeMillis();
                    pos = i;
                }
            }

            final int finalPos = pos;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLayoutManager.scrollToPositionWithOffset(finalPos, 0);
                }
            });
        }
    });

    private void setAdapter(String year) {

        currentYear = year;
        DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(getContext()));
        dbManager.openDatabase();
        matches.clear();
        matches.addAll(MatchDAO.getAllMatches(TimeFormatter.getMilisecondsForYear(year)));
        dbManager.closeDatabase();

        if (adapter == null) {
            mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_fixture.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recycler_fixture.setLayoutManager(mLayoutManager);

            adapter = new FixtureAdapter(getContext(), matches);
            recycler_fixture.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        searchCurrentMatch.run();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();

        if (activity != null)
            ((MainActivity) activity).spnSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String selectedYear = ((MainActivity) activity).spnSelector.getAdapter().getItem(i).toString();
                            if (!currentYear.equals(selectedYear)) {

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setAdapter(selectedYear);
                                    }
                                });
                            }
                        }
                    }).start();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
    }
}