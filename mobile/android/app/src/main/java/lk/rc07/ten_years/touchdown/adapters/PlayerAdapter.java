package lk.rc07.ten_years.touchdown.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.activities.PlayerDialogActivity;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.models.AdapterPlayer;
import lk.rc07.ten_years.touchdown.utils.AppHandler;

/**
 * Created by Sabri on 2/4/2017. Adapter for players
 */

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    //instances
    private Activity activity;
    private List<AdapterPlayer> players;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public PlayerAdapter(Activity activity, List<AdapterPlayer> players) {
        this.activity = activity;
        this.players = players;

        this.imageLoader = ImageLoader.getInstance();
        options = AppHandler.getImageOption(imageLoader, activity, R.drawable.default_profile_pic);
    }

    @Override
    public PlayerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.component_player, parent, false);
        return new PlayerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayerAdapter.ViewHolder holder, int position) {

        final int pos = holder.getAdapterPosition();

//        if(pos == 0) {
//            holder.background.setBackgroundColor(AppHandler.getColor(activity, android.R.color.transparent));
//            holder.txt_player_name.setTextColor(AppHandler.getColor(activity, R.color.tab_background));
//        } else {
//            holder.background.setBackgroundColor(AppHandler.getColor(activity, android.R.color.white));
//            holder.txt_player_name.setTextColor(AppHandler.getColor(activity, android.R.color.black));
//        }

        holder.txt_player_name.setText(players.get(pos).getPlayer().getName().split(" ")[0]);
        String no = "00";
        if (players.get(pos).getPosition() != null)
            no = String.format(Locale.getDefault(), "%02d", players.get(pos).getPosition().getPosNo());
        holder.txt_player_no.setText(no);

        try {
            String img_link = AppConfig.TOUCHDOWN_BASE_URL + players.get(pos).getPlayer().getImg_url();
            holder.img_profile_pic.setImageDrawable(AppHandler.getDrawable(activity, R.drawable.default_profile_pic));
            ImageAware imgAware = new ImageViewAware(holder.img_profile_pic);
            imageLoader.displayImage(img_link, imgAware, options);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        final View img_prof_pic = holder.img_profile_pic;
//        final View txt_name = holder.txt_player_name;
//        final View txt_no = holder.txt_player_no;
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    img_prof_pic.setTransitionName("profile_pic");
//                    txt_name.setTransitionName("name");
//                    txt_no.setTransitionName("no");
                }

                Intent intent = new Intent(activity, PlayerDialogActivity.class);
                intent.putExtra(PlayerDialogActivity.EXTRA_PLAYER_OBJECT, players.get(pos).getPlayer());
                if (players.get(pos).getPosition() != null)
                    intent.putExtra(PlayerDialogActivity.EXTRA_PLAYER_POSITION, players.get(pos).getPosition().getPosNo());
                else
                    intent.putExtra(PlayerDialogActivity.EXTRA_PLAYER_POSITION, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,
                            Pair.create(img_prof_pic, "profile_pic"));
//                            Pair.create(txt_name, "name"),
//                            Pair.create(txt_no, "no"));
                    activity.startActivity(intent, options.toBundle());
                } else {
                    activity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (players != null)
            return players.size();
        else
            return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parentView;
        RelativeLayout background;
        ImageView img_profile_pic;
        TextView txt_player_no;
        TextView txt_player_name;

        ViewHolder(View itemView) {
            super(itemView);
            background = (RelativeLayout) itemView.findViewById(R.id.layout_background);
            parentView = (RelativeLayout) itemView.findViewById(R.id.layout_player_parent);
            img_profile_pic = (ImageView) itemView.findViewById(R.id.img_player_pic);
            txt_player_no = (TextView) itemView.findViewById(R.id.txt_player_no);
            txt_player_name = (TextView) itemView.findViewById(R.id.txt_player_name);
        }
    }
}
