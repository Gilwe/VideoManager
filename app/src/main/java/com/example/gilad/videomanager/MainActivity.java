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

public class MainActivity extends BaseActivity {
    static final String FILTER = "Hearthstone";

    VideosManager vm = new VideosManager(
            Environment.getExternalStorageDirectory().getPath() + "/Movies", FILTER);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Download Video in the future", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }
}
