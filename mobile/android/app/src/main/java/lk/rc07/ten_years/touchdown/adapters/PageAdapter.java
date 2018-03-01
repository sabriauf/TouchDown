package lk.rc07.ten_years.touchdown.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;

import lk.rc07.ten_years.touchdown.utils.PageBuilder;

/**
 * Created by Sabri on 12/13/2016. adapter for page in viewpager
 */

public class PageAdapter extends FragmentPagerAdapter {

    private PageBuilder pageBuilder;
    private HashMap<Integer, Fragment> fragments;
    private FragmentManager fm;

    public PageAdapter(FragmentManager fm, PageBuilder pageBuilder) {
        super(fm);
        this.fm = fm;
        this.pageBuilder = pageBuilder;
        fragments = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments.containsKey(position))
            if (fragments.get(position).isAdded())
                return fragments.get(position);
            else
                return null;
        else {
            Fragment fragment = getFragmentForPosition(position);
            fragments.put(position, fragment);
            return fragment;
        }
    }

    private Fragment getFragmentForPosition(int pos) {
        if (pos < pageBuilder.getFragments().length) {
            return pageBuilder.getFragments()[pos];
        }
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

    public void notifyDataSetChanged() {
        for (Integer fragmentId : fragments.keySet()) {
            try {
                fm.beginTransaction()
                        .detach(fragments.get(fragmentId))
                        .attach(fragments.get(fragmentId))
                        .commit();
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
    }
}
