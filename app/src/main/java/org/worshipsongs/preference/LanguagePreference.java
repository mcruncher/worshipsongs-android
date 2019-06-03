package org.worshipsongs.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.R;
import org.worshipsongs.utils.CommonUtils;

import java.util.Locale;


/**
 * Author : Madasamy
 * Version : 3.x
 */

public class LanguagePreference extends Preference
{
    private SharedPreferences sharedPreferences;
    private PreferenceListener preferenceListener;
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
        setLanguagePreferenceProperties(index);
        setLocale((index == 0) ? "ta" : "en");
        languageTypeRadioGroup.check(index == 0 ? R.id.language_tamil : R.id.language_english);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.textColor, typedValue, true);
        AppCompatRadioButton tamilRadioButton = view.findViewById(R.id.language_tamil);
        tamilRadioButton.setTextColor(typedValue.data);
        tamilRadioButton.setVisibility(CommonUtils.isAboveKitkat() ? View.VISIBLE : View.GONE);
        ((AppCompatRadioButton) view.findViewById(R.id.language_english)).setTextColor(typedValue.data);
        languageTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId)
            {
                RadioButton foodOrSupplementType = (RadioButton) view.findViewById(checkedId);
                if (foodOrSupplementType.getId() == R.id.language_tamil) {
                    setLocaleAndSelectListener("ta");
                    sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 0).apply();
                } else {
                    setLocaleAndSelectListener("en");
                    sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 1).apply();
                }
                sharedPreferences.edit().putBoolean(CommonConstants.UPDATE_NAV_ACTIVITY_KEY, true).apply();
            }
        });
    }

    private void setLanguagePreferenceProperties(int index)
    {
        sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, index).apply();
        sharedPreferences.edit().putBoolean(CommonConstants.LANGUAGE_CHOOSED_KEY, true).apply();
    }

    private void setLocaleAndSelectListener(String localeKey)
    {
        setLocale(localeKey);
        preferenceListener.onSelect();
    }

    private void setLocale(String localeKey)
    {
        Locale configLocale = new Locale(localeKey);
        Locale.setDefault(configLocale);
        Configuration config = new Configuration();
        config.locale = configLocale;
        getContext().getResources().updateConfiguration(config, getContext().getResources().getDisplayMetrics());
    }

    public void setPreferenceListener(PreferenceListener preferenceListener)
    {
        this.preferenceListener = preferenceListener;
    }
}
