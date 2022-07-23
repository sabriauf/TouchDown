package lk.rc07.ten_years.touchdown.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.activities.LoginActivity;
import lk.rc07.ten_years.touchdown.adapters.ScoreAdapter;
import lk.rc07.ten_years.touchdown.config.AppConfig;
import lk.rc07.ten_years.touchdown.config.Constant;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.GroupDAO;
import lk.rc07.ten_years.touchdown.data.MatchDAO;
import lk.rc07.ten_years.touchdown.data.ScoreDAO;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
import lk.rc07.ten_years.touchdown.models.Comment;
import lk.rc07.ten_years.touchdown.models.GameTime;
import lk.rc07.ten_years.touchdown.models.Group;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Score;
import lk.rc07.ten_years.touchdown.models.Team;
import lk.rc07.ten_years.touchdown.utils.AppHandler;
import lk.rc07.ten_years.touchdown.utils.ScoreListener;
import lk.rc07.ten_years.touchdown.utils.ScoreObserver;
import lk.rc07.ten_years.touchdown.utils.TimeFormatter;

/**
 * Created by Sabri on 12/13/2016. fragment to show the live score
 */

public class LiveFragment extends Fragment {

    private static final int FACEBOOK_LOGIN_REQUEST = 201;
    private static final String DIGITAL_CLOCK_FONT = "fonts/digital_7.ttf";

    //instances
    private ScoreAdapter adapter;
    private ViewHolder holder;
    private Handler timer;
    private LinearLayoutManager mLayoutManager;
    private Context context;
    private Comment comment = null;
    private SortedMap<Long, GameTime> times;

    //primary data
    private int leftScoreTotal = 0;
    private int rightScoreTotal = 0;

    //views
    private RecyclerView recyclerView;
    private View parentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);

        parentView = view;

        recyclerView = view.findViewById(R.id.recycler_timeline);
        mLayoutManager = new LinearLayoutManager(parentView.getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(false);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadScore.run();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FACEBOOK_LOGIN_REQUEST && AccessToken.getCurrentAccessToken() != null && comment != null) {
                //TODO send comment
            } else
                comment = null;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private final Thread loadScore = new Thread(new Runnable() {
        @Override
        public void run() {
            DBManager dbManager = DBManager.initializeInstance(
                    DBHelper.getInstance(getContext()));
            dbManager.openDatabase();

            final Match match = MatchDAO.getDisplayMatch();
            List<Score> scores = null;
            Group group = null;
            if (match != null) {
                scores = ScoreDAO.getScores(match.getIdmatch());
                group = GroupDAO.getGroupForId(match.getGroup());
            }

            Team tOne = null;
            Team tTwo = null;

            //set matchStart time
            if (match != null) {
//                matchStartTime = AppHandler.getMatchStartTime(match);
                times = AppHandler.getMatchGameTimes(match.getIdmatch());
                calculateScore(match, scores);

                tOne = TeamDAO.getTeam(match.getTeamOne());
                tTwo = TeamDAO.getTeam(match.getTeamTwo());
            }

            final Team teamOne = tOne;
            final Team teamTwo = tTwo;

            dbManager.closeDatabase();

            final Group finalGroup = group;
            final List<Score> temp_scores = scores;
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (match != null) {
                            adapter = new ScoreAdapter(getContext(), temp_scores, match, times);
                            recyclerView.setAdapter(adapter);
                            setData(parentView, match, finalGroup, teamOne, teamTwo);
                            if (temp_scores.size() > 0)
                                mLayoutManager.scrollToPositionWithOffset(temp_scores.size() - 1, 0);
                        } else {
                            setData(parentView, null, null, null, null);
                        }
                    }
                });
            }

        }
    });

    private void setShareAction(final Match match) {
        holder.fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (match != null) {

                    DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(parentView.getContext()));
                    dbManager.openDatabase();

                    String message = AppHandler.getResultString(parentView.getContext(), match);
                    message += Constant.SHARE_APP_TAGS;
                    message += Constant.SHARE_APP_PROM + AppConfig.APP_DOWNLOAD_LINK;

                    try {
                        Log.d(LiveFragment.class.getSimpleName(), message);
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, "Share the score"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    dbManager.closeDatabase();
                }
            }
        });
    }

