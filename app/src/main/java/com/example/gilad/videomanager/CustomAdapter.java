package com.example.gilad.videomanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Gilad on 08/02/2017.
 */

public class CustomAdapter extends ArrayAdapter<File> {

    public CustomAdapter(Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Inflates the row - custrom_row
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_row, parent,false);

        TextView title = (TextView) customView.findViewById(R.id.videoTitle);
        TextView size = (TextView) customView.findViewById(R.id.videoSize);
        ImageView thumbnail = (ImageView) customView.findViewById(R.id.imageView);

        File file = getItem(position);

        // Setting the custom row with values
        title.setText(file.getName());
        size.setText(getFileSize(file));

        Bitmap b = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(),
                MediaStore.Images.Thumbnails.MINI_KIND);

        thumbnail.setImageBitmap(b);

        return customView;
    }

    private String getFileSize(File file)
    {
        String sSize;
        long lSize = file.length();

        if ( lSize >= 1024) {
            float f = (float) lSize / 1024;
            sSize = f + " MB";
        }
        else
            sSize = lSize + " KB";


        return sSize;
    }
}
