package lk.rc07.ten_years.touchdown.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.config.Constant;
import lk.rc07.ten_years.touchdown.data.DBContact;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.GroupDAO;
import lk.rc07.ten_years.touchdown.data.PointsDAO;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
import lk.rc07.ten_years.touchdown.models.Points;
import lk.rc07.ten_years.touchdown.models.Team;
import lk.rc07.ten_years.touchdown.utils.AppHandler;

/**
 * Created by Sabri on 1/15/2017. Fragment for Standings
 */

public class StandingFragment extends Fragment {

    //constants
    private static final String SPINNER_TITLE_LEAGUE = "LEAGUE";
    private static final String SPINNER_TITLE_ROUND = "ROUND";
    private static final String SPINNER_TITLE_GROUP = "GROUP";
    private final float TEXT_SIZE = 18f;
    private static final String UPDATE_STRING = "Updated On : %s";
    private final String[] titles = new String[]{"TEAM", "Played", "Won", "Draw", "Lost", "Points"};

    //views
    private TableLayout table;
    private Spinner spn_leagues;
    private Spinner spn_groups;
    private Spinner spn_rounds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_standings, container, false);

        table = (TableLayout) view.findViewById(R.id.tableLayout1);
        TextView txt_update = (TextView) view.findViewById(R.id.txt_last_update);

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        Long dateLong = getContext().getSharedPreferences(Constant.MY_PREFERENCES,
                Context.MODE_PRIVATE).getLong(Constant.PREFERENCES_LAST_SYNC, 0);
        String dateString = dateFormat.format(dateLong);
        txt_update.setText(String.format(UPDATE_STRING, dateString));

        DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(getContext()));
        dbManager.openDatabase();

        setSpinners(view);
        setLeagueSpinners();

        dbManager.closeDatabase();

        return view;
    }

    private void setSpinners(View view) {
        spn_leagues = setSpinnerRow(view.findViewById(R.id.spn_league), SPINNER_TITLE_LEAGUE);
        spn_rounds = setSpinnerRow(view.findViewById(R.id.spn_rounds), SPINNER_TITLE_ROUND);
        spn_groups =setSpinnerRow(view.findViewById(R.id.spn_group), SPINNER_TITLE_GROUP);
    }

    private Spinner setSpinnerRow(View view, String title) {
        ((TextView) view.findViewById(R.id.spinner_title)).setText(title);
        return (Spinner) view.findViewById(R.id.spinner);
    }

    private void setLeagueSpinners() {
        final List<String> leagues = GroupDAO.getAllFromColumn(DBContact.GroupTable.COLUMN_LEAGUE);
        loadSpinner(leagues, spn_leagues);
        if (leagues.size() > 1)
            spn_leagues.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(getContext()));
                    dbManager.openDatabase();
                    setRoundSpinner(leagues.get(i));
                    dbManager.closeDatabase();
                }
            });
        else if (leagues.size() == 1) {
            setRoundSpinner(leagues.get(0));
        }
    }

    private void setRoundSpinner(final String league) {
        String where = DBContact.GroupTable.COLUMN_LEAGUE + " = '" + league + "'";
        final List<String> rounds = GroupDAO.getAllFromColumn(DBContact.GroupTable.COLUMN_ROUND, where);
        loadSpinner(rounds, spn_rounds);
        if (rounds.size() > 1)
            spn_rounds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(getContext()));
                    dbManager.openDatabase();
                    setGroupSpinner(league, rounds.get(i));
                    dbManager.closeDatabase();
                }
            });
        else if (rounds.size() == 1)
            setGroupSpinner(league, rounds.get(0));
    }

    private void setGroupSpinner(final String league, final String round) {
        String where = DBContact.GroupTable.COLUMN_LEAGUE + " = '" + league
                + "' and " +
                DBContact.GroupTable.COLUMN_ROUND + " = '" + round + "'";
        final List<String> groups = GroupDAO.getAllFromColumn(DBContact.GroupTable.COLUMN_NAME, where);
        loadSpinner(groups, spn_groups);
        if (groups.size() > 1)
            spn_groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(getContext()));
                    dbManager.openDatabase();
                    int id = GroupDAO.getGroupIdForInfo(league, round, groups.get(i));
                    loadTable(table, PointsDAO.getPointTable(id));
                    dbManager.closeDatabase();
                }
            });
        else if (groups.size() == 1) {
            int id = GroupDAO.getGroupIdForInfo(league, round, groups.get(0));
            loadTable(table, PointsDAO.getPointTable(id));
        }
    }

    private void loadSpinner(List<String> labels, Spinner spinner) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, labels);

        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    private void loadTable(TableLayout table, List<Points> points) {
        table.addView(addTitles());
        table.addView(addDivider());
        for (Points point : points) {
            table.addView(addRow(point));
        }
    }

    private TableRow addTitles() {
        TableRow tr1 = new TableRow(getContext());
        tr1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tr1.setPadding(0, 0, 0, 40);
        for (String title : titles) {
            tr1.addView(addTitleTextView(title));
        }
        return tr1;
    }

    private LinearLayout addDivider() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        linearLayout.setLayoutParams(params);
        linearLayout.setBackgroundColor(AppHandler.getColor(getContext(), R.color.tab_background));
        return linearLayout;
    }

    private TableRow addRow(Points points) {
        final TableRow tr1 = new TableRow(getContext());
        tr1.setPadding(30, 80, 0, 0);
        Team team = TeamDAO.getTeam(points.getTeamId());
        boolean isRoyal = (team.getIdTeam() == AppConfig.HOME_TEAM_ID);
        tr1.addView(addTextView(team.getName(), isRoyal, Gravity.START));
        tr1.addView(addTextView(String.valueOf(points.getPlayed()), isRoyal, Gravity.CENTER_HORIZONTAL));
        tr1.addView(addTextView(String.valueOf(points.getWon()), isRoyal, Gravity.CENTER_HORIZONTAL));
        tr1.addView(addTextView(String.valueOf(points.getPlayed() - (points.getWon() + points.getLost())), isRoyal, Gravity.CENTER_HORIZONTAL));
        tr1.addView(addTextView(String.valueOf(points.getLost()), isRoyal, Gravity.CENTER_HORIZONTAL));
        tr1.addView(addTextView(String.valueOf(points.getPoints()), isRoyal, Gravity.CENTER_HORIZONTAL));

        return tr1;
    }

    private TextView addTextView(String data, boolean isRoyal, int gravity) {
        TextView textview = new TextView(getContext());
        textview.setText(data);
        if (isRoyal)
            textview.setTextColor(AppHandler.getColor(getContext(), R.color.text_row_royal));
        else
            textview.setTextColor(Color.WHITE);
        textview.setGravity(gravity);
        textview.setTextSize(TEXT_SIZE);
        return textview;
    }

    private TextView addTitleTextView(String data) {
        TextView textview = new TextView(getContext());
        textview.setText(data);
        textview.setTextColor(AppHandler.getColor(getContext(), R.color.text_title));
        textview.setGravity(Gravity.CENTER_HORIZONTAL);
        textview.setTextSize(TEXT_SIZE);
        return textview;
    }
}
