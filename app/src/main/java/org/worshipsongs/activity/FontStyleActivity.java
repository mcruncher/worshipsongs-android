package org.worshipsongs.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.font_style_tab);
            final ListView listView = (ListView) findViewById(R.id.fontStyle);
            final TextView textView = (TextView) findViewById(R.id.fontStyleTextView);
            SharedPreferences fontStylePreference = PreferenceManager.getDefaultSharedPreferences(FontStyleActivity.this);
            textView.setText("Font face: " + fontStylePreference.getString("fontStyle", "DEFAULT"));

            final List<String> stringList = Arrays.asList("DEFAULT", "DEFAULT_BOLD", "MONOSPACE", "SANS_SERIF", "SERIF");
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    textView.setText("Font face: " + stringList.get(position));
                    SharedPreferences fontStylePreference = PreferenceManager.getDefaultSharedPreferences(FontStyleActivity.this);
                    SharedPreferences.Editor editor = fontStylePreference.edit();
                    String[] split = textView.getText().toString().split(":");
                    editor.putString("fontStyle", split[1]);
                    editor.commit();
                }
            });
        } catch (Exception e) {
        }
    }
}
