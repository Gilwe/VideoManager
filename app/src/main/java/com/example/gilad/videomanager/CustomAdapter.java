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

//import com.daimajia.androidanimations;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilad on 08/02/2017.
 */

public class CustomAdapter extends ArrayAdapter<File> {

    static ArrayList<Integer> selectedPositions = new ArrayList<Integer>();

    public CustomAdapter(Context context, ArrayList<File> videos) {

        super(context, R.layout.custom_row, videos);
    }

    static class ViewHolder {
        TextView title;
        TextView size;
        ImageView thumbnail;
        ImageView selectionCB;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
            viewHolder.selectionCB = (ImageView) customView.findViewById(R.id.selectedImage);

            customView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) customView.getTag();
        File file = getItem(position);

        // Setting the custom row with values
        holder.title.setText(file.getName());
        holder.size.setText(getFilesSize(file));
        if (holder.thumbnail != null) {
            new ImageDownloaderTask(holder.thumbnail).execute(file.getAbsolutePath());
        }

        if (this.selectedPositions.contains(position)) {
            holder.selectionCB.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeIn)
                    .duration(700)
                    .playOn(holder.selectionCB);
        }
        else
            holder.selectionCB.setVisibility(View.INVISIBLE);

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
                    MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
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

    public static String getFilesSize(File... files) {
        DecimalFormat df = new DecimalFormat("0.00");

        long rawSize = 0;

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;

        for (File file: files)
            rawSize += file.length();

        if (rawSize < sizeMb)
            return df.format(rawSize / sizeKb) + " KB";

        else if (rawSize < sizeGb)
            return df.format(rawSize / sizeMb) + " MB";
        else
            return df.format(rawSize / sizeGb) + " GB";
    }


}
