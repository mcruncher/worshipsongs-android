package org.worshipsongs.preference

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Color

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable


import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder


/**
 * @author: Madasamy
 * @version: 3.3.x
 */
class ColorPickerPreference : Preference, Preference.OnPreferenceClickListener, ColorPickerDialog.OnColorChangedListener
{

    internal var mView: View? = null
    internal var mDialog: ColorPickerDialog? = null
    private var mValue = Color.BLACK
    private var mDensity = 0f
    private var mAlphaSliderEnabled = false
    private var mHexValueEnabled = false

    private//30dip
    val previewBitmap: Bitmap
        get()
        {
            val d = (mDensity * 31).toInt()
            val color = mValue
            val bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888)
            val w = bm.width
            val h = bm.height
            var c = color
            for (i in 0 until w)
            {
                for (j in i until h)
                {
                    c = if (i <= 1 || j <= 1 || i >= w - 2 || j >= h - 2) Color.GRAY else color
                    bm.setPixel(i, j, c)
                    if (i != j)
                    {
                        bm.setPixel(j, i, c)
                    }
                }
            }

            return bm
        }

    constructor(context: Context) : super(context)
    {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?)
    {
        mDensity = context.resources.displayMetrics.density
        onPreferenceClickListener = this
        if (attrs != null)
        {
            mAlphaSliderEnabled = attrs.getAttributeBooleanValue(null, "alphaSlider", false)
            mHexValueEnabled = attrs.getAttributeBooleanValue(null, "hexValue", false)
        }
    }

    /**
     * Method edited by
     *
     * @author Anna Berkovitch
     * added functionality to accept hex string as defaultValue
     * and to properly persist resources reference string, such as @color/someColor
     * previously persisted 0
     */
    override fun onGetDefaultValue(a: TypedArray, index: Int): Any
    {
        val colorInt: Int
        val mHexDefaultValue = a.getString(index)
        if (mHexDefaultValue != null && mHexDefaultValue.startsWith("#"))
        {
            colorInt = convertToColorInt(mHexDefaultValue)
            return colorInt

        } else
        {
            return a.getColor(index, Color.BLACK)
        }
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?)
    {
        onColorChanged(if (restoreValue) getPersistedInt(mValue) else defaultValue as Int)
    }


    override fun onBindViewHolder(holder: PreferenceViewHolder)
    {
        super.onBindViewHolder(holder)
        mView = holder.itemView
        setPreviewColor()
    }

    private fun setPreviewColor()
    {
        if (mView == null) return
        val iView = ImageView(context)
        val widgetFrameView = mView!!.findViewById<View>(android.R.id.widget_frame) as LinearLayout
                ?: return
        widgetFrameView.visibility = View.VISIBLE
        widgetFrameView.setPadding(widgetFrameView.paddingLeft, widgetFrameView.paddingTop, (mDensity * 8).toInt(), widgetFrameView.paddingBottom)
        // remove already create preview image
        val count = widgetFrameView.childCount
        if (count > 0)
        {
            widgetFrameView.removeViews(0, count)
        }
        widgetFrameView.addView(iView)
        widgetFrameView.minimumWidth = 0
        iView.setBackgroundDrawable(AlphaPatternDrawable((5 * mDensity).toInt()))
        iView.setImageBitmap(previewBitmap)
    }

    override fun onColorChanged(color: Int)
    {
        if (isPersistent)
        {
            persistInt(color)
        }
        mValue = color
        setPreviewColor()
        try
        {
            onPreferenceChangeListener.onPreferenceChange(this, color)
        } catch (e: NullPointerException)
        {

        }

    }

    override fun onPreferenceClick(preference: Preference): Boolean
    {
        showDialog(null)
        return false
    }

    protected fun showDialog(state: Bundle?)
    {
        mDialog = ColorPickerDialog(context, mValue)
        mDialog!!.setOnColorChangedListener(this)
        if (mAlphaSliderEnabled)
        {
            mDialog!!.alphaSliderVisible = true
        }
        if (mHexValueEnabled)
        {
            mDialog!!.hexValueEnabled = true
        }
        if (state != null)
        {
            mDialog!!.onRestoreInstanceState(state)
        }
        mDialog!!.show()
    }

    /**
     * Toggle Alpha Slider visibility (by default it's disabled)
     *
     * @param enable
     */
    fun setAlphaSliderEnabled(enable: Boolean)
    {
        mAlphaSliderEnabled = enable
    }

    /**
     * Toggle Hex Value visibility (by default it's disabled)
     *
     * @param enable
     */
    fun setHexValueEnabled(enable: Boolean)
    {
        mHexValueEnabled = enable
    }

    override fun onSaveInstanceState(): Parcelable
    {
        val superState = super.onSaveInstanceState()
        if (mDialog == null || !mDialog!!.isShowing)
        {
            return superState
        }

        val myState = SavedState(superState)
        myState.dialogBundle = mDialog!!.onSaveInstanceState()
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable?)
    {
        if (state == null || state !is SavedState)
        {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state)
            return
        }

        val myState = state as SavedState?
        super.onRestoreInstanceState(myState!!.superState)
        showDialog(myState.dialogBundle)
    }

    private class SavedState : Preference.BaseSavedState
    {
        internal var dialogBundle: Bundle? = null

        constructor(source: Parcel) : super(source)
        {
            dialogBundle = source.readBundle()
        }

        override fun writeToParcel(dest: Parcel, flags: Int)
        {
            super.writeToParcel(dest, flags)
            dest.writeBundle(dialogBundle)
        }

        constructor(superState: Parcelable) : super(superState)
        {
        }

        override fun describeContents(): Int
        {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState>
        {
            override fun createFromParcel(parcel: Parcel): SavedState
            {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?>
            {
                return arrayOfNulls(size)
            }
        }
    }

    companion object
    {

        /**
         * For custom purposes. Not used by ColorPickerPreferrence
         *
         * @param color
         * @author Unknown
         */
        fun convertToARGB(color: Int): String
        {
            var alpha = Integer.toHexString(Color.alpha(color))
            var red = Integer.toHexString(Color.red(color))
            var green = Integer.toHexString(Color.green(color))
            var blue = Integer.toHexString(Color.blue(color))

            if (alpha.length == 1)
            {
                alpha = "0$alpha"
            }

            if (red.length == 1)
            {
                red = "0$red"
            }

            if (green.length == 1)
            {
                green = "0$green"
            }

            if (blue.length == 1)
            {
                blue = "0$blue"
            }

            return "#$alpha$red$green$blue"
        }

        /**
         * Method currently used by onGetDefaultValue method to
         * convert hex string provided in android:defaultValue to color integer.
         *
         * @param color
         * @return A string representing the hex value of color,
         * without the alpha value
         * @author Charles Rosaaen
         */
        fun convertToRGB(color: Int): String
        {
            var red = Integer.toHexString(Color.red(color))
            var green = Integer.toHexString(Color.green(color))
            var blue = Integer.toHexString(Color.blue(color))

            if (red.length == 1)
            {
                red = "0$red"
            }

            if (green.length == 1)
            {
                green = "0$green"
            }

            if (blue.length == 1)
            {
                blue = "0$blue"
            }

            return "#$red$green$blue"
        }

        /**
         * For custom purposes. Not used by ColorPickerPreferrence
         *
         * @param argb
         * @throws NumberFormatException
         * @author Unknown
         */
        @Throws(IllegalArgumentException::class)
        fun convertToColorInt(argb: String): Int
        {
            var argb = argb

            if (!argb.startsWith("#"))
            {
                argb = "#$argb"
            }

            return Color.parseColor(argb)
        }
    }


}
