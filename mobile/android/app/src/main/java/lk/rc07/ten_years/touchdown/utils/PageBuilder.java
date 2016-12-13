package lk.rc07.ten_years.touchdown.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabri on 12/13/2016. builder for Pages in viewpager
 */

public class PageBuilder {

    List<String> pageTitles;

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
}
