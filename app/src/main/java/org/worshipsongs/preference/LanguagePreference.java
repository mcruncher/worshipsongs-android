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
    private LanguageListener languageListener;
    private int languageIndex;

    public LanguagePreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setPersistent(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (attrs != null) {
            String defaultLanguage = attrs.getAttributeValue(null, "defaultLanguage");
            languageIndex = "tamil".equalsIgnoreCase(defaultLanguage) ? 0 : 1;
        }
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
        int index = sharedPreferences.getInt(CommonConstants.LANGUAGE_INDEX_KEY, languageIndex);
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
                    setLocale("ta");
                    sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 0).apply();
                } else {
                    setLocale("en");
                    sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 1).apply();
                }
            }
        });
    }

    private void setLocale(String localeKey)
    {
        Locale configLocale = new Locale(localeKey);
        Locale.setDefault(configLocale);
        Configuration config = new Configuration();
        config.locale = configLocale;
        getContext().getResources().updateConfiguration(config, getContext().getResources().getDisplayMetrics());
        languageListener.onSelect();
    }

    public void setLanguageListener(LanguageListener languageListener)
    {
        this.languageListener = languageListener;
    }

    public interface LanguageListener
    {
        void onSelect();
    }
}
