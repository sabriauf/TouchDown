package lk.rc07.ten_years.touchdown.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lk.rc07.ten_years.touchdown.R;
import lk.rc07.ten_years.touchdown.activities.ImageViewActivity;
import lk.rc07.ten_years.touchdown.activities.LoginActivity;
import lk.rc07.ten_years.touchdown.data.DBHelper;
import lk.rc07.ten_years.touchdown.data.DBManager;
import lk.rc07.ten_years.touchdown.data.ImageDAO;
import lk.rc07.ten_years.touchdown.models.FBImage;
import lk.rc07.ten_years.touchdown.utils.AppHandler;

/**
 * Created by Sabri on 4/11/2017. Match Image gallery
 */

public class ImageFragment extends Fragment {

    //constants
    private static final String FACEBOOK_IMAGE_LINK = "http://graph.facebook.com/%s/picture";
    private static final String FACEBOOK_ALBUM_LINK = "/%s/photos?limit=400";
    public static final String FACEBOOK_ALBUM_ID = "album_id";
    public static final int FACEBOOK_LOGIN_REQUEST = 101;

    //instances
    private List<FBImageRow> images;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Activity activity;

    //views
    private RecyclerView recycler_fixture;
    private View parentView;
    private DBManager dbManager;

    //primary data
    private String albumId;
    private int matchId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        parentView = view;

        imageLoader = ImageLoader.getInstance();
        options = AppHandler.getImageOption(imageLoader, getContext(), R.drawable.icon_book_placeholder);

        recycler_fixture = view.findViewById(R.id.recycler_images);

        if(getArguments() != null) {
            albumId = getArguments().getString(FACEBOOK_ALBUM_ID);
            matchId = getArguments().getInt(PlayersFragment.MATCH_ID);
        }

        dbManager = DBManager.initializeInstance(DBHelper.getInstance(view.getContext()));
        dbManager.openDatabase();
        images = getImageRows(ImageDAO.getImages(matchId));
        dbManager.closeDatabase();

        if (images.size() > 1) {
            view.findViewById(R.id.txt_no_items).setVisibility(View.GONE);
            setAdapter();
        } else {
            if (AccessToken.getCurrentAccessToken() != null)
                if (albumId != null && !albumId.equals("")) {
                    view.findViewById(R.id.txt_no_items).setVisibility(View.GONE);
                    readAlbumPhotos(albumId);
                } else
                    recycler_fixture.setVisibility(View.GONE);
            else
                startActivityForResult(new Intent(getContext(), LoginActivity.class), FACEBOOK_LOGIN_REQUEST);
        }

