package com.example.gilad.videomanager;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.view.ActionMode;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

/**
 * Created by Gilad on 10/03/2017.
 */

public class ToolbarActionCallback implements ActionMode.Callback {
    Activity activity;
    VideosManager vm;
    ListAdapter adapter;

    public ToolbarActionCallback(Activity activity, VideosManager vm, ListAdapter adapter) {
        this.activity = activity;
        this.vm = vm;
        this.adapter = adapter;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.selection_mode, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            // Delete selected videos
            case (R.id.delete_video): {
                int nSelected = 0;
                String nTotalSize;
                final File[] selectedFiles = vm.getVideosByPosition(CustomAdapter.selectedPositions);


                // Set deleting message
                String msg = getResources().getString(R.string.delete_warning1) + " " +
                        "<b>" + CustomAdapter.getFilesSize(selectedFiles) + "</b>" + " "
                        + getResources().getString(R.string.delete_warning2);

                nSelected = CustomAdapter.selectedPositions.size();

                // Popup for the user
                new MaterialDialog.Builder(this.activity)
                        .title("Deleting " + nSelected + " Videos")
                        .content(Html.fromHtml(msg))
                        .positiveText("Agree")

                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                vm.deleteFiles(selectedFiles, adapter);

                                Toast.makeText(activity, getResources().getString(R.string.after_delete_msg), Toast.LENGTH_LONG).show();

                                CustomAdapter.selectedPositions.clear();

                                ((MainActivity) activity).setSelectionMode(false);
                                ((MainActivity) activity).updateMenu();
                            }
                        })
                        .show();
            }
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        ((MainActivity) activity).setNullActionMode();
    }

    private Resources getResources()
    {
        return activity.getResources();
    }

}
