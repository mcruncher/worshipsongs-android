package org.worshipsongs.preference

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import kotlinx.android.synthetic.main.language_layout.view.*
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.utils.CommonUtils
import java.util.*


/**
 * @author Madasamy
 * @since 3.x
 */

class LanguagePreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs)
{
    private var preferenceListener: PreferenceListener? = null
    private var languageIndex: Int = 0

    init
    {
        layoutResource = R.layout.language_layout
        isPersistent = true

        if (attrs != null)
        {
            val defaultLanguage = attrs.getAttributeValue(null, "defaultLanguage")
            languageIndex = if ("tamil".equals(defaultLanguage, ignoreCase = true)) 0 else 1
        }

    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?)
    {
        super.onBindViewHolder(holder)
        with(holder!!.itemView){
            setLanguageRadioGroup(type)
        }
    }

    private fun setLanguageRadioGroup(radioGroup: RadioGroup?)
    {
        val index = sharedPreferences.getInt(CommonConstants.LANGUAGE_INDEX_KEY, languageIndex)
        setLanguagePreferenceProperties(index)
        setLocale(if (index == 0) "ta" else "en")
        radioGroup!!.check(if (index == 0) R.id.language_tamil else R.id.language_english)
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
        val tamilRadioButton = radioGroup.findViewById<AppCompatRadioButton>(R.id.language_tamil)
        tamilRadioButton.setTextColor(typedValue.data)
        tamilRadioButton.visibility = if (CommonUtils.isAboveKitkat) View.VISIBLE else View.GONE
        (radioGroup.findViewById<View>(R.id.language_english) as AppCompatRadioButton).setTextColor(typedValue.data)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val foodOrSupplementType = radioGroup.findViewById<View>(checkedId) as RadioButton
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
