package org.worshipsongs.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.utils.PropertyUtils;
import org.worshipsongs.worship.R;

/**
 * Author:Madasamy
 * version:1.0.0
 */
public class FontDialogPreference extends Preference
{
    private int portraitFontSize;
    private int landscapeFontSize;

    public FontDialogPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setPersistent(true);
    }

    @Override
    protected void onClick()
    {
        super.onClick();
        LayoutInflater layoutInflater = LayoutInflater.from(FontDialogPreference.this.getContext());
        SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(FontDialogPreference.this.getContext());
        View promptsView = layoutInflater.inflate(R.layout.font_size_dialog, null);
        SeekBar fontSizeSeekBar = (SeekBar) promptsView.findViewById(R.id.portrait_font_size);
        portraitFontSize = customSharedPreference.getInt(CommonConstants.PORTRAIT_FONT_SIZE_KEY, 20);
        fontSizeSeekBar.setProgress(portraitFontSize);
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                portraitFontSize = progress;
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

        SeekBar landScapeSeekbar = (SeekBar) promptsView.findViewById(R.id.landscape_font_size);
        landscapeFontSize = customSharedPreference.getInt(CommonConstants.LANDSCAPE_FONT_SIZE_KEY, 28);
        landScapeSeekbar.setProgress(landscapeFontSize);
        landScapeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

                landscapeFontSize = progress;
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FontDialogPreference.this.getContext());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(true).setPositiveButton(getContext().getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                saveFontSizePreference(CommonConstants.LANDSCAPE_FONT_SIZE_KEY, landscapeFontSize);
                saveFontSizePreference(CommonConstants.PORTRAIT_FONT_SIZE_KEY, portraitFontSize);
                dialog.cancel();
            }
        }).setNegativeButton(getContext().getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void saveFontSizePreference(String key, int fontSize)
    {
        SharedPreferences fontSizePreference = PreferenceManager.getDefaultSharedPreferences(FontDialogPreference.this.getContext());
        SharedPreferences.Editor editor = fontSizePreference.edit();
        editor.putInt(key, fontSize);
        editor.commit();
    }
}
