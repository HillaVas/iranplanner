package com.iranplanner.tourism.iranplanner.photoViewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.iranplanner.tourism.iranplanner.R;

import java.util.List;

import entity.ResultImage;
import entity.ResultImageList;
import entity.ResultSouvenir;

/**
 * Created by h.vahidimehr on 25/02/2017.
 */

public class ImageGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<ResultImage> resultImageLists;

    public ImageGridAdapter(Context c, List<ResultImage> resultImageLists) {
        mContext = c;
        this.resultImageLists = resultImageLists;
    }

    @Override
    public int getCount() {
        return resultImageLists.size();
    }

    private void imageing(ImageView imageView, int position) {
        if (resultImageLists.get(position).getImgUrl() != null) {
            String url = resultImageLists.get(position).getImgUrl();
            Glide.with(mContext)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, GlideDrawable>() {

                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            //// TODO: 22/01/2017  get defeult picture
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);

        } else {
            Glide.clear(imageView);
//            viewHolder.ImageAttraction.setImageDrawable(null);
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_items, null);
            TextView textView = (TextView) grid.findViewById(R.id.txtName);
            ImageView imageView = (ImageView) grid.findViewById(R.id.imgGrid);
//            textView.setText(resultImageLists.get(position).getResultImages().get(position).getImgDescription());

            imageing(imageView, position);
//            imageView.setImageResource(imageView,position);
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}
