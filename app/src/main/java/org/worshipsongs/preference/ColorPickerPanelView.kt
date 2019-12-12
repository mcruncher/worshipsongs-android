package org.worshipsongs.preference


import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * @author: Madasamy
 * @version: 3.3.x
 */
class ColorPickerPanelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle)
{

    private var mDensity = 1f

    /**
     * Get the color of the border surrounding the panel.
     */
    /**
     * Set the color of the border surrounding the panel.
     *
     * @param color
     */
    var borderColor = -0x919192
        set(color)
        {
            field = color
            invalidate()
        }
    /**
     * Get the color currently show by this view.
     *
     * @return
     */
    /**
     * Set the color that should be shown by this view.
     *
     * @param color
     */
    var color = -0x1000000
        set(color)
        {
            field = color
            invalidate()
        }

    private var mBorderPaint: Paint? = null
    private var mColorPaint: Paint? = null

    private var mDrawingRect: RectF? = null
    private var mColorRect: RectF? = null

    private var mAlphaPattern: AlphaPatternDrawable? = null

    init
    {
        init()
    }

    private fun init()
    {
        mBorderPaint = Paint()
        mColorPaint = Paint()
        mDensity = context.resources.displayMetrics.density
    }


    override fun onDraw(canvas: Canvas)
    {

        val rect = mColorRect

        if (BORDER_WIDTH_PX > 0)
        {
            mBorderPaint!!.color = borderColor
            canvas.drawRect(mDrawingRect!!, mBorderPaint!!)
        }

        if (mAlphaPattern != null)
        {
            mAlphaPattern!!.draw(canvas)
        }

        mColorPaint!!.color = color

        canvas.drawRect(rect!!, mColorPaint!!)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {

        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
    {
        super.onSizeChanged(w, h, oldw, oldh)

        mDrawingRect = RectF()
        mDrawingRect!!.left = paddingLeft.toFloat()
        mDrawingRect!!.right = (w - paddingRight).toFloat()
        mDrawingRect!!.top = paddingTop.toFloat()
        mDrawingRect!!.bottom = (h - paddingBottom).toFloat()

        setUpColorRect()

    }

    private fun setUpColorRect()
    {
        val dRect = mDrawingRect

        val left = dRect!!.left + BORDER_WIDTH_PX
        val top = dRect.top + BORDER_WIDTH_PX
        val bottom = dRect.bottom - BORDER_WIDTH_PX
        val right = dRect.right - BORDER_WIDTH_PX

        mColorRect = RectF(left, top, right, bottom)

        mAlphaPattern = AlphaPatternDrawable((5 * mDensity).toInt())

        mAlphaPattern!!.setBounds(Math.round(mColorRect!!.left), Math.round(mColorRect!!.top), Math.round(mColorRect!!.right), Math.round(mColorRect!!.bottom))

    }

    companion object
    {

        /**
         * The width in pixels of the border
         * surrounding the color panel.
         */
        private val BORDER_WIDTH_PX = 1f
    }
}
