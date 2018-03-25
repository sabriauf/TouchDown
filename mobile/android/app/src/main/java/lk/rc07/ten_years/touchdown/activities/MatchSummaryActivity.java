package lk.rc07.ten_years.touchdown.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.Locale;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.adapters.PageAdapter;
import lk.rc07.ten_years.touchdown.config.Constant;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.TeamDAO;
import lk.rc07.ten_years.touchdown.fragments.ImageFragment;
import lk.rc07.ten_years.touchdown.fragments.PlayersFragment;
import lk.rc07.ten_years.touchdown.fragments.SummaryFragment;
import lk.rc07.ten_years.touchdown.fragments.TimelineFragment;
import lk.rc07.ten_years.touchdown.models.Match;
import lk.rc07.ten_years.touchdown.models.Team;
import lk.rc07.ten_years.touchdown.utils.PageBuilder;

public class MatchSummaryActivity extends AppCompatActivity {

    //constants
    public static final String GAME_NAME = "%s Vs %s";
    private final String[] TAB_TITLES = {"Summary", "Timeline", "Team", "Images"};
    public static final String EXTRA_MATCH_OBJECT = "match_extra";

    //instance
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_summary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        readExtras(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        readExtras(intent);
    }

    private void readExtras(Intent intent) {
        if(intent.getExtras() != null) {
            Match match = intent.getExtras().getParcelable(EXTRA_MATCH_OBJECT);
            initView(match);
            if(intent.getExtras().containsKey(Constant.EXTRA_FRAGMENT_ID)) {
                int pos = intent.getExtras().getInt(Constant.EXTRA_FRAGMENT_ID);
                viewPager.setCurrentItem(pos, true);
            }
        }
    }

    private void initView(Match match) {
        if (match != null) {
            DBManager dbManager = DBManager.initializeInstance(
                    DBHelper.getInstance(this));
            dbManager.openDatabase();
            Team tOne = TeamDAO.getTeam(match.getTeamOne());
            Team tTwo = TeamDAO.getTeam(match.getTeamTwo());
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(String.format(Locale.getDefault(), GAME_NAME, tOne.getName(), tTwo.getName()));
            dbManager.closeDatabase();
        }

        PageBuilder pageBuilder = new PageBuilder();
        for (String title : TAB_TITLES) {
            pageBuilder.addPage(title);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_MATCH_OBJECT, match);
        if (match != null) {
            bundle.putInt(PlayersFragment.MATCH_ID, match.getIdmatch());
            bundle.putString(ImageFragment.FACEBOOK_ALBUM_ID, match.getAlbum());
        }
        SummaryFragment summaryFragment = new SummaryFragment();
        TimelineFragment timelineFragment = new TimelineFragment();
        PlayersFragment playersFragment = new PlayersFragment();
        ImageFragment imageFragment = new ImageFragment();
        summaryFragment.setArguments(bundle);
        timelineFragment.setArguments(bundle);
        playersFragment.setArguments(bundle);
        imageFragment.setArguments(bundle);

        Fragment[] fragments = new Fragment[]{summaryFragment, timelineFragment, playersFragment, imageFragment};
        pageBuilder.setFragments(fragments);

        PageAdapter adapter = new PageAdapter(
                getSupportFragmentManager(), pageBuilder);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpager_tab);
        viewPagerTab.setViewPager(viewPager);
    }

}
