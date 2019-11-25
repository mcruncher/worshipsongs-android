package org.worshipsongs.preference

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration

import android.os.Build
import android.preference.Preference
import android.preference.PreferenceManager
import android.support.annotation.IdRes
import android.support.v7.widget.AppCompatRadioButton
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup

import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.utils.CommonUtils

import java.util.Locale


/**
 * Author : Madasamy
 * Version : 3.x
 */

class LanguagePreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs)
{
    private var preferenceListener: PreferenceListener? = null
    private var languageIndex: Int = 0

    init
    {
        isPersistent = true

        if (attrs != null)
        {
            val defaultLanguage = attrs.getAttributeValue(null, "defaultLanguage")
            languageIndex = if ("tamil".equals(defaultLanguage, ignoreCase = true)) 0 else 1
        }
    }

    override fun onCreateView(parent: ViewGroup): View
    {
        super.onCreateView(parent)
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.language_layout, parent, false)
        setLanguageRadioGroup(view)
        return view
    }

    private fun setLanguageRadioGroup(view: View)
    {
        val languageTypeRadioGroup = view.findViewById<View>(R.id.type) as RadioGroup
        val index = sharedPreferences.getInt(CommonConstants.LANGUAGE_INDEX_KEY, languageIndex)
        setLanguagePreferenceProperties(index)
        setLocale(if (index == 0) "ta" else "en")
        languageTypeRadioGroup.check(if (index == 0) R.id.language_tamil else R.id.language_english)
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
        val tamilRadioButton = view.findViewById<AppCompatRadioButton>(R.id.language_tamil)
        tamilRadioButton.setTextColor(typedValue.data)
        tamilRadioButton.visibility = if (CommonUtils.isAboveKitkat()) View.VISIBLE else View.GONE
        (view.findViewById<View>(R.id.language_english) as AppCompatRadioButton).setTextColor(typedValue.data)
        languageTypeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val foodOrSupplementType = view.findViewById<View>(checkedId) as RadioButton
            if (foodOrSupplementType.id == R.id.language_tamil)
            {
                setLocaleAndSelectListener("ta")
                sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 0).apply()
            } else
            {
                setLocaleAndSelectListener("en")
                sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, 1).apply()
            }
            sharedPreferences.edit().putBoolean(CommonConstants.UPDATE_NAV_ACTIVITY_KEY, true).apply()
        }
    }

    private fun setLanguagePreferenceProperties(index: Int)
    {
        sharedPreferences.edit().putInt(CommonConstants.LANGUAGE_INDEX_KEY, index).apply()
        sharedPreferences.edit().putBoolean(CommonConstants.LANGUAGE_CHOOSED_KEY, true).apply()
    }

    private fun setLocaleAndSelectListener(localeKey: String)
    {
        setLocale(localeKey)
        preferenceListener!!.onSelect()
    }

    private fun setLocale(localeKey: String)
    {
        val configLocale = Locale(localeKey)
        Locale.setDefault(configLocale)
        val config = Configuration()
        config.locale = configLocale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun setPreferenceListener(preferenceListener: PreferenceListener)
    {
        this.preferenceListener = preferenceListener
    }
}
