package org.worshipsongs.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.AddressConstants;

import org.apache.commons.io.FileUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.locator.IImportDatabaseLocator;
import org.worshipsongs.locator.ImportDatabaseLocator;
import org.worshipsongs.worship.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.app.Activity.RESULT_OK;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class DatabaseFragment extends Fragment
{
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WorshipSongApplication.getContext());
    private IImportDatabaseLocator importDatabaseLocator = new ImportDatabaseLocator();
    private SongDao songDao = new SongDao(WorshipSongApplication.getContext());
    private ProgressBar progressBar;

    public static DatabaseFragment newInstance()
    {
        return new DatabaseFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        setUserVisibleHint(false);
        View dataBaseFragmentView = inflater.inflate(R.layout.database_layout, container, false);
        setImportDatabaseButton(dataBaseFragmentView);
        setProgressBar(dataBaseFragmentView);
        setDefaultDatabaseButton(dataBaseFragmentView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return dataBaseFragmentView;
    }

    private void setImportDatabaseButton(View dataBaseFragmentView)
    {
        Button importDatabaseButton = (Button) dataBaseFragmentView.findViewById(R.id.upload_database_button);
        importDatabaseButton.setOnClickListener(new ImportDatabaseOnClickListener());
    }

    private class ImportDatabaseOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            showDatabaseTypeDialog();
        }
    }

    private void showDatabaseTypeDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AddressConstants.Themes.THEME_LIGHT);
        builder.setTitle(getString(R.string.type));
        builder.setSingleChoiceItems(R.array.dataBaseTypes, 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                importDatabaseLocator.load(getActivity(), DatabaseFragment.this, which, progressBar);
                dialog.cancel();
            }
        });

        builder.create();
        builder.show();
    }

    private void setProgressBar(View dataBaseFragmentView)
    {
        progressBar = (ProgressBar) dataBaseFragmentView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }

    private void setDefaultDatabaseButton(View dataBaseFragmentView)
    {
        Button defaultDatabaseButton = (Button) dataBaseFragmentView.findViewById(R.id.default_database_button);
        defaultDatabaseButton.setOnClickListener(new DefaultDbOnClickListener());
    }

    private class DefaultDbOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v)
        {
            LayoutInflater li = LayoutInflater.from(getActivity());
            View promptsView = li.inflate(R.layout.delete_confirmation_dialog, null);
            TextView deleteMsg = (TextView) promptsView.findViewById(R.id.deleteMsg);
            deleteMsg.setText(R.string.message_database_confirmation);
            AlertDialog.Builder builder = new AlertDialog.Builder((new ContextThemeWrapper(getActivity(), R.style.MyDialogTheme)));
            builder.setView(promptsView);
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    try {
                        songDao.copyDatabase("", true);
                        songDao.open();
                        dialog.cancel();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    File destinationFile = null;
                    Uri uri = intent.getData();
                    try {
                        progressBar.setVisibility(View.VISIBLE);
                        File externalCacheDir = getActivity().getCacheDir();
                        destinationFile = new File(externalCacheDir.getAbsolutePath(), CommonConstants.DATABASE_NAME);
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                        OutputStream os = new FileOutputStream(destinationFile);
                        byte[] data = new byte[inputStream.available()];
                        inputStream.read(data);
                        os.write(data);
                        inputStream.close();
                        os.close();
                        Log.i(DatabaseFragment.this.getClass().getSimpleName(), "Size of file " + FileUtils.sizeOf(destinationFile));
                        validateDatabase(destinationFile.getAbsolutePath());
                    } catch (Exception ex) {
                        Log.i(DatabaseFragment.class.getSimpleName(), "Error occurred while coping" + ex);
                    } finally {
                        progressBar.setVisibility(View.GONE);
                        destinationFile.deleteOnExit();
                    }

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void validateDatabase(String absolutePath)
    {
        try {
            songDao.copyDatabase(absolutePath, true);
            songDao.open();
            if (songDao.isValidDataBase()) {
                Toast.makeText(getContext(), R.string.message_database_successfull, Toast.LENGTH_SHORT).show();
            } else {
                showWarningDialog();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showWarningDialog()
    {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.delete_confirmation_dialog, null);
        TextView deleteMsg = (TextView) promptsView.findViewById(R.id.deleteMsg);
        deleteMsg.setText(R.string.message_database_invalid);
        AlertDialog.Builder builder = new AlertDialog.Builder((new ContextThemeWrapper(getActivity(), R.style.MyDialogTheme)));
        builder.setView(promptsView);
        builder.setTitle(getString(R.string.warning));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                try {
                    songDao.copyDatabase("", true);
                    songDao.open();
                    dialog.cancel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.show();
    }
}
