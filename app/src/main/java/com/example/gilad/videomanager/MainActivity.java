package com.example.gilad.videomanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.DialogPreference;
import android.preference.PreferenceGroup;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] permissionsArray = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    static class Params {
        String FILTER;
        Boolean ALL_FOLDERS;
        String SELECTED_FOLDER;
    }

    // Private members
    private boolean selectionMode = false;
    private VideosManager vm;
    private DataStorage storage;
    private Menu menu;
    private ListAdapter adapter;
    private Toolbar toolbar;
    private ActionMode actionMode;
    private Params params;
    private SettingsUIHandler settingsUIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        storage = new DataStorage(this);
        params = new Params();
        getParams();

        populateListIfPermitted();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean bSuper = super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.menu = menu;

        updateMenu();

        return bSuper;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.action_settings): {


                MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                        .title("Settings")
                        .customView(R.layout.settings_popup, true)
                        .positiveText("Apply")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (settingsUIHandler != null) {

                                    if (settingsUIHandler.updateParamsFromUI()) ;

                                    // Create new list if there were changes in the settings
                                    createNewList();
                                }
                            }
                        })
                        .show();

                settingsUIHandler = new SettingsUIHandler(materialDialog.getCustomView(), params);
                settingsUIHandler.initUI();

                break;

            }

        }

        return true;
    }

    public void setNullActionMode() {
        actionMode = null;
    }

    public void setSelectionMode(Boolean mode) {
        selectionMode = mode;
    }

    public void updateMenu() {

        if (selectionMode) {
            if (actionMode == null)
                // Select for the first time
                actionMode = this.startSupportActionMode(new ToolbarActionCallback(this, this.vm, adapter));

            actionMode.setTitle(String.valueOf(CustomAdapter.selectedPositions.size())
                    + "/" + vm.getVideos().size() + " Selected");

        }
        // Cancel action mode after exiting selection mode
        else if (actionMode != null)
            actionMode.finish();

    }


    private void populateList() {

        ArrayList<File> Videos = vm.getVideos();

        if (Videos.size() > 0) {
            ListView listView = (ListView) findViewById(R.id.list_video_list);

            adapter = new CustomAdapter(this, vm.getVideos());
            listView.setAdapter(adapter);

            // Long click on item method
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    selectionMode = true;

                    // Adds only unselected items
                    if (!CustomAdapter.selectedPositions.contains(position)) {
                        CustomAdapter.selectedPositions.add(position);

                        ((CustomAdapter) adapter).notifyDataSetChanged();

                        updateMenu();
                    }

                    return true;
                }
            });

            // Normal cick on item method
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (selectionMode) {
                        if (CustomAdapter.selectedPositions.contains(position)) {
                            CustomAdapter.selectedPositions.remove(
                                    CustomAdapter.selectedPositions.indexOf(position));

                            if (CustomAdapter.selectedPositions.size() == 0)
                                selectionMode = false;
                        } else {
                            CustomAdapter.selectedPositions.add(position);
                        }

                        updateMenu();
                        ((CustomAdapter) adapter).notifyDataSetChanged();
                    }

                }
            });
        }

    }

    private void createNewList() {
        String path = Environment.getExternalStorageDirectory().getPath();

        if (!params.ALL_FOLDERS)
            path += params.SELECTED_FOLDER;

        vm = new VideosManager(path, params.FILTER);
        populateList();
    }

    private void populateListIfPermitted() {
        List<String> deniedPermissions = new ArrayList<String>();

        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                    != PackageManager.PERMISSION_GRANTED) {

                deniedPermissions.add(permission);
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

        //  if its null - there are already permissions
        if (permissions != null && grantResults != null) {

            // Goes on each result and checks if it is granted
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED)
                    bAcceptedAll = false;
            }
        }

        if (bAcceptedAll) {

            createNewList();
        }
    }

    private void getParams() {
        params.FILTER = storage.getString(DataStorage.KEY_FILTER);
        params.ALL_FOLDERS = storage.getBool(DataStorage.KEY_ALL_FOLDERS);
        params.SELECTED_FOLDER = storage.getString(DataStorage.KEY_SELECTED_FOLDER);
    }

    private void saveParams() {
        storage.setFilter(params.FILTER);
        storage.setAllFolders(params.ALL_FOLDERS);
        storage.setSelectedFolder(params.SELECTED_FOLDER);
    }
}
