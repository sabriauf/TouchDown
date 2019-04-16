package lk.rc07.ten_years.touchdown.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.adapters.PlayerAdapter;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.GroupDAO;
import lk.rc07.ten_years.touchdown.data.MatchDAO;
import lk.rc07.ten_years.touchdown.data.PlayerPositionDAO;
import lk.rc07.ten_years.touchdown.data.StaffDAO;
import lk.rc07.ten_years.touchdown.models.AdapterPlayer;

/**
 * Created by Sabri on 2/4/2017. Fragment to show Player profiles
 */

public class PlayersFragment extends Fragment {

    private static final int PLAYER_COLUMNS = 3;
    public static final String MATCH_ID = "match_id";

    //primary data
    public static int team_id = AppConfig.HOME_TEAM_ID;
    public static int year = Calendar.getInstance().get(Calendar.YEAR);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_players, container, false);

        DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(getContext()));
        dbManager.openDatabase();
        List<Object> players;
        if (getArguments() == null || !getArguments().containsKey(MATCH_ID)) {
            players = new ArrayList<Object>(PlayerPositionDAO.getPlayersForMatch(team_id));
            if (players.size() > 0) {
                int matchId = PlayerPositionDAO.getAvailableLastMatch();
                year = GroupDAO.getGroupForId(MatchDAO.getMatchForId(matchId).getGroup()).getYear();
            }
        } else {
            players = new ArrayList<Object>(PlayerPositionDAO.getAdapterPlayers(getArguments().getInt(MATCH_ID)));
            year = GroupDAO.getGroupForId(MatchDAO.getMatchForId(getArguments().getInt(MATCH_ID)).getGroup()).getYear();
        }
        players.addAll(StaffDAO.getAll());
        dbManager.closeDatabase();

        final List<Object> adapterPlayers = new ArrayList<>(players);

        RecyclerView recycler_fixture = view.findViewById(R.id.recycler_players);
        if (players.size() > 0) {
            view.findViewById(R.id.txt_no_items).setVisibility(View.GONE);
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), PLAYER_COLUMNS);
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (adapterPlayers.get(position) instanceof AdapterPlayer)
                        return 1;
                    else
                        return PLAYER_COLUMNS;
                }
            });
            recycler_fixture.setLayoutManager(layoutManager);
            recycler_fixture.setAdapter(new PlayerAdapter(getActivity(), players));
        } else
            recycler_fixture.setVisibility(View.GONE);

        return view;
    }
}
