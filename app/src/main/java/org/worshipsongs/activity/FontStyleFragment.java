package org.worshipsongs.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.worshipsongs.worship.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Seenivasan on 4/26/2015.
 */
public class FontStyleFragment extends Fragment {

    private LinearLayout FragmentLayout;
    private FragmentActivity FragmentActivity;
    private ArrayAdapter adapter;
    private ListView listView;
    private Button doneButton;
    final List<String> stringList = Arrays.asList("DEFAULT", "DEFAULT_BOLD", "MONOSPACE", "SANS_SERIF", "SERIF");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    try {
        FragmentActivity = (FragmentActivity) super.getActivity();
        FragmentLayout = (LinearLayout) inflater.inflate(R.layout.font_style_tab, container, false);
        setHasOptionsMenu(true);
        //listView = (ListView) FragmentLayout.findViewById(R.id.fontStyle);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        doneButton = (Button) FragmentLayout.findViewById(R.id.fontSizeOkButton);
        SharedPreferences fontStylePreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, stringList);
        listView.setAdapter(adapter);
        String fontFace = fontStylePreference.getString("fontStyle", "DEFAULT");

        listView.setItemChecked(stringList.indexOf(fontFace), true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                SharedPreferences fontStylePreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = fontStylePreference.edit();
                editor.putString("fontStyle", stringList.get(position));
                editor.commit();
            }
        });
    } catch (Exception e) {
    }

    doneButton.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
           getActivity().finish();
        }
    });

    return FragmentLayout;
    }

 }
