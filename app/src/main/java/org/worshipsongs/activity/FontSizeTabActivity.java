package org.worshipsongs.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.worshipsongs.worship.R;

/**
 * Author:Madasamy
 * version:1.0.0
 */
public class FontSizeTabActivity extends Activity
{
    private Button okButton;
    private Button cancelButton;
    private TextView fontSizeTextView;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.font_size_tab);
        SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        fontSizeTextView = (TextView) findViewById(R.id.fontSizeTextView);
        okButton = (Button) findViewById(R.id.fontSizeOkButton);
        cancelButton = (Button) findViewById(R.id.fontSizeCancelButton);
        SeekBar fontSizeSeekBar = (SeekBar) findViewById(R.id.fontSizeSeekBar);
        int fontSize = customSharedPreference.getInt("fontSize", 10);
        fontSizeSeekBar.setProgress(fontSize);
        fontSizeTextView.setText("Font size  :" + fontSize);
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                fontSizeTextView.setText("Font size  :" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveFontSizePreference();
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void saveFontSizePreference()
    {
        SharedPreferences fontSizePreference = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = fontSizePreference.edit();
        String[] fontSizeArray = fontSizeTextView.getText().toString().split(":");
        editor.putInt("fontSize", Integer.valueOf(fontSizeArray[1]));
        editor.commit();
    }
}
