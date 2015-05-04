package org.worshipsongs.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
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
    private int fontSizeValue;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.font_size_tab);
        SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        okButton = (Button) findViewById(R.id.fontSizeOkButton);
        SeekBar fontSizeSeekBar = (SeekBar) findViewById(R.id.fontSizeSeekBar);
        final int fontSize = customSharedPreference.getInt("fontSize", 14);
        fontSizeSeekBar.setProgress(fontSize);
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                fontSizeValue = progress;
                saveFontSizePreference();
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
                finish();
            }
        });
    }

    private void saveFontSizePreference()
    {
        SharedPreferences fontSizePreference = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = fontSizePreference.edit();
        editor.putInt("fontSize", fontSizeValue);
        editor.commit();
    }
}
