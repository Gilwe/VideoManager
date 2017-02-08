package com.example.gilad.videomanager;

import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Gilad on 08/02/2017.
 */

public class VideosManager {

    private File[] videos;

    private String path;
    private String filter;

    // Constructor
    public VideosManager(String path, String filter) {
        this.path = path;
        this.filter = filter;

        loadVideosFromPath();
    }

    // Getters
    public File[] getVideos() {
        return videos;
    }

    private void loadVideosFromPath()
    {
        File f = new File(path);

        videos = f.listFiles();
    }


}
