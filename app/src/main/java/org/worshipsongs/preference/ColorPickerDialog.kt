package org.worshipsongs.preference


import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import org.worshipsongs.R
import java.util.*

/**
 * @author: Madasamy
 * @version: 3.3.x
 */
class ColorPickerDialog(context: Context, initialColor: Int) : Dialog(context), ColorPickerView.OnColorChangedListener, View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener
{

    private var mColorPicker: ColorPickerView? = null

    private var mOldColor: ColorPickerPanelView? = null
    private var mNewColor: ColorPickerPanelView? = null

    private var mHexVal: EditText? = null
    var hexValueEnabled = false
        set(enable)
        {
            field = enable
            if (enable)
            {
                mHexVal!!.visibility = View.VISIBLE
                updateHexLengthFilter()
                updateHexValue(color)
            } else mHexVal!!.visibility = View.GONE
        }
    private var mHexDefaultTextColor: ColorStateList? = null

    private var mListener: OnColorChangedListener? = null
    private var mOrientation: Int = 0
    private var mLayout: View? = null

    var alphaSliderVisible: Boolean
        get() = mColorPicker!!.alphaSliderVisible
        set(visible)
        {
            mColorPicker!!.alphaSliderVisible = visible
            if (hexValueEnabled)
            {
                updateHexLengthFilter()
                updateHexValue(color)
            }
        }

    val color: Int
        get() = mColorPicker!!.color

    override fun onGlobalLayout()
    {
        if (context.resources.configuration.orientation != mOrientation)
        {
            val oldcolor = mOldColor!!.color
            val newcolor = mNewColor!!.color
            mLayout!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
            setUp(oldcolor)
            mNewColor!!.color = newcolor
            mColorPicker!!.color = newcolor
        }
    }

    interface OnColorChangedListener
    {
        fun onColorChanged(color: Int)
    }

    init
    {

        init(initialColor)
    }

    private fun init(color: Int)
    {
        // To fight color banding.
        window!!.setFormat(PixelFormat.RGBA_8888)

        setUp(color)

    }

    private fun setUp(color: Int)
    {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mLayout = inflater.inflate(R.layout.dialog_color_picker, null)
        mLayout!!.viewTreeObserver.addOnGlobalLayoutListener(this)

        mOrientation = context.resources.configuration.orientation
        setContentView(mLayout!!)

        setTitle(R.string.dialog_color_picker)

        mColorPicker = mLayout!!.findViewById<View>(R.id.color_picker_view) as ColorPickerView
        mOldColor = mLayout!!.findViewById<View>(R.id.old_color_panel) as ColorPickerPanelView
        mNewColor = mLayout!!.findViewById<View>(R.id.new_color_panel) as ColorPickerPanelView

        mHexVal = mLayout!!.findViewById<View>(R.id.hex_val) as EditText
        mHexVal!!.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        mHexDefaultTextColor = mHexVal!!.textColors

        mHexVal!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
            {
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                val s = mHexVal!!.text.toString()
                if (s.length > 5 || s.length < 10)
                {
                    try
                    {
                        val c = ColorPickerPreference.convertToColorInt(s)
                        mColorPicker!!.setColor(c, true)
                        mHexVal!!.setTextColor(mHexDefaultTextColor)
                    } catch (e: IllegalArgumentException)
                    {
                        mHexVal!!.setTextColor(Color.RED)
                    }

                } else
                {
                    mHexVal!!.setTextColor(Color.RED)
                }
                return@OnEditorActionListener true
            }
            false
        })

        (mOldColor!!.parent as LinearLayout).setPadding(Math.round(mColorPicker!!.drawingOffset), 0, Math.round(mColorPicker!!.drawingOffset), 0)

        mOldColor!!.setOnClickListener(this)
        mNewColor!!.setOnClickListener(this)
        mColorPicker!!.setOnColorChangedListener(this)
        mOldColor!!.color = color
        mColorPicker!!.setColor(color, true)

    }

    override fun onColorChanged(color: Int)
    {

        mNewColor!!.color = color

        if (hexValueEnabled) updateHexValue(color)

        /*
        if (mListener != null) {
			mListener.onColorChanged(color);
		}
		*/

    }

    private fun updateHexLengthFilter()
    {
        if (alphaSliderVisible) mHexVal!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9))
        else mHexVal!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(7))
    }

    private fun updateHexValue(color: Int)
    {
        if (alphaSliderVisible)
        {
            mHexVal!!.setText(ColorPickerPreference.convertToARGB(color).toUpperCase(Locale.getDefault()))
        } else
        {
            mHexVal!!.setText(ColorPickerPreference.convertToRGB(color).toUpperCase(Locale.getDefault()))
        }
        mHexVal!!.setTextColor(mHexDefaultTextColor)
    }

    /**
     * Set a OnColorChangedListener to get notified when the color
     * selected by the user has changed.
     *
     * @param listener
     */
    fun setOnColorChangedListener(listener: OnColorChangedListener)
    {
        mListener = listener
    }

    override fun onClick(v: View)
    {
        if (v.id == R.id.new_color_panel)
        {
            if (mListener != null)
            {
                mListener!!.onColorChanged(mNewColor!!.color)
            }
        }
        dismiss()
    }

    override fun onSaveInstanceState(): Bundle
    {
        val state = super.onSaveInstanceState()
        state.putInt("old_color", mOldColor!!.color)
        state.putInt("new_color", mNewColor!!.color)
        return state
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle)
    {
        super.onRestoreInstanceState(savedInstanceState)
        mOldColor!!.color = savedInstanceState.getInt("old_color")
        mColorPicker!!.setColor(savedInstanceState.getInt("new_color"), true)
    }
}
