package org.worshipsongs.fragment

import android.support.v4.app.Fragment
import android.view.View
import android.widget.TextView

import org.apache.commons.lang3.StringUtils
import org.worshipsongs.R

/**
 * Author : Madasamy
 * Version : 3.2.x
 */
open class AbstractTabFragment : Fragment()
{
    protected fun setCountView(countTextView: TextView, count: String)
    {
        countTextView.text = getString(R.string.no_of_songs, count)
        countTextView.visibility = if (StringUtils.isNotBlank(countTextView.text)) View.VISIBLE else View.GONE
    }
}
