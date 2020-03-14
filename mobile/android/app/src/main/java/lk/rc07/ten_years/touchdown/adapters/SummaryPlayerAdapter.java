package lk.rc07.ten_years.touchdown.adapters;

import android.content.Context;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.BuildConfig;
import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.fragments.SummaryFragment;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.models.Scorer;
import lk.rc07.ten_years.touchdown.utils.AppHandler;

/**
 * Created by Sabri on 3/25/2017. Player adapter for Summary view
 */

public class SummaryPlayerAdapter extends RecyclerView.Adapter<SummaryPlayerAdapter.ViewHolder> {

    //instances
    private Context context;
    private List<Scorer> players;
    private LayoutInflater layoutInflater;

    //primary data
    private int recycler_height = 0;

    public SummaryPlayerAdapter(Context context, List<Scorer> players) {
        this.context = context;
        this.players = players;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public SummaryPlayerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.component_summary_player_row, parent, false);
        return new SummaryPlayerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Scorer scorer = players.get(position);

        final ViewHolder finaHolder = holder;
        holder.itemView.post(new Runnable() {
            @Override
            public void run() {
                int height = (finaHolder.itemView.getHeight() * getItemCount());
                if (recycler_height != height) {
                    recycler_height = height;
                    Message msg = new Message();
                    msg.arg1 = height;
                    if (scorer.getScores().get(0).getActionType() == Score.WHAT_ACTION_SCORE)
                        msg.what = SummaryFragment.WHAT_SCORER_SIZE;
                    else
                        msg.what = SummaryFragment.WHAT_PENALIZED_SIZE;
                    SummaryFragment.handler.sendMessage(msg);
                }
            }
        });

        String img_link =scorer.getPlayer().getImg_url();
        if (!img_link.equals("") && !img_link.equals("/contents/players/")) {
            img_link = BuildConfig.DEFAULT_URL + img_link;
            Glide.with(context).load(img_link).placeholder(R.drawable.default_profile_pic)
                    .into(holder.img_player);
        }

        holder.txt_name.setText(scorer.getPlayer().getName());

        createScoreView(scorer.getScores(), holder);
    }

    private void createScoreView(List<Score> scores, ViewHolder holder) {
        HashMap<Score.Action, Integer> items = new HashMap<>();
        for (Score score : scores) {
            if (items.containsKey(score.getAction())) {
                //noinspection ConstantConditions - Checked it above
                int count = items.get(score.getAction());
                items.put(score.getAction(), ++count);
            } else
                items.put(score.getAction(), 1);
        }

        for (Score.Action action : items.keySet()) {
            View view = layoutInflater.inflate(R.layout.component_score_row, holder.layout_score, false);
            ((ImageView) view.findViewById(R.id.img_score_icon)).setImageDrawable(
                    AppHandler.getDrawable(context, getScoreIcon(action)));
            ((TextView) view.findViewById(R.id.txt_tries_stat)).setText(String.format(Locale.getDefault(), "%02d", items.get(action)));
            holder.layout_score.addView(view);
        }
    }

    @Override
    public int getItemCount() {
        if (players != null)
            return players.size();
        else
            return 0;
    }

    private int getScoreIcon(Score.Action action) {
        switch (action) {
            case TRY:
                return R.mipmap.score_try;
            case DROP_GOAL:
                return R.mipmap.score_drop_goal;
            case PENALTY_KICK:
                return R.mipmap.score_penalty;
            case CONVERSION:
                return R.mipmap.score_conversion;
            case YELLOW_CARD:
                return R.mipmap.yellow_card;
            case RED_CARD:
                return R.mipmap.red_card;
        }
        return R.mipmap.score_try;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_player;
        private TextView txt_name;
        private LinearLayout layout_score;

        ViewHolder(View view) {
            super(view);
            img_player = view.findViewById(R.id.img_summary_player);
            txt_name = view.findViewById(R.id.txt_summary_player_name);
            layout_score = view.findViewById(R.id.layout_score_row);
        }
    }
}
