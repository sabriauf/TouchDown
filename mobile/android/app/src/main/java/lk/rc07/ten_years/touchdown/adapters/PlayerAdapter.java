package lk.rc07.ten_years.touchdown.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.BuildConfig;
import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.activities.PlayerDialogActivity;
import lk.rc07.ten_years.touchdown.fragments.PlayersFragment;
import lk.rc07.ten_years.touchdown.models.AdapterPlayer;
import lk.rc07.ten_years.touchdown.models.Staff;
import lk.rc07.ten_years.touchdown.utils.AppHandler;

/**
 * Created by Sabri on 2/4/2017. Adapter for players
 */

public class PlayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //constants
    private static final int VIEW_PLAYER = 1;
    private static final int VIEW_STAFF = 2;
    //instances
    private Activity activity;
    private List<Object> players;

    public PlayerAdapter(Activity activity, List<Object> players) {
        this.activity = activity;
        this.players = players;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);

        if (viewType == VIEW_PLAYER) {
            View view = inflater.inflate(R.layout.component_player, parent, false);
            return new PlayerAdapter.PlayerViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.component_staff_player_row, parent, false);
            return new PlayerAdapter.StaffViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        final int pos = viewHolder.getAdapterPosition();

//        if(pos == 0) {
//            holder.background.setBackgroundColor(AppHandler.getColor(activity, android.R.color.transparent));
//            holder.txt_player_name.setTextColor(AppHandler.getColor(activity, R.color.tab_background));
//        } else {
//            holder.background.setBackgroundColor(AppHandler.getColor(activity, android.R.color.white));
//            holder.txt_player_name.setTextColor(AppHandler.getColor(activity, android.R.color.black));
//        }

        if (viewHolder instanceof PlayerViewHolder) {
            PlayerViewHolder holder = (PlayerViewHolder) viewHolder;
            final AdapterPlayer adapterPlayer = (AdapterPlayer) players.get(pos);

            if (adapterPlayer != null) {
                if (adapterPlayer.getPlayer().getName().contains(" "))
                    holder.txt_player_name.setText(adapterPlayer.getPlayer().getName().split(" ")[0]);
                else
                    holder.txt_player_name.setText(adapterPlayer.getPlayer().getName());

                String no = "00";
                if (adapterPlayer.getPosition() != null)
                    no = String.format(Locale.getDefault(), "%02d", adapterPlayer.getPosition().getPosNo());
                holder.txt_player_no.setText(no);

                try {
                    String img_link = adapterPlayer.getPlayer().getImg_url();
                    if (!img_link.equals("/contents/players/")) {
                        img_link = BuildConfig.DEFAULT_URL + img_link;
                        holder.img_profile_pic.setImageDrawable(AppHandler.getDrawable(activity, R.drawable.default_profile_pic));
                        Glide.with(activity).load(img_link).placeholder(R.drawable.default_profile_pic).into(holder.img_profile_pic);
                    }
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
                        intent.putExtra(PlayerDialogActivity.EXTRA_PLAYER_OBJECT, adapterPlayer.getPlayer());
                        intent.putExtra(PlayerDialogActivity.EXTRA_PLAYER_TEAM, PlayersFragment.team_id);
                        intent.putExtra(PlayerDialogActivity.EXTRA_PLAYER_YEAR, PlayersFragment.year);
                        if (adapterPlayer.getPosition() != null)
                            intent.putExtra(PlayerDialogActivity.EXTRA_PLAYER_POSITION, adapterPlayer.getPosition().getPosNo());
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
        } else {
            StaffViewHolder holder = (StaffViewHolder) viewHolder;
            Staff staff = (Staff) players.get(pos);

            holder.txtName.setText(staff.getName());
            holder.txtPosition.setText(staff.getPosition());
        }
    }

    @Override
    public int getItemCount() {
        if (players != null)
            return players.size();
        else
            return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (players.get(position) instanceof AdapterPlayer)
            return VIEW_PLAYER;
        else if (players.get(position) instanceof Staff)
            return VIEW_STAFF;
        return super.getItemViewType(position);
    }

    class PlayerViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout parentView;
        //      private RelativeLayout background;
        private ImageView img_profile_pic;
        private TextView txt_player_no;
        private AppCompatTextView txt_player_name;

        PlayerViewHolder(View itemView) {
            super(itemView);
//            background = itemView.findViewById(R.id.layout_background);
            parentView = itemView.findViewById(R.id.layout_player_parent);
            img_profile_pic = itemView.findViewById(R.id.img_player_pic);
            txt_player_no = itemView.findViewById(R.id.txt_player_no);
            txt_player_name = itemView.findViewById(R.id.txt_player_name);
        }
    }

    class StaffViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtPosition;

        StaffViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name_staff_row);
            txtPosition = itemView.findViewById(R.id.txt_position_staff_row);
        }
    }
}
