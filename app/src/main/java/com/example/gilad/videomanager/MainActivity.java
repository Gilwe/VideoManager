package com.example.gilad.videomanager;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends BaseActivity {
    static final String FILTER = "Hearthstone";

    private VideosManager vm = new VideosManager(
            Environment.getExternalStorageDirectory().getPath() + "/Movies", FILTER);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateList();
    }

    private void populateList()
    {
        ListView listView = (ListView) findViewById(R.id.videoList);

        ListAdapter adapter = new CustomAdapter(this,vm.getVideos());

        listView.setAdapter(adapter);

    }
}
