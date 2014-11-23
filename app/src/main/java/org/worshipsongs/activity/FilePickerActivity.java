package org.worshipsongs.activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.worshipsongs.worship.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author : Seenivasan
 * @Version : 1.0
 */
public class FilePickerActivity extends ListActivity
{
    public final static String EXTRA_FILE_PATH = "file_path";
    public final static String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";
    public final static String EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions";
    private final static String DEFAULT_INITIAL_DIRECTORY = "/";

    protected File externalStorageDirPath;
    protected ArrayList<File> files;
    protected FilePickerListAdapter filePickerListAdapter;
    protected boolean showHiddenFiles = false;
    protected String[] acceptedFileExtensions;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LayoutInflater inflator = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View emptyView = inflator.inflate(R.layout.file_picker_empty_view, null);
        ((ViewGroup) getListView().getParent()).addView(emptyView);
        getListView().setEmptyView(emptyView);
        // Set initial directory
        externalStorageDirPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        // Initialize the ArrayList
        files = new ArrayList<File>();
        // Set the ListAdapter
        filePickerListAdapter = new FilePickerListAdapter(this, files);
        setListAdapter(filePickerListAdapter);
        // Initialize the extensions array to allow any file extensions
        acceptedFileExtensions = new String[]{};
        // Get intent extras
        if (getIntent().hasExtra(EXTRA_FILE_PATH)) {
            externalStorageDirPath = new File(getIntent().getStringExtra(EXTRA_FILE_PATH));
        }

        if (getIntent().hasExtra(EXTRA_SHOW_HIDDEN_FILES)) {
            showHiddenFiles = getIntent().getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false);
        }

        if (getIntent().hasExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS)) {
            ArrayList<String> collection =
                    getIntent().getStringArrayListExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS);
            acceptedFileExtensions = (String[])
                    collection.toArray(new String[collection.size()]);
        }
    }

    @Override
    protected void onResume()
    {
        refreshFilesList();
        super.onResume();
    }

    protected void refreshFilesList()
    {
        files.clear();
        ExtensionFilenameFilter filter =
                new ExtensionFilenameFilter(acceptedFileExtensions);
        File[] files = externalStorageDirPath.listFiles(filter);
        if (files != null && files.length > 0) {
            for (File f : files) {
                if (f.isHidden() && !showHiddenFiles) {
                    continue;
                }
                this.files.add(f);
            }
            Collections.sort(this.files, new FileComparator());
        }
        filePickerListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed()
    {
        if (externalStorageDirPath.getParentFile() != null) {
            externalStorageDirPath = externalStorageDirPath.getParentFile();
            refreshFilesList();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id)
    {
        File newFile = (File) listView.getItemAtPosition(position);
        if (newFile.isFile()) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_FILE_PATH, newFile.getAbsolutePath());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            externalStorageDirPath = newFile;
            refreshFilesList();
        }
        super.onListItemClick(listView, view, position, id);
    }

    private class FilePickerListAdapter extends ArrayAdapter<File>
    {
        private List<File> fileList;

        public FilePickerListAdapter(Context context, List<File> fileList)
        {
            super(context, R.layout.list_item, android.R.id.text1, fileList);
            this.fileList = fileList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View rowView = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.list_item, parent, false);
            } else
                rowView = convertView;
            File object = fileList.get(position);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.file_picker_image);
            imageView.setMaxWidth(10);
            TextView textView = (TextView) rowView.findViewById(R.id.file_picker_text);
            textView.setSingleLine(true);
            textView.setText(object.getName());
            textView.setTextSize(10);
            textView.setTypeface(Typeface.SANS_SERIF);
            if (object.isFile()) {
                imageView.setImageResource(R.drawable.file);
            } else {
                imageView.setImageResource(R.drawable.folder);
            }
            return rowView;
        }
    }

    private class FileComparator implements Comparator<File>
    {
        public int compare(File file1, File file2)
        {
            if (file1 == file2) {
                return 0;
            }
            if (file1.isDirectory() && file2.isFile()) {
                // Show directories above files
                return -1;
            }
            if (file1.isFile() && file2.isDirectory()) {
                // Show files below directories
                return 1;
            }
            // Sort the directories alphabetically
            return file1.getName().compareToIgnoreCase(file2.getName());
        }
    }

    private class ExtensionFilenameFilter implements FilenameFilter
    {
        private String[] extensions;

        public ExtensionFilenameFilter(String[] extensions)
        {
            super();
            this.extensions = extensions;
        }

        public boolean accept(File dir, String filename)
        {
            if (new File(dir, filename).isDirectory()) {
                // Accept all directory names
                return true;
            }

            if (extensions != null && extensions.length > 0) {
                for (int i = 0; i < extensions.length; i++) {
                    if (filename.endsWith(extensions[i])) {
                        // The filename ends with the extension
                        return true;
                    }
                }
                // The filename did not match any of the extensions
                return false;
            }
            // No extensions has been set. Accept all file extensions.
            return true;
        }
    }
}