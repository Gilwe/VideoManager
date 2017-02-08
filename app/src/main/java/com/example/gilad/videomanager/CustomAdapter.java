package com.example.gilad.videomanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Gilad on 08/02/2017.
 */

public class CustomAdapter extends ArrayAdapter<File> {

    public CustomAdapter(Context context, ArrayList<File> videos) {

        super(context,R.layout.custom_row ,videos);
    }

    static class ViewHolder
    {
        TextView title;
        TextView size;
        ImageView  thumbnail;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View customView = convertView;

        if (customView == null) {
            // Inflates the row - custrom_row
            LayoutInflater inflater = LayoutInflater.from(getContext());
            customView = inflater.inflate(R.layout.custom_row, parent, false);

            // Configure Viewholer
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.title = (TextView) customView.findViewById(R.id.videoTitle);
            viewHolder.size = (TextView) customView.findViewById(R.id.videoSize);
            viewHolder.thumbnail = (ImageView) customView.findViewById(R.id.imageView);

            customView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) customView.getTag();
        File file = getItem(position);

        // Setting the custom row with values
        holder.title.setText(file.getName());
        holder.size.setText(getFileSize(file));
        if (holder.thumbnail != null)
        {
            new ImageDownloaderTask(holder.thumbnail).execute(file.getAbsolutePath());
        }


        return customView;
    }

    // Asynch task for image download
    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... path) {
            return ThumbnailUtils.createVideoThumbnail(path[0],
                    MediaStore.Images.Thumbnails.MINI_KIND);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
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
