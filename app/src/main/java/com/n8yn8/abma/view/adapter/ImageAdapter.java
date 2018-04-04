package com.n8yn8.abma.view.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.n8yn8.abma.App;
import com.n8yn8.abma.model.backendless.BSponsor;

import java.util.List;

/**
 * Created by Nate on 3/15/15.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<BSponsor> sponsors;

    private ImageLoader mImageLoader;

    public ImageAdapter(Context c, List<BSponsor> sponsors) {
        mContext = c;
        this.sponsors = sponsors;

        mImageLoader = ((App)c.getApplicationContext()).getImageLoader();
    }

    public int getCount() {
        return sponsors.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        NetworkImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes

            WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);

            int dimension = metrics.widthPixels/2-16;

            imageView = new NetworkImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(dimension, dimension));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (NetworkImageView) convertView;
        }

        imageView.setImageUrl(sponsors.get(position).getImageUrl(), mImageLoader);
        return imageView;
    }

    // references to our images

    //2015
//    private Integer[] mThumbIds = {
//            R.drawable.cph_zoo, R.drawable.dbp,
//            R.drawable.givskud_zoo, R.drawable.odense_zoo,
//            R.drawable.sdu, R.drawable.training_store,
//            R.drawable.mazuri, R.drawable.profis,
//            R.drawable.sea_world, R.drawable.zooply
//    };
    //2014
//    private Integer[] mThumbIds = {
//            R.drawable.aazk_dallas, R.drawable.aazk_galv,
//            R.drawable.aazkchey, R.drawable.abi,
//            R.drawable.ap, R.drawable.blue,
//            R.drawable.childrensaquarium, R.drawable.cliff,
//            R.drawable.dallaszoo, R.drawable.dwa,
//            R.drawable.frwc, R.drawable.fwzoo,
//            R.drawable.maf, R.drawable.natbal,
//            R.drawable.seaworld
//    };
}
