package lk.rc07.ten_years.touchdown.activities;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.models.FBImage;
import lk.rc07.ten_years.touchdown.utils.AppHandler;
import lk.rc07.ten_years.touchdown.utils.ImageViewAutoHeight;

public class ImageViewActivity extends AppCompatActivity {

    //constants
    public static final String IMAGE_STRING = "image_string";
    public static final String IMAGE_POS = "image_pos";

    private static List<FBImage> imagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        Type messageType = new TypeToken<List<FBImage>>() {
        }.getType();
        imagesList = new Gson().fromJson(getIntent().getStringExtra(IMAGE_STRING), messageType);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(getIntent().getIntExtra(IMAGE_POS, 0), true);

    }

    public static class PlaceholderFragment extends Fragment {
        //constants
        private static final String FACEBOOK_IMAGE_LINK = "http://graph.facebook.com/%s/picture";
        private static final String ARG_SECTION_NUMBER = "section_number";

        //instances
        private ImageLoader imageLoader;
        private DisplayImageOptions options;

        public PlaceholderFragment() {
            imageLoader = ImageLoader.getInstance();
            options = AppHandler.getImageOption(imageLoader, getContext(), R.drawable.icon_book_placeholder);
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_image_view, container, false);

            ImageViewAutoHeight imageView = (ImageViewAutoHeight) rootView.findViewById(R.id.image);
            imageLoader.displayImage(String.format(Locale.getDefault(), FACEBOOK_IMAGE_LINK,
                    imagesList.get(getArguments().getInt(ARG_SECTION_NUMBER)).getId()), imageView, options);
            return rootView;
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            if (imagesList != null)
                return imagesList.size();
            else
                return 0;
        }
    }
}
