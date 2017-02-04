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

import java.util.List;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.activities.PlayerDialogActivity;
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
        holder.txt_name.setText(players.get(pos).getPlayer().getName());
        holder.txt_pos_no.setText(String.valueOf(players.get(pos).getPosition().getIdPosition()));
        holder.txt_position.setText(players.get(pos).getPosition().getPosName());

        imageLoader.displayImage(players.get(pos).getPlayer().getImg_url(), holder.img_profile_pic, options);

        final View txt_name = holder.txt_name;
        final View txt_pos = holder.txt_position;
        final View txt_pos_id = holder.txt_pos_no;
        final View img_prof_pic = holder.img_profile_pic;
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PlayerDialogActivity.class);
                intent.putExtra(PlayerDialogActivity.EXTRA_PLAYER_OBJECT, players.get(pos).getPlayer());
                intent.putExtra(PlayerDialogActivity.EXTRA_PLAYER_POSITION_ID, players.get(pos).getPosition().getPosNo());
                intent.putExtra(PlayerDialogActivity.EXTRA_PLAYER_POSITION ,players.get(pos).getPosition().getPosName());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,
                            Pair.create(txt_name, "profile_pic"),
                            Pair.create(txt_pos, "profile_name"),
                            Pair.create(img_prof_pic, "profile_position"),
                            Pair.create(txt_pos_id, "profile_position_id"));
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
        TextView txt_name;
        TextView txt_position;
        TextView txt_pos_no;
        ImageView img_profile_pic;

        ViewHolder(View itemView) {
            super(itemView);
            parentView = (RelativeLayout) itemView.findViewById(R.id.layout_player_parent);
            txt_name = (TextView) itemView.findViewById(R.id.txt_player_name);
            txt_position = (TextView) itemView.findViewById(R.id.txt_player_position);
            txt_pos_no = (TextView) itemView.findViewById(R.id.txt_player_id);
            img_profile_pic = (ImageView) itemView.findViewById(R.id.img_player_pic);
        }
    }
}
