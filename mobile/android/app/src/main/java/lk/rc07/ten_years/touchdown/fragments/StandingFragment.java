package lk.rc07.ten_years.touchdown.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.config.Constant;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
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
    private final float TEXT_SIZE = 18f;
    private static final String UPDATE_STRING = "Updated On : %s";
    private final String[] titles = new String[]{"TEAM", "Played", "Won", "Draw", "Lost", "Points"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_standings, container, false);

        TableLayout table = (TableLayout) view.findViewById(R.id.tableLayout1);
        TextView txt_update = (TextView) view.findViewById(R.id.txt_last_update);

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String dateString = dateFormat.format(getContext().getSharedPreferences(Constant.MY_PREFERENCES,
                Context.MODE_PRIVATE).getLong(Constant.PREFERENCES_LAST_SYNC, 0));
        txt_update.setText(String.format(UPDATE_STRING, dateString));

        DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(getContext()));
        dbManager.openDatabase();
        loadTable(table, PointsDAO.getPointTable());
        dbManager.closeDatabase();

        return view;
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
        Team team = TeamDAO.getTeam(points.getTeamId());
        boolean isRoyal = (team.getIdTeam() == 1);
        tr1.addView(addTextView(team.getName(), isRoyal));
        tr1.addView(addTextView(String.valueOf(points.getPlayed()), isRoyal));
        tr1.addView(addTextView(String.valueOf(points.getWon()), isRoyal));
        tr1.addView(addTextView(String.valueOf(points.getPlayed() - (points.getWon() + points.getLost())), isRoyal));
        tr1.addView(addTextView(String.valueOf(points.getLost()), isRoyal));
        tr1.addView(addTextView(String.valueOf(points.getPoints()), isRoyal));

        return tr1;
    }

    private TextView addTextView(String data, boolean isRoyal) {
        TextView textview = new TextView(getContext());
        textview.setText(data);
        if (isRoyal)
            textview.setTextColor(AppHandler.getColor(getContext(), R.color.text_row_royal));
        else
            textview.setTextColor(Color.WHITE);
        textview.setGravity(Gravity.CENTER_HORIZONTAL);
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
