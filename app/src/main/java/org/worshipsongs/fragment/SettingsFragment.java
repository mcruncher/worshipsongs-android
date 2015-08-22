package org.worshipsongs.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.worshipsongs.worship.R;

/**
 * author:Madasamy
 * version: 2.1.0
 */
//New
public class SettingsFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.settings_fragment, container, false);

       // getFragmentManager().beginTransaction().replace(R.id.setting_preference, new SettingsPreferenceFragment()).commit();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getActivity().getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsPreferenceFragment())
                .commit();
    }
}
