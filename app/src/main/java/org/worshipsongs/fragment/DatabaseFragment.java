package org.worshipsongs.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.dialog.CustomDialogBuilder;
import org.worshipsongs.domain.DialogConfiguration;
import org.worshipsongs.locator.IImportDatabaseLocator;
import org.worshipsongs.locator.ImportDatabaseLocator;
import org.worshipsongs.worship.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Author : Madasamy
 * Version : 3.x
 */

public class DatabaseFragment extends Fragment
{
    private IImportDatabaseLocator importDatabaseLocator = new ImportDatabaseLocator();
    private SongDao songDao = new SongDao(WorshipSongApplication.getContext());
    private ProgressBar progressBar;
    private Button defaultDatabaseButton;
    private TextView resultTextView;

    public DatabaseFragment()
    {

    }

    public static DatabaseFragment newInstance()
    {
        return new DatabaseFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View dataBaseFragmentView = inflater.inflate(R.layout.database_layout, container, false);
        setImportDatabaseButton(dataBaseFragmentView);
        setProgressBar(dataBaseFragmentView);
        setDefaultDatabaseButton(dataBaseFragmentView);
        setResultTextView(dataBaseFragmentView);
        getActivity().invalidateOptionsMenu();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.MyDialogTheme));
        builder.setTitle(getString(R.string.type));
        builder.setItems(R.array.dataBaseTypes, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Map<String, Object> objectMap = new HashMap<String, Object>();
                objectMap.put(CommonConstants.INDEX_KEY, which);
                objectMap.put(CommonConstants.PROGRESS_BAR_KEY, progressBar);
                objectMap.put(CommonConstants.FRAGMENT_KEY, DatabaseFragment.this);
                objectMap.put(CommonConstants.TEXTVIEW_KEY, resultTextView);
                importDatabaseLocator.load(getActivity(), objectMap);
                defaultDatabaseButton.setVisibility(View.VISIBLE);
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getListView().setSelector(android.R.color.darker_gray);
        dialog.show();
    }

    private void setProgressBar(View dataBaseFragmentView)
    {
        progressBar = (ProgressBar) dataBaseFragmentView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }

    private void setDefaultDatabaseButton(View dataBaseFragmentView)
    {
        defaultDatabaseButton = (Button) dataBaseFragmentView.findViewById(R.id.default_database_button);
        defaultDatabaseButton.setOnClickListener(new DefaultDbOnClickListener());
    }

    private class DefaultDbOnClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            DialogConfiguration dialogConfiguration = new DialogConfiguration("",
                    getActivity().getString(R.string.message_database_confirmation));
            CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(getActivity(), dialogConfiguration);
            customDialogBuilder.getBuilder().setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    try {
                        songDao.close();
                        songDao.copyDatabase("", true);
                        songDao.open();
                        defaultDatabaseButton.setVisibility(View.GONE);
                        updateResultTextview();
                        dialog.cancel();
                    } catch (IOException ex) {
                        Log.e(DatabaseFragment.this.getClass().getSimpleName(), "Error occurred while coping database "+ex);
                    }
                }
            });
            customDialogBuilder.getBuilder().setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });
            customDialogBuilder.getBuilder().show();
        }
    }

    private void setResultTextView(View dataBaseFragmentView)
    {
        resultTextView = (TextView)dataBaseFragmentView.findViewById(R.id.result_textview);
        resultTextView.setText("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.action_bar_menu, menu);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setVisibility(View.GONE);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        progressBar.setVisibility(View.VISIBLE);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    doCopyFile(uri, getFileName(uri));
                }
                break;
        }
        progressBar.setVisibility(View.GONE);
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void doCopyFile(Uri uri, String fileName)
    {
        try {
            String fileExtension = FilenameUtils.getExtension(fileName);
            if ("sqlite".equalsIgnoreCase(fileExtension)) {
                showConfirmationDialog(uri, fileName);
            } else {
                copyFile(uri);
            }
        } finally {
            getDestinationFile().deleteOnExit();
        }
    }

    private void showConfirmationDialog(final Uri uri, String fileName)
    {
        String formattedMessage = String.format(getResources().getString(R.string.message_chooseDatabase_confirmation), fileName);
        DialogConfiguration dialogConfiguration = new DialogConfiguration(getActivity().getString(R.string.confirmation),
                formattedMessage);
        CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(getActivity(), dialogConfiguration);
        customDialogBuilder.getBuilder().setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                copyFile(uri);
                dialog.cancel();
            }
        });
        customDialogBuilder.getBuilder().setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        customDialogBuilder.getBuilder().show();
    }

    private void copyFile(Uri uri)
    {
        try {
            File destinationFile = getDestinationFile();
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            OutputStream outputstream = new FileOutputStream(destinationFile);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            outputstream.write(data);
            inputStream.close();
            outputstream.close();
            Log.i(DatabaseFragment.this.getClass().getSimpleName(), "Size of file " + FileUtils.sizeOf(destinationFile));
            validateDatabase(getDestinationFile().getAbsolutePath());
        } catch (IOException ex) {
            Log.i(DatabaseFragment.class.getSimpleName(), "Error occurred while coping file" + ex);
        }
    }

    File getDestinationFile()
    {
        return new File(getActivity().getCacheDir().getAbsolutePath(), CommonConstants.DATABASE_NAME);
    }

    private String getFileName(Uri uri)
    {
        File selectedFile = new File(uri.toString());
        String fileName = "";
        if (uri.toString().startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else {
            fileName = selectedFile.getName();
        }
        return fileName;
    }

    private void validateDatabase(String absolutePath)
    {
        try {
            resultTextView.setText("");
            songDao.close();
            songDao.copyDatabase(absolutePath, true);
            songDao.open();
            if (songDao.isValidDataBase()) {
                updateResultTextview();
            } else {
                showWarningDialog();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(DatabaseFragment.this.getClass().getSimpleName(), "Error occurred while coping external db"+e);
        }
    }

    private void updateResultTextview()
    {
        resultTextView.setText("");
        resultTextView.setText(getCountQueryResult());
    }

    private void showWarningDialog()
    {
        DialogConfiguration dialogConfiguration = new DialogConfiguration(getString(R.string.warning),
                getString(R.string.message_database_invalid));
        CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(getActivity(), dialogConfiguration);
        customDialogBuilder.getBuilder().setCancelable(false);
        customDialogBuilder.getBuilder().setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                try {
                    songDao.close();
                    songDao.copyDatabase("", true);
                    songDao.open();
                    dialog.cancel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        customDialogBuilder.getBuilder().show();
    }

    public String getCountQueryResult()
    {
        long count = songDao.count();
        return "select count(*) from songs \nResult: "+count;
    }
}
