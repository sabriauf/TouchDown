package lk.rc07.ten_years.touchdown.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.PlayerDAO;
import lk.rc07.ten_years.touchdown.data.PlayerPositionDAO;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Player;
import lk.rc07.ten_years.touchdown.models.Position;
import lk.rc07.ten_years.touchdown.models.Score;

/**
 * Created by Sabri on 12/24/2016. Timeline adapter for score
 */

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {

    //constants
    private static final String PLAYER_NO = "%d'";
    //instances
    private Context context;
    private List<Score> scores;
    private DBManager dbManager;
    private Match match;

    public ScoreAdapter(Context context, List<Score> scores, Match match) {
        this.context = context;
        this.scores = scores;
        this.match = match;

        dbManager = DBManager.initializeInstance(DBHelper.getInstance(context));
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.component_score_timeline_row, parent, false);
        return new ScoreAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Score score = scores.get(position);
        String playerName;
        int playerNo = 0;
        if(score.getPlayer() != 0) {
            AdapterPlayer aPlayer = getPlayer(score.getPlayer());
            playerName = aPlayer.player.getName();
            playerNo = aPlayer.position.getPosNo();
        } else {
            playerName = getTeamName(score.getTeamId());
        }

        holder.txt_score_type.setText(String.valueOf(String.valueOf(score.getAction()).charAt(0)));
        if(score.getTeamId() == match.getTeamOne()) {
            holder.txt_player_no_left.setText(String.format(Locale.getDefault(), PLAYER_NO, playerNo));
            holder.txt_player_name_left.setText(playerName);
            setVisibility(holder, true);
        } else {
            holder.txt_player_no_right.setText(String.format(Locale.getDefault(), PLAYER_NO, playerNo));
            holder.txt_player_name_right.setText(playerName);
            setVisibility(holder, false);
        }
    }

    @Override
    public int getItemCount() {
        if(scores != null)
            return scores.size();
        else
            return 0;
    }

    private void setVisibility(ViewHolder holder, boolean isHome) {
        if(isHome) {
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
        AdapterPlayer aPlayer = new AdapterPlayer();

        dbManager.openDatabase();
        aPlayer.player = PlayerDAO.getPlayer(playerId);
        aPlayer.position = PlayerPositionDAO.getPosition(playerId, match.getIdmatch());
        dbManager.closeDatabase();

        return aPlayer;
    }

    private String getTeamName(int teamId) {
        dbManager.openDatabase();
        String team = TeamDAO.getTeam(teamId).getName();
        dbManager.closeDatabase();

        return team;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_score_type;
        TextView txt_player_no_left;
        TextView txt_player_name_left;
        TextView txt_player_no_right;
        TextView txt_player_name_right;

        ViewHolder(View itemView) {
            super(itemView);
            txt_score_type = (TextView) itemView.findViewById(R.id.txt_score_tag);
            txt_player_no_left = (TextView) itemView.findViewById(R.id.txt_player_no_left);
            txt_player_name_left = (TextView) itemView.findViewById(R.id.txt_player_name_left);
            txt_player_no_right = (TextView) itemView.findViewById(R.id.txt_player_no_right);
            txt_player_name_right = (TextView) itemView.findViewById(R.id.txt_player_name_right);
        }
    }

    public void addItem(Score score) {
        scores.add(score);
        notifyItemInserted(scores.size() - 1);
    }

    public void updateItem(Score score) {
        for (int i=0; i< scores.size(); i++) {
            Score score1 =  scores.get(i);
            if(score1.getIdscore() == score.getIdscore()) {
                scores.set(i, score);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void removeItem(Score score) {
        for (int i=0; i< scores.size(); i++) {
            Score score1 =  scores.get(i);
            if(score1.getIdscore() == score.getIdscore()) {
                scores.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    private class AdapterPlayer {
        Player player;
        Position position;
    }
}
