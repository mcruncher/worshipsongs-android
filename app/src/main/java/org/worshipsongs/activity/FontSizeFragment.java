package org.worshipsongs.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import org.worshipsongs.worship.R;

/**
 * Created by Seenivasan on 4/26/2015.
 */
public class FontSizeFragment extends Fragment {

    private LinearLayout FragmentLayout;
    private FragmentActivity FragmentActivity;
    private Button okButton;
    int fontSizeValue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity = (FragmentActivity) super.getActivity();
        FragmentLayout = (LinearLayout) inflater.inflate(R.layout.font_size_tab, container, false);
        setHasOptionsMenu(true);
        SharedPreferences customSharedPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //      fontSizeTextView = (TextView) findViewById(R.id.fontSizeTextView);
        okButton = (Button)FragmentLayout.findViewById(R.id.fontSizeOkButton);
        //cancelButton = (Button) findViewById(R.id.fontSizeCancelButton);
        SeekBar fontSizeSeekBar = (SeekBar)FragmentLayout.findViewById(R.id.fontSizeSeekBar);
        final int fontSize = customSharedPreference.getInt("fontSize", 10);
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
                getActivity().finish();
            }
        });
        return FragmentLayout;
    }

    private void saveFontSizePreference()
    {
        SharedPreferences fontSizePreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = fontSizePreference.edit();
        editor.putInt("fontSize", fontSizeValue);
        editor.commit();
    }
}
