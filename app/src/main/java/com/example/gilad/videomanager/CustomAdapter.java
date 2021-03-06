package com.example.gilad.videomanager;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.HandlerThread;
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
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Gilad on 08/02/2017.
 */

public class CustomAdapter extends ArrayAdapter<VideosManager.VideoFile> {

    static ArrayList<Integer> selectedPositions = new ArrayList<Integer>();

    public CustomAdapter(Context context, ArrayList<VideosManager.VideoFile> videos) {

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

            viewHolder.title = (TextView) customView.findViewById(R.id.text_video_title);
            viewHolder.size = (TextView) customView.findViewById(R.id.text_video_size);
            viewHolder.thumbnail = (ImageView) customView.findViewById(R.id.image_thumbnail);
            viewHolder.selectionCB = (ImageView) customView.findViewById(R.id.image_selected);

            // Invisible by default
            viewHolder.selectionCB.setVisibility(View.INVISIBLE);

            customView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) customView.getTag();
        File file = getItem(position).file;

        // Setting the custom row with values
        holder.title.setText(file.getName());
        holder.size.setText(getFilesSize(file));

        android.os.Handler h = new android.os.Handler();

        h.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++ ) {
                    holder.size.setText(String.valueOf(i) + "%");
                    try {
                        wait(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        if (holder.thumbnail != null) {
            new ImageDownloaderTask(holder.thumbnail).execute(file.getAbsolutePath());
        }

        if (this.selectedPositions.contains(position)) {

            if (holder.selectionCB.getVisibility() == View.INVISIBLE) {
                YoYo.with(Techniques.FadeIn)
                        .duration(400)
                        .playOn(holder.selectionCB);
            }

            holder.selectionCB.setVisibility(View.VISIBLE);
        }
        else {

            if (holder.selectionCB.getVisibility() == View.VISIBLE)
                YoYo.with(Techniques.FadeOut)
                        .duration(200)
                        .onEnd(new YoYo.AnimatorCallback() {
                            @Override
                             public void call(Animator animator) {
                                  holder.selectionCB.setVisibility(View.INVISIBLE);
                             }})

                        .playOn(holder.selectionCB)
                        ;


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
