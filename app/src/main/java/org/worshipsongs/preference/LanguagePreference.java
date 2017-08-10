package org.worshipsongs.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;

import java.util.Locale;


/**
 * Author : Madasamy
 * Version : 3.x
 */

public class LanguagePreference extends Preference
{
    private SharedPreferences sharedPreferences;

    public LanguagePreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setPersistent(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent)
    {
        super.onCreateView(parent);
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.language_layout, parent, false);
        setLanguageRadioGroup(view);
        return view;
    }

    private void setLanguageRadioGroup(final View view)
    {
        RadioGroup languageTypeRadioGroup = (RadioGroup) view.findViewById(R.id.type);
        int index = sharedPreferences.getInt(CommonConstants.LANGUAGE_INDEX_KEY, 0);
        if (index == 0) {
            languageTypeRadioGroup.check(R.id.language_tamil);
        } else {
            languageTypeRadioGroup.check(R.id.language_english);
        }
        languageTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
            {
                RadioButton foodOrSupplementType = (RadioButton) view.findViewById(checkedId);
                if (foodOrSupplementType.getId() == R.id.language_tamil) {
                    Locale configureLocale = new Locale("ta");
                    setLocale(configureLocale);
                    sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 0).apply();
                } else {
                    Locale configureLocale = new Locale("en");
                    setLocale(configureLocale);
                    sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 1).apply();
                }
            }
        });
    }

    private void setLocale(Locale configureLocale)
    {
        Locale.setDefault(configureLocale);
        Configuration config = new Configuration();
        config.locale = configureLocale;
        getContext().getResources().updateConfiguration(config, getContext().getResources().getDisplayMetrics());
    }
}
