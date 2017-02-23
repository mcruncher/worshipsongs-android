package org.worshipsongs.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.worshipsongs.worship.R;

/**
 * Author:Madasamy
 * version:1.0.0
 */
public class FontDialogPreference extends Preference
{
    private int fontSize;
    private int defaultFontSize;
    private int maxSize = 100;
    private SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(FontDialogPreference.this.getContext());
    private TextView fontSizetextView;

    public FontDialogPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setPersistent(true);
        if (attrs != null) {
            maxSize = attrs.getAttributeIntValue(null, "maxSize", 20);
        }
    }

    @Override
    protected View onCreateView(ViewGroup parent)
    {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.font_size_dialog, parent, false);
        setFontSizeSeekBar(view);
        setFontSizeTextView(view);
        setMaxSizeTextView(view);
        return view;
    }

    private void setFontSizeSeekBar(View view)
    {
        SeekBar fontSizeSeekBar = (SeekBar) view.findViewById(R.id.portrait_font_size);
        fontSizeSeekBar.setMax(maxSize);
        fontSize = customSharedPreference.getInt(getKey(), defaultFontSize);
        fontSizeSeekBar.setProgress(fontSize);
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                fontSize = progress;
                String text = getContext().getResources().getString(R.string.fontSize) + ": " + fontSize;
                fontSizetextView.setText(text);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                saveFontSizePreference(getKey(), fontSize);
            }
        });
    }

    private void setFontSizeTextView(View view)
    {
        fontSizetextView = (TextView) view.findViewById(R.id.fontSize);
        String text = getContext().getResources().getString(R.string.fontSize) + ": " + fontSize;
        fontSizetextView.setText(text);
    }

    private void setMaxSizeTextView(View view)
    {
        TextView textView = (TextView) view.findViewById(R.id.maxsize_textView);
        textView.setText(String.valueOf(maxSize));
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue)
    {
        setDefaultValue(restore ? getPersistedInt(20) : (Integer) defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
        defaultFontSize = a.getInteger(index, 20);
        return defaultFontSize;
    }

    private void saveFontSizePreference(String key, int fontSize)
    {
        SharedPreferences fontSizePreference = PreferenceManager.getDefaultSharedPreferences(FontDialogPreference.this.getContext());
        SharedPreferences.Editor editor = fontSizePreference.edit();
        editor.putInt(key, fontSize);
        editor.apply();
    }
}
