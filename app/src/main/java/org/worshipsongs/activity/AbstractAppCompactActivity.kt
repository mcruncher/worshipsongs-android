package org.worshipsongs.activity

import android.os.Build
import android.os.Bundle

import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.worshipsongs.R
import org.worshipsongs.utils.ThemeUtils

/**
 * @author Madasamy
 * @since 3.3.x
 */
open class AbstractAppCompactActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        ThemeUtils.setTheme(this)
    }

    protected fun setCustomActionBar()
    {
        if (supportActionBar == null)
        {
            setStatusBarColor()
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            toolbar.visibility = View.VISIBLE
            toolbar.setBackgroundColor(getAttributeColor(R.attr.colorPrimary))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                toolbar.elevation = 0f
            }
            setSupportActionBar(toolbar)
        }
    }

    private fun setStatusBarColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getAttributeColor(R.attr.colorPrimaryDark)
        }
    }

    private fun getAttributeColor(attribute: Int): Int
    {
        val typedValue = TypedValue()
        theme.resolveAttribute(attribute, typedValue, true)
        return typedValue.data
    }

}
