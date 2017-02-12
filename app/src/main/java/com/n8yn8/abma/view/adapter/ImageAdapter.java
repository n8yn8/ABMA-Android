package com.n8yn8.abma.view.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.n8yn8.abma.R;

/**
 * Created by Nate on 3/15/15.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes

            WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);

            int dimension = metrics.widthPixels/2-16;

            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(dimension, dimension));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    //2016
    private Integer[] mThumbIds = {
            R.drawable.brevard_zoo_logo_m,
            R.drawable.cfz,
            R.drawable.tampa_zoo,
            R.drawable.sante_fe_teaching_zoo,
            R.drawable.cma,
            R.drawable.fl_aq,
            R.drawable.sea_world,
            R.drawable.nei,
            R.drawable.pb,
            R.drawable.abi,
            R.drawable.bgt,
            R.drawable.fala,
            R.drawable.tampa_aazk,

    };
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
