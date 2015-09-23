package org.worshipsongs.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import org.worshipsongs.worship.R;

import java.util.Arrays;
import java.util.List;

/**
 * Author:Madasamy
 * version:1.0.0
 */
public class FontStyleActivity extends Activity
{
    private ArrayAdapter adapter;
    private ListView listView;
    private Spinner fontSpinner;
    private Button doneButton;
    final List<String> stringList = Arrays.asList("DEFAULT", "DEFAULT_BOLD", "MONOSPACE", "SANS_SERIF", "SERIF");
    private SharedPreferences fontStylePreference;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.font_style_tab);
            fontSpinner = (Spinner)findViewById(R.id.fontStyleSpinner);
            doneButton = (Button) findViewById(R.id.doneButton);
            fontStylePreference = PreferenceManager.getDefaultSharedPreferences(FontStyleActivity.this);
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stringList);
            fontSpinner.setAdapter(adapter);
            String fontFace = fontStylePreference.getString("fontStyle", "DEFAULT");
            fontSpinner.setSelection(stringList.indexOf(fontFace));
            fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    SharedPreferences fontStylePreference = PreferenceManager.getDefaultSharedPreferences(FontStyleActivity.this);
                    SharedPreferences.Editor editor = fontStylePreference.edit();
                    editor.putString("fontStyle", stringList.get(i));
                    editor.commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } catch (Exception e) {
        }

        doneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String fontFace = fontStylePreference.getString("fontStyle", "DEFAULT");
                Log.i("Font", "Font Face: " + fontFace
                );
                finish();
            }
        });

    }
}
