package org.worshipsongs.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;
import org.worshipsongs.dao.SongDao;
import org.worshipsongs.page.component.fragment.SongsListFragment;
import org.worshipsongs.task.AsyncDownloadTask;
import org.worshipsongs.worship.R;

import java.io.File;


/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class SettingsActivity extends Activity
{
    private static final int REQUEST_PICK_FILE = 1;
    private File selectedFile;
    private SongDao songDao;
    private final Context context = this;
    private ListView settingsMenuList;
    private String settingsMenuValues[];
    private AlertDialog levelDialog;

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        songDao = new SongDao(this);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        settingsMenuValues = getResources().getStringArray(R.array.settings_menu);
        settingsMenuList = (ListView) findViewById(R.id.listView1);
        settingsMenuList.setAdapter(new ListAdapter(this));

        settingsMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override

            public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
            {
                if (position == 0) {
                    AlertDialogView();
                }
                if (position == 1) {
                    Intent aboutIntent = new Intent(SettingsActivity.this, AboutWebViewActivity.class);
                    startActivity(aboutIntent);
                }

            }

        });
    }

    private void AlertDialogView()
    {
        // Strings to Show In Dialog with Radio Buttons
        final CharSequence[] syncDatabaseOption = getResources().getStringArray(R.array.sync_database_options);
        // Creating and Building the Dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sync Database");
        builder.setSingleChoiceItems(syncDatabaseOption, -1, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item)
            {
                switch (item) {
                    case 0:
                        Intent intent = new Intent(SettingsActivity.this, FilePickerActivity.class);
                        intent.setType("storage/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, REQUEST_PICK_FILE);
                        break;

                    case 1:
                        //Download database file from remote url
                        // Your code when 2nd  option seletced
                        downloadDialogBuilder();
                        break;
                    case 2:
                        // Your code when 3rd option seletced
                        break;
                    case 3:
                        // Your code when 4th  option seletced
                        break;

                }


            }
        });
        levelDialog = builder.create();
        levelDialog.show();
    }

    private void downloadDialogBuilder()
    {
        final AsyncDownloadTask asyncDownloadTask = new AsyncDownloadTask();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.remoteUrlTitle));

        // Set an EditText view to get user remoteUrlEditText
        final EditText remoteUrlEditText = new EditText(this);
        remoteUrlEditText.setText(R.string.remoteUrl);
        alertDialogBuilder.setView(remoteUrlEditText);

        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String remoteUrl = remoteUrlEditText.getText().toString();
                File externalCacheDir = context.getExternalCacheDir();
                String extension = FilenameUtils.getExtension(remoteUrl);
                if (extension.equalsIgnoreCase("sqlite")) {
                    File downloadSongFile = null;
                    try {
                        downloadSongFile = File.createTempFile("downloadsongs", "sqlite", externalCacheDir);
                        if (asyncDownloadTask.execute(remoteUrl, downloadSongFile.getAbsolutePath()).get()) {
                            songDao.copyDatabase(downloadSongFile.getAbsolutePath(), true);
                        } else {
                            Log.w(SettingsActivity.class.getSimpleName(), "File is not downloaded from " + remoteUrl);
                        }
                    } catch (Exception e) {
                        Log.e(SettingsActivity.class.getSimpleName(), "Error occurred while downloading file" + e);
                    } finally {
                        downloadSongFile.deleteOnExit();
                    }
                } else {
                    Toast.makeText(SettingsActivity.this, "Sqlite file only supported", Toast.LENGTH_LONG).show();
                }
            }
        });

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
            }
        });
        alertDialogBuilder.show();
    }

    private class ListAdapter extends BaseAdapter
    {
        private LayoutInflater inflater;

        public ListAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            TextView txt_view1;
            convertView = inflater.inflate(R.layout.settings_list_textview, null);
            txt_view1 = (TextView) convertView.findViewById(R.id.textView1);
            txt_view1.setText(settingsMenuValues[position]);
            return convertView;
        }

        public int getCount()
        {
            return settingsMenuValues.length;
        }

        public Object getItem(int position)
        {
            return position;
        }

        public long getItemId(int position)
        {
            return position;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_FILE:
                    if (data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {
                        levelDialog.dismiss();
                        selectedFile = new File
                                (data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
                        String extension = FilenameUtils.getExtension(selectedFile.getAbsolutePath());
                        if (extension.equalsIgnoreCase("sqlite")) {
                            try {
                                songDao.copyDatabase(selectedFile.getAbsolutePath(), true);
                                Toast.makeText(SettingsActivity.this, "Database is loaded from the file Path: " + selectedFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, SongsListFragment.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } catch (Exception e) {

                            }
                        } else {
                            Toast.makeText(SettingsActivity.this, "Sqlite file only supported", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                intent = new Intent(this, SongsViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}