package lk.rc07.ten_years.touchdown.utils;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabri on 12/13/2016. builder for Pages in viewpager
 */

public class PageBuilder {

    List<String> pageTitles;
    Fragment[] fragments;

    public PageBuilder() {
        pageTitles = new ArrayList<>();
    }

    public PageBuilder addPage(String title) {
        pageTitles.add(title);
        return this;
    }

    public List<String> getPageTitles() {
        return pageTitles;
    }

    public void setPageTitles(List<String> pageTitles) {
        this.pageTitles = pageTitles;
    }

    public Fragment[] getFragments() {
        return fragments;
    }

    public void setFragments(Fragment[] fragments) {
        this.fragments = fragments;
    }
}
