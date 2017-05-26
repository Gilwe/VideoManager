package com.example.gilad.videomanager;

import android.provider.MediaStore;
import android.widget.ListAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Gilad on 08/02/2017.
 */

public class VideosManager {

    class VideoFile
    {
        public File file;
        public int downloadedPrecentage;

        public boolean isDone = false;

        VideoFile(File file)
        {
            this.file = file;
            isDone = true;
        }
    }

    private ArrayList<VideoFile> videos = new ArrayList<VideoFile>();

    private String path;
    private String filter;

    // Constructor
    public VideosManager(String path, String filter) {
        this.path = path;
        this.filter = filter;

        loadVideosFromPath();
    }

    // Getters
    public ArrayList<VideoFile> getVideos() {
        return videos;
    }

    public File[] getVideosByPosition(ArrayList<Integer> positions)
    {
        File[] selFiles = new File[positions.size()];
        int i = 0;

        for (int pos: positions)
            selFiles[i++] = videos.get(pos).file;

        return selFiles;
    }

    public void deleteFiles(File[] files, ListAdapter adapter)
    {

        for (File file: files) {
            ((CustomAdapter) adapter).remove(file); // Remove from UI list
          //  videos.remove(pos);       // Remove from app memory
            file.delete();             // Remove from storage
        }
    }

    private void loadVideosFromPath()
    {
        videos.clear();

        File file = new File(path);

        getFilesFromDirectory(file);
    }

    private void getFilesFromDirectory(final File folder)
    {
        for( final File file : folder.listFiles())
        {
            if (file.isDirectory())
                getFilesFromDirectory(file);

            // Videos only
            else if (file.toString().endsWith(".mp4") &&
                     file.getName().contains(filter))
                 videos.add(new VideoFile(file));

        }
    }


}
