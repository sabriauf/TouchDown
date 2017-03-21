package lk.rc07.ten_years.touchdown.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.adapters.PlayerAdapter;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.MatchDAO;
import lk.rc07.ten_years.touchdown.data.PlayerPositionDAO;
import lk.rc07.ten_years.touchdown.models.AdapterPlayer;
import lk.rc07.ten_years.touchdown.models.Match;

/**
 * Created by Sabri on 2/4/2017. Fragment to show Player profiles
 */

public class PlayersFragment extends Fragment {

    private static final int PLAYER_COLUMNS = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_players, container, false);

        DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(getContext()));
        dbManager.openDatabase();

//        int matchId = 0;
//        Match match = MatchDAO.getDisplayMatch();
//        if (match != null)
//            matchId = match.getIdmatch();
//        PlayerPositionDAO.getPlayerPosition(2);
        List<AdapterPlayer> players = PlayerPositionDAO.getPlayersForMatch(AppConfig.HOME_TEAM_ID);
        dbManager.closeDatabase();

        RecyclerView recycler_fixture = (RecyclerView) view.findViewById(R.id.recycler_players);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), PLAYER_COLUMNS);
        recycler_fixture.setLayoutManager(layoutManager);
        recycler_fixture.setAdapter(new PlayerAdapter(getActivity(), players));

        return view;
    }
}
