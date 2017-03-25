package com.example.gilad.videomanager;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Gilad on 25/03/2017.
 * Handles the UI of the settings dialog
 */

public class SettingsUIHandler {
    View view; // Comes from the MaterialDialog.getCustomView
    MainActivity.Params params;

    public SettingsUIHandler(View view, MainActivity.Params params) {
        this.view = view;
        this.params = params;
    }

    public void initUI()
    {
        EditText filter = (EditText) view.findViewById(R.id.edit_filter);
        Switch allFolders = (Switch) view.findViewById(R.id.switch_all_folders);
        final TextView folder = (TextView) view.findViewById(R.id.text_selected_folder);

        filter.setText(params.FILTER);
        allFolders.setChecked(params.ALL_FOLDERS);
        folder.setText(params.SELECTED_FOLDER);

        if (allFolders.isChecked())
            folder.setEnabled(false);

        allFolders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // folder is relevant only when ALL FOLDER switch is off
                folder.setEnabled(!isChecked);
            }
        });
    }


    // Return true if there were changes
    public boolean updateParamsFromUI()
    {
        EditText filter = (EditText) view.findViewById(R.id.edit_filter);
        Switch allFolders = (Switch) view.findViewById(R.id.switch_all_folders);
        TextView folder = (TextView) view.findViewById(R.id.text_selected_folder);
        boolean isChanged = false;

        if (params.FILTER != filter.getText().toString().trim() ||
            params.ALL_FOLDERS != allFolders.isChecked() ||
            params.SELECTED_FOLDER != folder.getText().toString().trim())
            isChanged = true;

        params.FILTER = ((EditText) view.findViewById(R.id.edit_filter)).getText().toString();
        params.ALL_FOLDERS = ((Switch) view.findViewById(R.id.switch_all_folders)).isChecked();
        params.SELECTED_FOLDER =
                ((TextView) view.findViewById(R.id.text_selected_folder)).getText().toString();

        return isChanged;
    }
}
