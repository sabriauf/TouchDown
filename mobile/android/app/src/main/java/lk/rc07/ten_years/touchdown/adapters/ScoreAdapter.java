package lk.rc07.ten_years.touchdown.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.BuildConfig;
import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.PlayerDAO;
import lk.rc07.ten_years.touchdown.data.PlayerPositionDAO;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
import lk.rc07.ten_years.touchdown.models.AdapterPlayer;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Player;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.models.Team;
import lk.rc07.ten_years.touchdown.utils.AppHandler;
import lk.rc07.ten_years.touchdown.utils.TimeFormatter;

/**
 * Created by Sabri on 12/24/2016. Timeline adapter for score
 */

public class ScoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //constants
//    private static final String EXIT = "Exit";
    private static final int SCORE_VIEW_SCORE = 1;
    private static final int SCORE_VIEW_TOPIC = 2;
    private static final int SCORE_VIEW_MESSAGE = 3;
    private static final String EVENT_ROW = " - %s";
    private static final String CARD_MESSAGE = " to %s";

    //instances
    private Context context;
    private List<Score> scores;
    private DBManager dbManager;
    private Match match;

    //primary data
    private long firstHalfTime = 0;
    private long secondHalfTime = 0;

    public ScoreAdapter(Context context, List<Score> scores, Match match) {
        this.context = context;
        this.scores = scores;
        this.match = match;

        dbManager = DBManager.initializeInstance(DBHelper.getInstance(context));

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case SCORE_VIEW_SCORE:
                view = LayoutInflater.from(context).inflate(R.layout.component_score_timeline_row, parent, false);
                return new ScoreAdapter.ScoreViewHolder(view);
            case SCORE_VIEW_TOPIC:
                view = LayoutInflater.from(context).inflate(R.layout.component_score_topic_row, parent, false);
                return new ScoreAdapter.TitleViewHolder(view);
            case SCORE_VIEW_MESSAGE:
                view = LayoutInflater.from(context).inflate(R.layout.component_score_message, parent, false);
                return new ScoreAdapter.MessageViewHolder(view);
        }
        view = LayoutInflater.from(context).inflate(R.layout.component_score_topic_row, parent, false);
        return new ScoreAdapter.TitleViewHolder(view);
    }

    private long getOffSetTime(long time) {
        setGameTimes();

        if (time > secondHalfTime && secondHalfTime != 0) {
            return time - secondHalfTime + AppConfig.SECOND_HALF_START_TIME;
        } else if (firstHalfTime != 0) {
            return time - firstHalfTime;
        } else
            return 0;
    }

    private void setGameTimes() {
        if (firstHalfTime == 0 || secondHalfTime == 0) {
            dbManager.openDatabase();
            if (firstHalfTime == 0)
                firstHalfTime = getGamePoint(Score.Action.START);
            if (secondHalfTime == 0)
                secondHalfTime = getGamePoint(Score.Action.SECOND_HALF);
//            if (firstHalfTime != 0 && secondHalfTime != 0 && SECOND_HALF_START_TIME == 0) {
//                long firstHalfEnd = getGamePoint(Score.Action.HALF_TIME);
//                SECOND_HALF_START_TIME = secondHalfTime - firstHalfEnd;
//            }
            dbManager.closeDatabase();
        }
    }

    private long getGamePoint(Score.Action point) {
        List<Score> startScores = ScoreDAO.getActionScore(match.getIdmatch(), point);
        if (startScores.size() > 0)
            return startScores.get(0).getTime();
        else
            return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Score score = scores.get(position);

        String playerName = "";
        if (score.getPlayer() != 0) {
            AdapterPlayer aPlayer = getPlayer(score.getPlayer());
            if (aPlayer.getPlayer() != null)
                playerName = aPlayer.getPlayer().getName();
        } else {
            if (score.getTeamId() != 0) {
                playerName = getTeamName(score.getTeamId());
            }
        }
        final String time = TimeFormatter.getGameTimeString(getOffSetTime(score.getTime()));

        if (holder instanceof ScoreViewHolder) {
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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createShowDetails(score, time);
                }
            });

        } else if (holder instanceof TitleViewHolder) {
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            titleViewHolder.txt_title.setText(score.getActionString());

            GradientDrawable shapeColor = (GradientDrawable) titleViewHolder.txt_title.getBackground();
            if (score.getAction() == Score.Action.START || score.getAction() == Score.Action.SECOND_HALF)
                shapeColor.setColor(AppHandler.getColor(context, R.color.timer_clock));
            else
                shapeColor.setColor(AppHandler.getColor(context, R.color.live_back));
        } else {
            MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
            String message = getMessageString(score, playerName);
            messageViewHolder.txt_time.setText(time);

            if(score.getAction() == Score.Action.RED_CARD) {
                messageViewHolder.img_score.setVisibility(View.VISIBLE);
                messageViewHolder.img_score.setImageDrawable(AppHandler.getDrawable(context, R.mipmap.red_card));
                messageViewHolder.txt_title.setText(message);
            } else if(score.getAction() == Score.Action.YELLOW_CARD) {
                messageViewHolder.img_score.setVisibility(View.VISIBLE);
                messageViewHolder.img_score.setImageDrawable(AppHandler.getDrawable(context, R.mipmap.yellow_card));
                messageViewHolder.txt_title.setText(message);
            } else {
                messageViewHolder.img_score.setVisibility(View.GONE);
                messageViewHolder.txt_title.setText(String.format(Locale.getDefault(), EVENT_ROW, message));
            }
        }
    }

    private String getMessageString(Score score, String name) {
        switch (score.getAction()) {
            case YELLOW_CARD:
            case RED_CARD:
                return String.format(Locale.getDefault(), CARD_MESSAGE, name);
            case MESSAGE:
                return score.getDetails();
            case LINE_OUT:
                return score.getActionString() + " to " + name;
            default:
                if (score.getDetails() != null && !score.getDetails().equals(""))
                    return score.getDetails();
                else
                    return score.getActionString() + " conceded by " + name;
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
            case START:
            case SECOND_HALF:
                return SCORE_VIEW_TOPIC;
            case KNOCK_ON:
            case PENALTY:
            case RED_CARD:
            case SCRUM:
            case YELLOW_CARD:
            case MESSAGE:
                return SCORE_VIEW_MESSAGE;
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
        aPlayer = PlayerPositionDAO.getAdapterPlayer(playerId, match.getIdmatch());
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

        RelativeLayout main_layout;
        TextView txt_score_type;
        TextView txt_player_no_left;
        TextView txt_player_name_left;
        TextView txt_player_no_right;
        TextView txt_player_name_right;

        ScoreViewHolder(View itemView) {
            super(itemView);
            main_layout = itemView.findViewById(R.id.main_layout);
            txt_score_type = itemView.findViewById(R.id.txt_score_tag);
            txt_player_no_left = itemView.findViewById(R.id.txt_player_no_left);
            txt_player_name_left = itemView.findViewById(R.id.txt_player_name_left);
            txt_player_no_right = itemView.findViewById(R.id.txt_player_no_right);
            txt_player_name_right =  itemView.findViewById(R.id.txt_player_name_right);
        }
    }

    private class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView txt_title;

        TitleViewHolder(View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_score_tag);
        }
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView txt_title;
        TextView txt_time;
        ImageView img_score;

        MessageViewHolder(View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_score_tag);
            txt_time = itemView.findViewById(R.id.txt_score_time);
            img_score = itemView.findViewById(R.id.img_score);
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

    private void createShowDetails(Score score, String time) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alert_score_details);

        ImageView img_profile = dialog.findViewById(R.id.img_alert_score_player);
        ImageView img_score = dialog.findViewById(R.id.img_alert_score);
        TextView txt_score = dialog.findViewById(R.id.txt_alert_score_title);
        TextView txt_time = dialog.findViewById(R.id.txt_alert_score_time);
        TextView txt_team = dialog.findViewById(R.id.txt_alert_score_team);
        TextView txt_player = dialog.findViewById(R.id.txt_alert_score_player);
        TextView txt_details = dialog.findViewById(R.id.txt_alert_score_details);

        dbManager.openDatabase();
        Team team = TeamDAO.getTeam(score.getTeamId());
        Player player = PlayerDAO.getPlayer(score.getPlayer());
        dbManager.closeDatabase();

        if (player != null && !player.getImg_url().equals("")) {
            Glide.with(context).load(BuildConfig.DEFAULT_URL + player.getImg_url())
                    .placeholder(R.drawable.default_profile_pic).into(img_profile);
            txt_player.setText(player.getName());
        } else {
            Glide.with(context).load(BuildConfig.DEFAULT_URL + team.getLogo_url())
                    .placeholder(R.drawable.default_profile_pic).into(img_profile);
            txt_player.setVisibility(View.GONE);
        }

        img_score.setImageDrawable(AppHandler.getDrawable(context, getScoreImage(score.getAction())));

        txt_score.setText(score.getActionString());
        txt_time.setText(time);
        txt_team.setText(team.getName());
        txt_details.setText(score.getDetails());

        dialog.show();
    }

    private int getScoreImage(Score.Action action) {
        switch (action) {
            case TRY:
                return R.mipmap.score_try;
            case CONVERSION:
                return R.mipmap.score_conversion;
            case DROP_GOAL:
                return R.mipmap.score_drop_goal;
            case PENALTY_KICK:
                return R.mipmap.score_penalty;
        }
        return R.mipmap.score_try;
    }
}
