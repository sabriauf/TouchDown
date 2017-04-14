package lk.rc07.ten_years.touchdown.adapters;

import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.config.AppConfig;
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
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private LayoutInflater layoutInflater;

    //primary data
    private int recycler_height = 0;

    public SummaryPlayerAdapter(Context context, List<Scorer> players) {
        this.context = context;
        this.players = players;

        this.imageLoader = ImageLoader.getInstance();
        options = AppHandler.getImageOption(imageLoader, context, R.drawable.default_profile_pic);

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public SummaryPlayerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

        ImageAware imgAware = new ImageViewAware(holder.img_player);
        String img_link =scorer.getPlayer().getImg_url();
        if (!img_link.equals("") && !img_link.equals("/contents/players/")) {
            img_link = AppConfig.TOUCHDOWN_BASE_URL + img_link;
            imageLoader.displayImage(img_link, imgAware, options);
        }

        holder.txt_name.setText(scorer.getPlayer().getName());

        createScoreView(scorer.getScores(), holder);
    }

    private void createScoreView(List<Score> scores, ViewHolder holder) {
        HashMap<Score.Action, Integer> items = new HashMap<>();
        for (Score score : scores) {
            if (items.containsKey(score.getAction())) {
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
            img_player = (ImageView) view.findViewById(R.id.img_summary_player);
            txt_name = (TextView) view.findViewById(R.id.txt_summary_player_name);
            layout_score = (LinearLayout) view.findViewById(R.id.layout_score_row);
        }
    }
}
