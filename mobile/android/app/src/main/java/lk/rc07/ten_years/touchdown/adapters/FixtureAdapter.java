package lk.rc07.ten_years.touchdown.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.activities.MatchSummaryActivity;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.GroupDAO;
import lk.rc07.ten_years.touchdown.data.MatchDAO;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
import lk.rc07.ten_years.touchdown.models.Group;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Team;
import lk.rc07.ten_years.touchdown.utils.AppHandler;

/**
 * Created by Sabri on 1/14/2017. Adapter to fixture view
 */

public class FixtureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //constants
    private static final int VIEW_PENDING = 1;
    private static final int VIEW_RESULT = 2;
    private static final String LAST_RESULT = "Last match : %s";
    private static final String NEW_RESULT = " RC -<span style=\"color:#F2C311\"> %d : %d </span>- %s";
    private static final String RESULT_SUMMARY = " %s won by %d points.";
    private static final String RESULT_PROGRESS = " %s leading by %d points.";
    private static final String TO_BE_ANNOUNCED = "TBA";

    //instances
    private Context context;
    private List<Match> matches;
    private DBManager dbManager;

    public FixtureAdapter(Context context, List<Match> matches) {
        this.context = context;
        this.matches = matches;

        dbManager = DBManager.initializeInstance(DBHelper.getInstance(context));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_PENDING) {
            view = LayoutInflater.from(context).inflate(R.layout.component_fixture_row, parent, false);
            return new FixtureAdapter.PendingViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.component_fixture_result_row, parent, false);
            return new FixtureAdapter.ResultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final Match match = matches.get(position);

        Date date = null;
        if (match.getMatchDate() != 0 && match.getStatus() != Match.Status.TBA)
            date = new Date(match.getMatchDate());

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String opponentTeam;

        ImageView img_crest;
        if (viewHolder instanceof PendingViewHolder) {
            PendingViewHolder holder = (PendingViewHolder) viewHolder;

            dbManager.openDatabase();
            opponentTeam = getTeamName(match);
            holder.txt_team.setText(opponentTeam);
            Group group = GroupDAO.getGroupForId(match.getGroup());
            Match lastMatch = MatchDAO.getMatchForTheYear(group.getYear() - 1, match.getTeamOne(), match.getTeamTwo());
            dbManager.closeDatabase();

            if (date != null) {
                holder.txt_date.setText(AppHandler.getLinkText(dateFormat.format(date)));
                dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                holder.txt_time.setText(dateFormat.format(date));
            } else {
                holder.txt_date.setText(TO_BE_ANNOUNCED);
                holder.txt_time.setText("");
            }

            holder.txt_venue.setText(AppHandler.getLinkText(match.getVenue()));
            if (match.getLastMatch() != null && !match.getLastMatch().equals(""))
                holder.txt_last.setText(String.format(LAST_RESULT, match.getLastMatch()));
            else if (lastMatch != null) {
                holder.txt_last.setText(String.format(LAST_RESULT, lastMatch.getResult()));
            }

            holder.txt_venue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", match.getLatitude(), match.getLongitude(),
                                match.getLatitude(), match.getLongitude(), match.getVenue());
                        Uri gmmIntentUri = Uri.parse(uri);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        context.startActivity(mapIntent);
                    } catch (ActivityNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            holder.txt_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createEvent(match);
                }
            });

            img_crest = holder.img_crest;
        } else {
            ResultViewHolder holder = (ResultViewHolder) viewHolder;

            dbManager.openDatabase();
            int homeTotal;
            int opponentTotal;
            if (match.getTeamOne() == AppConfig.HOME_TEAM_ID) {
                homeTotal = getTeamTotal(match.getIdmatch(), match.getTeamOne());
                opponentTotal = getTeamTotal(match.getIdmatch(), match.getTeamTwo());
                opponentTeam = getTeamShortName(match.getTeamTwo());
            } else {
                homeTotal = getTeamTotal(match.getIdmatch(), match.getTeamTwo());
                opponentTotal = getTeamTotal(match.getIdmatch(), match.getTeamOne());
                opponentTeam = getTeamShortName(match.getTeamOne());
            }

            holder.txt_team.setText(getTeamName(match));
            if (date != null)
                holder.txt_date.setText(dateFormat.format(date));
            else
                holder.txt_date.setText("");
            holder.txt_time.setText(match.getStatus().toStringValue());
//            if (match.getResult() != null)
//                holder.txt_final.setText(match.getResult());
//            else
            if (match.getStatus() == Match.Status.FULL_TIME || match.getStatus() == Match.Status.DONE)
                holder.txt_final.setText(getMatchResult(opponentTeam, homeTotal, opponentTotal, RESULT_SUMMARY));
            else
                holder.txt_final.setText(getMatchResult(opponentTeam, homeTotal, opponentTotal, RESULT_PROGRESS));


            holder.txt_result.setText(getResultString(opponentTeam, homeTotal, opponentTotal));
            dbManager.closeDatabase();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MatchSummaryActivity.class);
                    intent.putExtra(MatchSummaryActivity.EXTRA_MATCH_OBJECT, match);
                    context.startActivity(intent);
                }
            });

            img_crest = holder.img_crest;
        }

        Glide.with(context).load(getOpponentCrest(match.getTeamTwo())).placeholder(R.drawable
                .icon_book_placeholder).into(img_crest);
    }

    private String getMatchResult(String opponentTeam, int homeTotal, int opponentTotal, String message) {
        return String.format(Locale.getDefault(), message, homeTotal > opponentTotal ?
                "Royal" : opponentTeam, (homeTotal > opponentTotal) ? (homeTotal - opponentTotal) : (opponentTotal - homeTotal));
    }

    private String getTeamName(Match match) {
        int teamId;
        if (match != null) {
            if (match.getTeamOne() == AppConfig.HOME_TEAM_ID)
                teamId = match.getTeamTwo();
            else
                teamId = match.getTeamOne();

            Team team = TeamDAO.getTeam(teamId);

            return team.getName();
        } else
            return "";
    }

    private Spanned getResultString(String opponentTeam, int homeTotal, int opponentTotal) {
        return AppHandler.getHtmlString(String.format(Locale.getDefault(), NEW_RESULT, homeTotal, opponentTotal, opponentTeam));
    }

    private int getTeamTotal(int matchId, int teamId) {
        return ScoreDAO.getTotalScore(matchId, teamId);
    }

    private String getTeamShortName(int teamId) {
        StringBuilder shortName = new StringBuilder();
        String name = TeamDAO.getTeam(teamId).getName();
        for (String part : name.split(" ")) {
            shortName.append(part.charAt(0));
        }
        return shortName.toString();
    }

    private void createEvent(Match match) {
        dbManager.openDatabase();

        Team tOne = TeamDAO.getTeam(match.getTeamOne());
        Team tTwo = TeamDAO.getTeam(match.getTeamTwo());
        Group group = GroupDAO.getGroupForId(match.getGroup());

        dbManager.closeDatabase();

        Calendar matchDate = Calendar.getInstance();
        matchDate.setTime(new Date(match.getMatchDate()));
//        matchDate.set(Calendar.HOUR, 15);

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, tOne.getName() + " vs " + tTwo.getName());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, match.getVenue());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, group.getLeagueName() + " - " + group.getRoundName()
                + " - " + group.getGroupName());

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                matchDate.getTimeInMillis());

        matchDate.set(Calendar.HOUR, 6);

        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                matchDate.getTimeInMillis());

        intent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        context.startActivity(intent);

    }

    private String getOpponentCrest(int id) {
        dbManager.openDatabase();
        Team team = TeamDAO.getTeam(id);
        dbManager.closeDatabase();
        if (team != null)
            return team.getLogo_url();
        else
            return "";
    }

    @Override
    public int getItemCount() {
        if (matches != null)
            return matches.size();
        else
            return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (matches.get(position).getStatus() != Match.Status.PENDING && matches.get(position).getStatus()
                != Match.Status.TBA && matches.get(position).getStatus() != Match.Status.CALLED_OFF &&
                matches.get(position).getStatus() != Match.Status.CANCELED && matches.get(position).getStatus()
                != Match.Status.CANCELLED)
            return VIEW_RESULT;
        else
            return VIEW_PENDING;
    }

    private class PendingViewHolder extends RecyclerView.ViewHolder {

        TextView txt_team;
        TextView txt_date;
        TextView txt_time;
        TextView txt_venue;
        TextView txt_last;

        ImageView img_crest;

        PendingViewHolder(View itemView) {
            super(itemView);
            txt_team = itemView.findViewById(R.id.txt_fixture_team_name);
            txt_date = itemView.findViewById(R.id.txt_fixture_date);
            txt_time = itemView.findViewById(R.id.txt_fixture_time);
            txt_venue = itemView.findViewById(R.id.txt_fixture_venue);
            txt_last = itemView.findViewById(R.id.txt_fixture_last_match);

            img_crest = itemView.findViewById(R.id.img_fixture_college_crest);
        }
    }

    private class ResultViewHolder extends RecyclerView.ViewHolder {

        TextView txt_team;
        TextView txt_result;
        TextView txt_time;
        TextView txt_date;
        TextView txt_final;

        ImageView img_crest;

        ResultViewHolder(View itemView) {
            super(itemView);
            txt_team = itemView.findViewById(R.id.txt_fixture_team_name);
            txt_result = itemView.findViewById(R.id.txt_fixture_score);
            txt_time = itemView.findViewById(R.id.txt_fixture_time);
            txt_date = itemView.findViewById(R.id.txt_fixture_date);
            txt_final = itemView.findViewById(R.id.txt_fixture_final);

            img_crest = itemView.findViewById(R.id.img_fixture_college_crest);
        }
    }
}
