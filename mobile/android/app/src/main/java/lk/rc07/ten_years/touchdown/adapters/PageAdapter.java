package lk.rc07.ten_years.touchdown.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import lk.rc07.ten_years.touchdown.fragments.LiveFragment;
import lk.rc07.ten_years.touchdown.utils.PageBuilder;

/**
 * Created by Sabri on 12/13/2016. adapter for page in viewpager
 */

public class PageAdapter extends FragmentPagerAdapter {

    private PageBuilder pageBuilder;

    public PageAdapter(FragmentManager fm, PageBuilder pageBuilder) {
        super(fm);
        this.pageBuilder = pageBuilder;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return new LiveFragment();
        else
            return new Fragment();
    }

    @Override
    public int getCount() {
        return pageBuilder.getPageTitles().size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageBuilder.getPageTitles().get(position);
    }
}