        return view;
    }

    private void readAlbumPhotos(String albumId) {
        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        params.putString("fields", "id, created_time, width, height, name");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                String.format(Locale.getDefault(), FACEBOOK_ALBUM_LINK, albumId),
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        if (response.getJSONObject() != null) {
                            Log.d(ImageFragment.class.getSimpleName(), response.getJSONObject().toString());

                            try {
                                final String imgString = response.getJSONObject().getJSONArray("data").toString();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Type messageType = new TypeToken<List<FBImage>>() {
                                        }.getType();

                                        List<FBImage> imagesList = new Gson().fromJson(imgString, messageType);
                                        images = getImageRows(imagesList);

                                        if (activity == null)
                                            activity = getActivity();
                                        if (activity != null)
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    setAdapter();
                                                }
                                            });

                                        dbManager.openDatabase();
                                        for (FBImage image : imagesList) {
                                            image.setMatch(matchId);
                                            ImageDAO.addImage(image);
                                        }
                                        dbManager.closeDatabase();
                                    }
                                }).start();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).executeAsync();
    }

    private List<FBImageRow> getImageRows(List<FBImage> imagesList) {
        List<FBImageRow> images = new ArrayList<>();
        for (int i = 0; i < imagesList.size(); i++) {
            FBImageRow row;
            int rowNo = i / 3;
            if (images.size() <= rowNo) {
                row = new FBImageRow();
                row.images[i % 3] = imagesList.get(i);
                images.add(rowNo, row);
            } else {
                row = images.get(rowNo);
                row.images[i % 3] = imagesList.get(i);
                images.set(rowNo, row);
            }
        }

        return images;
    }

    private void setAdapter() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_fixture.setLayoutManager(mLayoutManager);
        recycler_fixture.setAdapter(new ImageAdapter());
    }

    private class FBImageRow {
        private FBImage[] images = new FBImage[3];
    }


    private class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        //constants
        private static final int LEFT_VIEW = 1;
        private static final int RIGHT_VIEW = 2;
        private static final int NORMAL_VIEW = 3;

        //instances
        private boolean isLeft = false;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case LEFT_VIEW:
                    view = LayoutInflater.from(getContext()).inflate(R.layout.component_image_left_row, parent, false);
                    return new ImageAdapter.ViewHolder(view);
                case RIGHT_VIEW:
                    view = LayoutInflater.from(getContext()).inflate(R.layout.component_image_right_row, parent, false);
                    return new ImageAdapter.ViewHolder(view);
                case NORMAL_VIEW:
                    view = LayoutInflater.from(getContext()).inflate(R.layout.component_image_landscape_row, parent, false);
                    return new ImageAdapter.ViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

            ViewHolder holder = (ViewHolder) viewHolder;
            final int pos = holder.getAdapterPosition();
            FBImageRow row = getSortedImageRow(images.get(pos));

            imageLoader.displayImage(String.format(Locale.getDefault(), FACEBOOK_IMAGE_LINK,
                    row.images[0].getId()), new ImageViewAware(holder.img_one), options);

            if (row.images[1] != null) {
                imageLoader.displayImage(String.format(Locale.getDefault(), FACEBOOK_IMAGE_LINK,
                        row.images[1].getId()), new ImageViewAware(holder.img_two), options);
                holder.img_two.setVisibility(View.VISIBLE);
            } else
                holder.img_two.setVisibility(View.GONE);

            if (row.images[2] != null) {
                imageLoader.displayImage(String.format(Locale.getDefault(), FACEBOOK_IMAGE_LINK,
                        row.images[2].getId()), new ImageViewAware(holder.img_three), options);
                holder.img_three.setVisibility(View.VISIBLE);
            } else
                holder.img_three.setVisibility(View.GONE);
            setImageClickEvent(holder.img_one, (pos * 3));
            setImageClickEvent(holder.img_two, (pos * 3) + 1);
            setImageClickEvent(holder.img_three, (pos * 3) + 2);
        }

        private void setImageClickEvent(ImageView imgView, final int pos) {
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ImageViewActivity.class);
                    intent.putExtra(ImageViewActivity.IMAGE_STRING, getImageListString());
                    intent.putExtra(ImageViewActivity.IMAGE_POS, pos);
                    startActivity(intent);
                }
            });
        }

        private FBImageRow getSortedImageRow(FBImageRow row) {
            for (int i = 0; i < row.images.length - 1; i++) {
                if (row.images[i] != null && row.images[i + 1] != null)
                    if (row.images[i].getWidth() > row.images[i + 1].getWidth()) {
                        FBImage temp = row.images[i + 1];
                        row.images[i + 1] = row.images[i];
                        row.images[i] = temp;
                    }
            }
            return row;
        }

        private String getImageListString() {
            List<FBImage> imagesList = new ArrayList<>();
            for (FBImageRow row : images) {
                imagesList.add(row.images[0]);
                if (row.images[1] != null)
                    imagesList.add(row.images[1]);
                if (row.images[2] != null)
                    imagesList.add(row.images[2]);
            }
            return new Gson().toJson(imagesList);
        }

        @Override
        public int getItemCount() {
            if (images != null)
                return images.size();
            else
                return 0;
        }

        @Override
        public int getItemViewType(int position) {
            for (FBImage image : images.get(position).images) {
                if (image != null)
                    if (image.getWidth() < image.getHeight())
                        if (!isLeft) {
                            isLeft = true;
                            return LEFT_VIEW;
                        } else {
                            isLeft = false;
                            return RIGHT_VIEW;
                        }
            }
            return NORMAL_VIEW;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView img_one;
            ImageView img_two;
            ImageView img_three;


            ViewHolder(View itemView) {
                super(itemView);
                img_one = itemView.findViewById(R.id.img_portrait);
                img_two = itemView.findViewById(R.id.img_landscape_one);
                img_three = itemView.findViewById(R.id.img_landscape_two);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FACEBOOK_LOGIN_REQUEST) {
                readAlbumPhotos(albumId);
                parentView.findViewById(R.id.txt_no_items).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }
}
