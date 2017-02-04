package lk.rc07.ten_years.touchdown.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Team;
import lk.rc07.ten_years.touchdown.utils.AppHandler;

/**
 * Created by Sabri on 1/14/2017. Adapter to fixture view
 */

public class FixtureAdapter extends RecyclerView.Adapter<FixtureAdapter.ViewHolder> {

    //instances
    private Context context;
    private List<Match> matches;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private DBManager dbManager;

    public FixtureAdapter(Context context, List<Match> matches) {
        this.context = context;
        this.matches = matches;

        dbManager = DBManager.initializeInstance(DBHelper.getInstance(context));

        this.imageLoader = ImageLoader.getInstance();
        options = AppHandler.getImageOption(imageLoader, context, R.drawable.icon_book_placeholder);
    }

    @Override
    public FixtureAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.component_fixture_row, parent, false);
        return new FixtureAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FixtureAdapter.ViewHolder holder, int position) {
        Match match = matches.get(position);

        Date date = new Date(match.getMatchDate());
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        holder.txt_date.setText(dateFormat.format(date));

        dateFormat =  new SimpleDateFormat("hh:mm a", Locale.getDefault());
        holder.txt_time.setText(dateFormat.format(date));
        holder.txt_venue.setText(match.getVenue());
        holder.txt_last.setText("Royal Won by 23 points");

        imageLoader.displayImage(getOpponentCrest(match.getTeamTwo()), holder.img_crest, options);
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

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_date;
        TextView txt_time;
        TextView txt_venue;
        TextView txt_last;

        ImageView img_crest;

        ViewHolder(View itemView) {
            super(itemView);
            txt_date = (TextView) itemView.findViewById(R.id.txt_fixture_date);
            txt_time = (TextView) itemView.findViewById(R.id.txt_fixture_time);
            txt_venue = (TextView) itemView.findViewById(R.id.txt_fixture_venue);
            txt_last = (TextView) itemView.findViewById(R.id.txt_fixture_last_match);

            img_crest = (ImageView) itemView.findViewById(R.id.img_fixture_college_crest);
        }
    }
}
