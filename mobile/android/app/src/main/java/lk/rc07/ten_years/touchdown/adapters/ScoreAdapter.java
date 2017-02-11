package lk.rc07.ten_years.touchdown.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.PlayerDAO;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
import lk.rc07.ten_years.touchdown.models.AdapterPlayer;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.utils.TimeFormatter;

/**
 * Created by Sabri on 12/24/2016. Timeline adapter for score
 */

public class ScoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //constants
//    private static final String PLAYER_NO = "%s'";
    private static final int SCORE_VIEW_SCORE = 1;
    private static final int SCORE_VIEW_TOPIC = 2;
    //instances
    private Context context;
    private List<Score> scores;
    private DBManager dbManager;
    private Match match;

    //primary data
    private long startTime;

    public ScoreAdapter(Context context, List<Score> scores, Match match) {
        this.context = context;
        this.scores = scores;
        this.match = match;

        dbManager = DBManager.initializeInstance(DBHelper.getInstance(context));
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case SCORE_VIEW_SCORE:
                view = LayoutInflater.from(context).inflate(R.layout.component_score_timeline_row, parent, false);
                return new ScoreAdapter.ScoreViewHolder(view);
            case SCORE_VIEW_TOPIC:
                view = LayoutInflater.from(context).inflate(R.layout.component_score_topic_row, parent, false);
                return new ScoreAdapter.TitleViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Score score = scores.get(position);

        if (score.getAction() == Score.Action.START)
            startTime = score.getTime();

        String playerName;
        if (score.getPlayer() != 0) {
            AdapterPlayer aPlayer = getPlayer(score.getPlayer());
            playerName = aPlayer.getPlayer().getName();
        } else {
            playerName = getTeamName(score.getTeamId());
        }
        String time = TimeFormatter.millisToGameTime(context, startTime, score.getTime());

        if(holder instanceof ScoreViewHolder) {
            ScoreViewHolder scoreHolder = (ScoreViewHolder) holder;
            scoreHolder.txt_score_type.setText(String.valueOf(String.valueOf(score.getAction()).charAt(0)));
            if (score.getTeamId() == match.getTeamOne()) {
                scoreHolder.txt_player_no_left.setText(time);
                scoreHolder.txt_player_name_left.setText(playerName);
                setVisibility(scoreHolder, true);
            } else {
                scoreHolder.txt_player_no_right.setText(time);
                scoreHolder.txt_player_name_right.setText(playerName);
                setVisibility(scoreHolder, false);
            }
        } else {
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            titleViewHolder.txt_title.setText(score.getActionString());
        }
    }

    @Override
    public int getItemCount() {
        if (scores != null)
            return scores.size();
        else
            return 0;
    }

    @Override
    public int getItemViewType(int position) {

        Score score = scores.get(position);

        switch (score.getAction()) {
            case FULL_TIME:
            case HALF_TIME:
            case KNOCK_ON:
            case PENALTY:
            case RED_CARD:
            case SCRUM:
            case YELLOW_CARD:
            case START:
            case SECOND_HALF:
                return SCORE_VIEW_TOPIC;
        }
        return SCORE_VIEW_SCORE;
    }

    private void setVisibility(ScoreViewHolder holder, boolean isHome) {
        if (isHome) {
            holder.txt_player_name_right.setVisibility(View.GONE);
            holder.txt_player_no_right.setVisibility(View.GONE);
            holder.txt_player_name_left.setVisibility(View.VISIBLE);
            holder.txt_player_no_left.setVisibility(View.VISIBLE);
        } else {
            holder.txt_player_name_right.setVisibility(View.VISIBLE);
            holder.txt_player_no_right.setVisibility(View.VISIBLE);
            holder.txt_player_name_left.setVisibility(View.GONE);
            holder.txt_player_no_left.setVisibility(View.GONE);
        }
    }

    private AdapterPlayer getPlayer(int playerId) {
        AdapterPlayer aPlayer;

        dbManager.openDatabase();
        aPlayer = PlayerDAO.getAdapterPlayer(playerId, match.getIdmatch());
        dbManager.closeDatabase();

        return aPlayer;
    }

    private String getTeamName(int teamId) {
        dbManager.openDatabase();
        String team = TeamDAO.getTeam(teamId).getName();
        dbManager.closeDatabase();

        return team;
    }

    private class ScoreViewHolder extends RecyclerView.ViewHolder {

        TextView txt_score_type;
        TextView txt_player_no_left;
        TextView txt_player_name_left;
        TextView txt_player_no_right;
        TextView txt_player_name_right;

        ScoreViewHolder(View itemView) {
            super(itemView);
            txt_score_type = (TextView) itemView.findViewById(R.id.txt_score_tag);
            txt_player_no_left = (TextView) itemView.findViewById(R.id.txt_player_no_left);
            txt_player_name_left = (TextView) itemView.findViewById(R.id.txt_player_name_left);
            txt_player_no_right = (TextView) itemView.findViewById(R.id.txt_player_no_right);
            txt_player_name_right = (TextView) itemView.findViewById(R.id.txt_player_name_right);
        }
    }

    private class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView txt_title;

        TitleViewHolder(View itemView) {
            super(itemView);
            txt_title = (TextView) itemView.findViewById(R.id.txt_score_tag);
        }
    }

    public void addItem(Score score) {
        scores.add(score);
        notifyItemInserted(scores.size() - 1);
    }

    public void updateItem(Score score) {
        for (int i = 0; i < scores.size(); i++) {
            Score score1 = scores.get(i);
            if (score1.getIdscore() == score.getIdscore()) {
                scores.set(i, score);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void removeItem(Score score) {
        for (int i = 0; i < scores.size(); i++) {
            Score score1 = scores.get(i);
            if (score1.getIdscore() == score.getIdscore()) {
                scores.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public List<Score> getScores() {
        return scores;
    }
}
