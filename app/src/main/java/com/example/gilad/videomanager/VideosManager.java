package com.example.gilad.videomanager;

import android.provider.MediaStore;
import android.widget.ListAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Gilad on 08/02/2017.
 */

public class VideosManager {

    private ArrayList<File> videos = new ArrayList<File>();

    private String path;
    private String filter;

    // Constructor
    public VideosManager(String path, String filter) {
        this.path = path;
        this.filter = filter;

        loadVideosFromPath();
    }

    // Getters
    public ArrayList<File> getVideos() {
        return videos;
    }

    public File[] getVideosByPosition(ArrayList<Integer> positions)
    {
        File[] selFiles = new File[positions.size()];
        int i = 0;

        for (int pos: positions)
            selFiles[i++] = videos.get(pos);

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

        File f = new File(path);
        int i = 0;

        for (File file : f.listFiles() ) {
            if ( file.getName().contains(filter))
            {
                videos.add(file);
                i++;
            }
        }

    }


}