//    private String getTeamShortName(int teamId) {
//        String shortName = "";
//        String name = TeamDAO.getTeam(teamId).getName();
//        for (String part : name.split(" ")) {
//            shortName += part.charAt(0);
//        }
//        return shortName;
//    }


    private void setData(View view, final Match match, Group group, Team tOne, Team tTwo) {
        holder = new ViewHolder(view);

//        holder.img_murc_logo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.MURC_WEB_LINK));
//                parentView.getContext().startActivity(intent);
//            }
//        });

        ScoreObserver.setScoreListener(new ScoreListener() {
            @Override
            public void OnNewScoreUpdate(Score score) {
                if (match == null || score.getMatchid() != match.getIdmatch())
                    loadScore.run();
                else {
                    if (score.getActionType() == Score.WHAT_ACTION_TIME)
                        AppHandler.addGameTimeObj(times, score);
                    updateScore(score, getUpdatedMatch(score, match));
                }
            }

            @Override
            public void OnScoreUpdate(Score score) {
                if (score != null && match != null && score.getMatchid() == match.getIdmatch()) {
                    if (score.getActionType() == Score.WHAT_ACTION_TIME) {
                        AppHandler.removeGameTimeObj(times, score);
                        AppHandler.addGameTimeObj(times, score);
                        setTimer(getUpdatedMatch(score, match));
                    }
                    adapter.updateItem(score);
                    calculateScore(match, adapter.getScores());
                    holder.txt_score_one.setText(String.valueOf(leftScoreTotal));
                    holder.txt_score_two.setText(String.valueOf(rightScoreTotal));
                }
            }

            @Override
            public void OnScoreRemove(Score score) {
                if (score.getMatchid() == match.getIdmatch()) {
                    if (score.getActionType() == Score.WHAT_ACTION_TIME)
                        AppHandler.removeGameTimeObj(times, score);
                    adapter.removeItem(score);
                    calculateScore(match, adapter.getScores());
                    holder.txt_score_one.setText(String.valueOf(leftScoreTotal));
                    holder.txt_score_two.setText(String.valueOf(rightScoreTotal));
                }
            }

            @Override
            public void OnMatchRemoved() {
                times = null;
                loadScore.run();
            }


        }, LiveFragment.class.getSimpleName());

        if (match == null)
            setDefaultView();
        else
            setMatchView(match, group, tOne, tTwo);

        setTimer(match);

        setShareAction(match);

//        holder.img_send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (holder.edt_comment.getText().toString().equals("")) {
//                    holder.edt_comment.setError("Please add a comment");
//                } else if (AccessToken.getCurrentAccessToken() == null) {
//                    comment = createCommentObject(holder.edt_comment.getText().toString());
//                    startActivityForResult(new Intent(getContext(), LoginActivity.class), FACEBOOK_LOGIN_REQUEST);
//                } else {
//                    Comment comment = createCommentObject(holder.edt_comment.getText().toString());
//                    //TODO send comment
//                    holder.edt_comment.setError(null);
//                }
//            }
//        });
    }

