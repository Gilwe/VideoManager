package com.example.gilad.videomanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    static final String FILTER = "Hearthstone";

    private String[] permissionsArray =  new String[] {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private VideosManager vm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateListIfPermitted();
    }

    private void populateList() {

        vm = new VideosManager(
                Environment.getExternalStorageDirectory().getPath() + "/Movies", FILTER);

        ListView listView = (ListView) findViewById(R.id.videoList);

        ListAdapter adapter = new CustomAdapter(this, vm.getVideos());

        listView.setAdapter(adapter);
    }

    private void populateListIfPermitted()
    {
        List<String> deniedPermissions = new ArrayList<String>();

        for (String curr : permissionsArray) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, curr)
                    != PackageManager.PERMISSION_GRANTED) {

                deniedPermissions.add(curr);
            }
        }

        if (deniedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    deniedPermissions.toArray(new String[deniedPermissions.size()]), 0);
        } else {
            onRequestPermissionsResult(0, null, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean bAcceptedAll = true;

        // IT if it null - there are already permissions
        if (permissions != null && grantResults != null) {

            // Goes on each result and checks if it is granted
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED)
                    bAcceptedAll = false;
            }
        }

        if (bAcceptedAll)
            populateList();
    }
}
