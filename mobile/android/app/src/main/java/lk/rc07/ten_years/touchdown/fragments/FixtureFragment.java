package lk.rc07.ten_years.touchdown.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

    //instances
    public static Handler mHandler;
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fixture, container, false);

        DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(getContext()));
        dbManager.openDatabase();
        List<Match> matches = MatchDAO.getAllMatches();
        dbManager.closeDatabase();

        RecyclerView recycler_fixture = (RecyclerView) view.findViewById(R.id.recycler_fixtures);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_fixture.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_fixture.setLayoutManager(mLayoutManager);
        recycler_fixture.setAdapter(new FixtureAdapter(getContext(), matches));

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                mLayoutManager.scrollToPosition(message.arg1);
                return false;
            }
        });

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
}