//    private Comment createCommentObject(String message) {
//        DBManager dbManager = DBManager.initializeInstance(
//                DBHelper.getInstance(getContext()));
//        dbManager.openDatabase();
//
//        SharedPreferences sharedPref = context.getSharedPreferences(Constant.MY_PREFERENCES, Context.MODE_PRIVATE);
//
//        Match match = MatchDAO.getDisplayMatch();
//
//        if (match != null) {
//            Comment comment = new Comment();
//            comment.setComment(message);
//            comment.setMatchId(match.getIdmatch());
//            comment.setUserId(sharedPref.getString(Constant.SHEARED_PREFEREANCE_KEY_USER_ID, ""));
//            comment.setUsername(sharedPref.getString(Constant.SHEARED_PREFEREANCE_KEY_USER_NAME, ""));
//            comment.setTimestamp(Calendar.getInstance().getTimeInMillis());
//
//            dbManager.closeDatabase();
//
//            return comment;
//        } else
//            return null;
//    }

    private Match getUpdatedMatch(Score score, Match match) {
        if (score.getActionType() == Score.WHAT_ACTION_TIME) {
            DBManager dbManager = DBManager.initializeInstance(DBHelper.getInstance(getContext()));
            dbManager.openDatabase();
            match = MatchDAO.getMatchForId(score.getMatchid());
            dbManager.closeDatabase();
        }
        return match;
    }

    private void setTimer(final Match match) {
        timer = new Handler();
        timer.postDelayed(new Runnable() {
            @Override
            public void run() {
                long gameTime = getGameMilliseconds();
                if (match != null && (match.getStatus() == Match.Status.FIRST_HALF || match.getStatus() == Match.Status.SECOND_HALF
                        || match.getStatus() == Match.Status.GAME_PAUSE) && times != null) {
                    if (gameTime > AppConfig.SECOND_HALF_START_TIME * 2 || (gameTime > AppConfig.SECOND_HALF_START_TIME
                            && match.getStatus() == Match.Status.FIRST_HALF))
                        holder.txt_time.setTextColor(AppHandler.getColor(parentView.getContext(), R.color.live_back));
                    else if (match.getStatus() == Match.Status.GAME_PAUSE)
                        holder.txt_time.setTextColor(AppHandler.getColor(parentView.getContext(), R.color.tab_background));
                    else
                        holder.txt_time.setTextColor(AppHandler.getColor(parentView.getContext(), R.color.timer_clock));

                    if (gameTime > 0) {
                        holder.txt_time.setText(TimeFormatter.millisToGameTime(getContext(), gameTime));
                        timer.postDelayed(this, 1000);
                    }
                }
            }
        }, 1000);
    }

    private long getGameMilliseconds() {
        if (times != null && times.size() > 0) {
            GameTime gameTime = times.get(times.lastKey());
            if (gameTime != null) {
                long diff = gameTime.getType() == 1 ? System.currentTimeMillis() - gameTime.getRealTime() : 0;
                return diff + gameTime.getRelativeTime();
            }
        }

        return 0L;
    }

    private void updateScore(Score score, Match match) {
        adapter.addItem(score);
        if (adapter.getItemCount() > 0)
            mLayoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
        switch (score.getAction()) {
            case START:
            case SECOND_HALF:
                holder.txt_live.setVisibility(View.VISIBLE);
                setTimerFont(true);
                setTimer(match);
                break;
            case FULL_TIME:
            case HALF_TIME:
//                matchStartTime = 0;
                break;
            default:
                if (score.getTeamId() == match.getTeamOne())
                    leftScoreTotal += score.getScore();
                else
                    rightScoreTotal += score.getScore();
                holder.txt_score_one.setText(String.valueOf(leftScoreTotal));
                holder.txt_score_two.setText(String.valueOf(rightScoreTotal));
        }
    }

    private void setDefaultView() {
        holder.txt_league.setText("");
        holder.txt_round.setText("");
        holder.txt_score_two.setText("0");
        holder.txt_score_one.setText("0");
        holder.txt_live.setVisibility(View.GONE);
        setTimerFont(true);
        holder.txt_time.setText(R.string.default_live_match_time);
    }

    private void setMatchView(Match match, Group group, Team tOne, Team tTwo) {
        if (group != null) {
            holder.txt_league.setText(group.getLeagueName());
            holder.txt_round.setText(group.getRoundName());
        } else {
            holder.txt_league.setText("");
            holder.txt_round.setText("");
        }
        holder.txt_score_two.setText(String.valueOf(rightScoreTotal));
        holder.txt_score_one.setText(String.valueOf(leftScoreTotal));
        Glide.with(this).load(tOne.getLogo_url()).into(holder.img_team_one_logo);
        Glide.with(this).load(tTwo.getLogo_url()).into(holder.img_team_two_logo);
        if (match.getStatus() == Match.Status.FIRST_HALF || match.getStatus() == Match.Status.SECOND_HALF
                || match.getStatus() == Match.Status.GAME_PAUSE) {
            holder.txt_live.setVisibility(View.VISIBLE);
            setTimerFont(true);
            holder.txt_time.setText(TimeFormatter.millisToGameTime(getContext(), getGameMilliseconds()));
        } else {
            holder.txt_live.setVisibility(View.GONE);
            setTimerFont(false);
            times = null;
            holder.txt_time.setText(getMatchString(match));
        }
    }

    private String getMatchString(Match match) {
        switch (match.getStatus()) {
            case PENDING:
                Date date = new Date(match.getMatchDate());
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
                return dateFormat.format(date);
            case DONE:
            case FULL_TIME:
//                if(!match.getResult().equals("")) //TODO
                return match.getResult();
//                else
//                    return
            default:
                return match.getStatus().toStringValue();
        }
    }

    private void setTimerFont(boolean isTimer) {
        if (isTimer) {
            holder.txt_time.setTypeface(Typeface.createFromAsset(parentView.getContext().getAssets(), DIGITAL_CLOCK_FONT));
            holder.txt_time.setTextSize(42);
            holder.txt_time.setTextColor(AppHandler.getColor(parentView.getContext(), R.color.timer_clock));
        } else {
            holder.txt_time.setTypeface(Typeface.SANS_SERIF);
            holder.txt_time.setTextSize(22);
            holder.txt_time.setTextColor(AppHandler.getColor(parentView.getContext(), R.color.text_title));
        }
    }

    private void calculateScore(Match match, List<Score> scores) {

        leftScoreTotal = 0;
        rightScoreTotal = 0;
        for (Score score : scores) {
            if (score.getTeamId() == match.getTeamOne())
                leftScoreTotal += score.getScore();
            else
                rightScoreTotal += score.getScore();
        }
    }

    private static class ViewHolder {
        AppCompatTextView txt_league;
        AppCompatTextView txt_round;
        AppCompatTextView txt_score_one;
        AppCompatTextView txt_score_two;
        TextView txt_live;
        TextView txt_time;
        ImageView img_team_one_logo;
        ImageView img_team_two_logo;
        //        EditText edt_comment;
        //        ImageViewAutoHeight img_murc_logo;
        ImageView img_send;
        FloatingActionButton fab_share;

        ViewHolder(View view) {
            txt_league = view.findViewById(R.id.txt_league_name);
            txt_round = view.findViewById(R.id.txt_round_name);
            txt_live = view.findViewById(R.id.txt_live_notifier);
            txt_time = view.findViewById(R.id.txt_match_time);
//            img_murc_logo = view.findViewById(R.id.img_murc);
//            edt_comment = view.findViewById(R.id.edt_user_comment_timeline);
            img_send = view.findViewById(R.id.img_send_comment_timeline);

            View view_team_one = view.findViewById(R.id.layout_score_team_one);
            txt_score_one = view_team_one.findViewById(R.id.txt_score);
            img_team_one_logo = view_team_one.findViewById(R.id.img_college_crest);

            View view_team_two = view.findViewById(R.id.layout_score_team_two);
            txt_score_two = view_team_two.findViewById(R.id.txt_score);
            img_team_two_logo = view_team_two.findViewById(R.id.img_college_crest);

            fab_share = view.findViewById(R.id.fab_share);

//            edt_comment.clearFocus();
        }
    }
}
