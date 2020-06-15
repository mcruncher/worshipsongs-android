package org.worshipsongs.preference

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


/**
 * @author: Madasamy
 * @version: 3.3.x
 */
class ColorPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle)
{

    /**
     * The width in dp of the hue panel.
     */
    private var HUE_PANEL_WIDTH = 30f
    /**
     * The height in dp of the alpha panel
     */
    private var ALPHA_PANEL_HEIGHT = 20f
    /**
     * The distance in dp between the different
     * color panels.
     */
    private var PANEL_SPACING = 10f
    /**
     * The radius in dp of the color palette tracker circle.
     */
    private var PALETTE_CIRCLE_TRACKER_RADIUS = 5f
    /**
     * The dp which the tracker of the hue or alpha panel
     * will extend outside of its bounds.
     */
    private var RECTANGLE_TRACKER_OFFSET = 2f


    private var mDensity = 1f

    private var mListener: OnColorChangedListener? = null

    private var mSatValPaint: Paint? = null
    private var mSatValTrackerPaint: Paint? = null

    private var mHuePaint: Paint? = null
    private var mHueTrackerPaint: Paint? = null

    private var mAlphaPaint: Paint? = null
    private var mAlphaTextPaint: Paint? = null

    private var mBorderPaint: Paint? = null

    private var mValShader: Shader? = null
    private var mSatShader: Shader? = null
    private var mHueShader: Shader? = null
    private var mAlphaShader: Shader? = null

    private var mAlpha = 0xff
    private var mHue = 360f
    private var mSat = 0f
    private var mVal = 0f

    private var mAlphaSliderText: String? = ""
    var sliderTrackerColor = -0xe3e3e4
        set(color)
        {
            field = color

            mHueTrackerPaint!!.color = sliderTrackerColor

            invalidate()
        }
    /**
     * Get the color of the border surrounding all panels.
     */
    /**
     * Set the color of the border surrounding all panels.
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
     * Set if the user is allowed to adjust the alpha panel. Default is false.
     * If it is set to false no alpha will be set.
     *
     * @param visible
     */
    /*
             * Reset all shader to force a recreation.
             * Otherwise they will not look right after
             * the size of the view has changed.
             */ var alphaSliderVisible = false
        set(visible)
        {

            if (alphaSliderVisible != visible)
            {
                field = visible
                mValShader = null
                mSatShader = null
                mHueShader = null
                mAlphaShader = null

                requestLayout()
            }

        }

    /*
     * To remember which panel that has the "focus" when
     * processing hardware button data.
     */
    private var mLastTouchedPanel = PANEL_SAT_VAL

    /**
     * Offset from the edge we must have or else
     * the finger tracker will get clipped when
     * it is drawn outside of the view.
     */
    /**
     * Get the drawing offset of the color picker view.
     * The drawing offset is the distance from the side of
     * a panel to the side of the view minus the padding.
     * Useful if you want to have your own panel below showing
     * the currently selected color and want to align it perfectly.
     *
     * @return The offset in pixels.
     */
    var drawingOffset: Float = 0.toFloat()
        private set


    /*
     * Distance form the edges of the view
     * of where we are allowed to draw.
     */
    private var mDrawingRect: RectF? = null

    private var mSatValRect: RectF? = null
    private var mHueRect: RectF? = null
    private var mAlphaRect: RectF? = null

    private var mAlphaPattern: AlphaPatternDrawable? = null

    private var mStartTouchPoint: Point? = null

    private val prefferedWidth: Int
        get()
        {

            var width = prefferedHeight

            if (alphaSliderVisible)
            {
                width -= (PANEL_SPACING + ALPHA_PANEL_HEIGHT).toInt()
            }


            return (width.toFloat() + HUE_PANEL_WIDTH + PANEL_SPACING).toInt()

        }

    private val prefferedHeight: Int
        get()
        {

            var height = (200 * mDensity).toInt()

            if (alphaSliderVisible)
            {
                height += (PANEL_SPACING + ALPHA_PANEL_HEIGHT).toInt()
            }

            return height
        }

    /**
     * Get the current color this view is showing.
     *
     * @return the current color.
     */
    /**
     * Set the color the view should show.
     *
     * @param color The color that should be selected.
     */
    var color: Int
        get() = Color.HSVToColor(mAlpha, floatArrayOf(mHue, mSat, mVal))
        set(color) = setColor(color, false)

    interface OnColorChangedListener
    {
        fun onColorChanged(color: Int)
    }

    init
    {
        init()
    }

    private fun init()
    {
        mDensity = context.resources.displayMetrics.density
        PALETTE_CIRCLE_TRACKER_RADIUS *= mDensity
        RECTANGLE_TRACKER_OFFSET *= mDensity
        HUE_PANEL_WIDTH *= mDensity
        ALPHA_PANEL_HEIGHT *= mDensity
        PANEL_SPACING = PANEL_SPACING * mDensity

        drawingOffset = calculateRequiredOffset()

        initPaintTools()

        //Needed for receiving trackball motion events.
        isFocusable = true
        isFocusableInTouchMode = true
    }

    private fun initPaintTools()
    {

        mSatValPaint = Paint()
        mSatValTrackerPaint = Paint()
        mHuePaint = Paint()
        mHueTrackerPaint = Paint()
        mAlphaPaint = Paint()
        mAlphaTextPaint = Paint()
        mBorderPaint = Paint()


        mSatValTrackerPaint!!.style = Paint.Style.STROKE
        mSatValTrackerPaint!!.strokeWidth = 2f * mDensity
        mSatValTrackerPaint!!.isAntiAlias = true

        mHueTrackerPaint!!.color = sliderTrackerColor
        mHueTrackerPaint!!.style = Paint.Style.STROKE
        mHueTrackerPaint!!.strokeWidth = 2f * mDensity
        mHueTrackerPaint!!.isAntiAlias = true

        mAlphaTextPaint!!.color = -0xe3e3e4
        mAlphaTextPaint!!.textSize = 14f * mDensity
        mAlphaTextPaint!!.isAntiAlias = true
        mAlphaTextPaint!!.textAlign = Paint.Align.CENTER
        mAlphaTextPaint!!.isFakeBoldText = true


    }

    private fun calculateRequiredOffset(): Float
    {
        var offset = Math.max(PALETTE_CIRCLE_TRACKER_RADIUS, RECTANGLE_TRACKER_OFFSET)
        offset = Math.max(offset, BORDER_WIDTH_PX * mDensity)

        return offset * 1.5f
    }

    private fun buildHueColorArray(): IntArray
    {

        val hue = IntArray(361)

        var count = 0
        var i = hue.size - 1
        while (i >= 0)
        {
            hue[count] = Color.HSVToColor(floatArrayOf(i.toFloat(), 1f, 1f))
            i--
            count++
        }

        return hue
    }


    override fun onDraw(canvas: Canvas)
    {

        if (mDrawingRect!!.width() <= 0 || mDrawingRect!!.height() <= 0) return

        drawSatValPanel(canvas)
        drawHuePanel(canvas)
        drawAlphaPanel(canvas)

    }

    private fun drawSatValPanel(canvas: Canvas)
    {

        val rect = mSatValRect

        if (BORDER_WIDTH_PX > 0)
        {
            mBorderPaint!!.color = borderColor
            canvas.drawRect(mDrawingRect!!.left, mDrawingRect!!.top, rect!!.right + BORDER_WIDTH_PX, rect.bottom + BORDER_WIDTH_PX, mBorderPaint!!)
        }

        if (mValShader == null)
        {
            mValShader = LinearGradient(rect!!.left, rect.top, rect.left, rect.bottom, -0x1, -0x1000000, Shader.TileMode.CLAMP)
        }

        val rgb = Color.HSVToColor(floatArrayOf(mHue, 1f, 1f))

        mSatShader = LinearGradient(rect!!.left, rect.top, rect.right, rect.top, -0x1, rgb, Shader.TileMode.CLAMP)
        val mShader = ComposeShader(mValShader!!, mSatShader!!, PorterDuff.Mode.MULTIPLY)
        mSatValPaint!!.shader = mShader

        canvas.drawRect(rect, mSatValPaint!!)

        val p = satValToPoint(mSat, mVal)

        mSatValTrackerPaint!!.color = -0x1000000
        canvas.drawCircle(p.x.toFloat(), p.y.toFloat(), PALETTE_CIRCLE_TRACKER_RADIUS - 1f * mDensity, mSatValTrackerPaint!!)

        mSatValTrackerPaint!!.color = -0x222223
        canvas.drawCircle(p.x.toFloat(), p.y.toFloat(), PALETTE_CIRCLE_TRACKER_RADIUS, mSatValTrackerPaint!!)

    }

    private fun drawHuePanel(canvas: Canvas)
    {

        val rect = mHueRect

        if (BORDER_WIDTH_PX > 0)
        {
            mBorderPaint!!.color = borderColor
            canvas.drawRect(rect!!.left - BORDER_WIDTH_PX, rect.top - BORDER_WIDTH_PX, rect.right + BORDER_WIDTH_PX, rect.bottom + BORDER_WIDTH_PX, mBorderPaint!!)
        }

        if (mHueShader == null)
        {
            mHueShader = LinearGradient(rect!!.left, rect.top, rect.left, rect.bottom, buildHueColorArray(), null, Shader.TileMode.CLAMP)
            mHuePaint!!.shader = mHueShader
        }

        canvas.drawRect(rect!!, mHuePaint!!)

        val rectHeight = 4 * mDensity / 2

        val p = hueToPoint(mHue)

        val r = RectF()
        r.left = rect.left - RECTANGLE_TRACKER_OFFSET
        r.right = rect.right + RECTANGLE_TRACKER_OFFSET
        r.top = p.y - rectHeight
        r.bottom = p.y + rectHeight


        canvas.drawRoundRect(r, 2f, 2f, mHueTrackerPaint!!)

    }

    private fun drawAlphaPanel(canvas: Canvas)
    {

        if (!alphaSliderVisible || mAlphaRect == null || mAlphaPattern == null) return

        val rect = mAlphaRect

        if (BORDER_WIDTH_PX > 0)
        {
            mBorderPaint!!.color = borderColor
            canvas.drawRect(rect!!.left - BORDER_WIDTH_PX, rect.top - BORDER_WIDTH_PX, rect.right + BORDER_WIDTH_PX, rect.bottom + BORDER_WIDTH_PX, mBorderPaint!!)
        }


        mAlphaPattern!!.draw(canvas)

        val hsv = floatArrayOf(mHue, mSat, mVal)
        val color = Color.HSVToColor(hsv)
        val acolor = Color.HSVToColor(0, hsv)

        mAlphaShader = LinearGradient(rect!!.left, rect.top, rect.right, rect.top, color, acolor, Shader.TileMode.CLAMP)


        mAlphaPaint!!.shader = mAlphaShader

        canvas.drawRect(rect, mAlphaPaint!!)

        if (mAlphaSliderText != null && mAlphaSliderText !== "")
        {
            canvas.drawText(mAlphaSliderText!!, rect.centerX(), rect.centerY() + 4 * mDensity, mAlphaTextPaint!!)
        }

        val rectWidth = 4 * mDensity / 2

        val p = alphaToPoint(mAlpha)

        val r = RectF()
        r.left = p.x - rectWidth
        r.right = p.x + rectWidth
        r.top = rect.top - RECTANGLE_TRACKER_OFFSET
        r.bottom = rect.bottom + RECTANGLE_TRACKER_OFFSET

        canvas.drawRoundRect(r, 2f, 2f, mHueTrackerPaint!!)

    }


    private fun hueToPoint(hue: Float): Point
    {

        val rect = mHueRect
        val height = rect!!.height()

        val p = Point()

        p.y = (height - hue * height / 360f + rect.top).toInt()
        p.x = rect.left.toInt()

        return p
    }

    private fun satValToPoint(sat: Float, `val`: Float): Point
    {

        val rect = mSatValRect
        val height = rect!!.height()
        val width = rect.width()

        val p = Point()

        p.x = (sat * width + rect.left).toInt()
        p.y = ((1f - `val`) * height + rect.top).toInt()

        return p
    }

    private fun alphaToPoint(alpha: Int): Point
    {

        val rect = mAlphaRect
        val width = rect!!.width()

        val p = Point()

        p.x = (width - alpha * width / 0xff + rect.left).toInt()
        p.y = rect.top.toInt()

        return p

    }

    private fun pointToSatVal(x: Float, y: Float): FloatArray
    {
        var x = x
        var y = y

        val rect = mSatValRect
        val result = FloatArray(2)

        val width = rect!!.width()
        val height = rect.height()

        if (x < rect.left)
        {
            x = 0f
        } else if (x > rect.right)
        {
            x = width
        } else
        {
            x = x - rect.left
        }

        if (y < rect.top)
        {
            y = 0f
        } else if (y > rect.bottom)
        {
            y = height
        } else
        {
            y = y - rect.top
        }


        result[0] = 1f / width * x
        result[1] = 1f - 1f / height * y

        return result
    }

    private fun pointToHue(y: Float): Float
    {
        var y = y

        val rect = mHueRect

        val height = rect!!.height()

        if (y < rect.top)
        {
            y = 0f
        } else if (y > rect.bottom)
        {
            y = height
        } else
        {
            y = y - rect.top
        }

        return 360f - y * 360f / height
    }

    private fun pointToAlpha(x: Int): Int
    {
        var x = x

        val rect = mAlphaRect
        val width = rect!!.width().toInt()

        if (x < rect.left)
        {
            x = 0
        } else if (x > rect.right)
        {
            x = width
        } else
        {
            x = x - rect.left.toInt()
        }

        return 0xff - x * 0xff / width

    }


    override fun onTrackballEvent(event: MotionEvent): Boolean
    {

        val x = event.x
        val y = event.y

        var update = false


        if (event.action == MotionEvent.ACTION_MOVE)
        {

            when (mLastTouchedPanel)
            {

                PANEL_SAT_VAL ->
                {

                    var sat: Float
                    var `val`: Float

                    sat = mSat + x / 50f
                    `val` = mVal - y / 50f

                    if (sat < 0f)
                    {
                        sat = 0f
                    } else if (sat > 1f)
                    {
                        sat = 1f
                    }

                    if (`val` < 0f)
                    {
                        `val` = 0f
                    } else if (`val` > 1f)
                    {
                        `val` = 1f
                    }

                    mSat = sat
                    mVal = `val`

                    update = true
                }

                PANEL_HUE ->
                {

                    var hue = mHue - y * 10f

                    if (hue < 0f)
                    {
                        hue = 0f
                    } else if (hue > 360f)
                    {
                        hue = 360f
                    }

                    mHue = hue

                    update = true
                }

                PANEL_ALPHA ->

                    if (!alphaSliderVisible || mAlphaRect == null)
                    {
                        update = false
                    } else
                    {

                        var alpha = (mAlpha - x * 10).toInt()

                        if (alpha < 0)
                        {
                            alpha = 0
                        } else if (alpha > 0xff)
                        {
                            alpha = 0xff
                        }

                        mAlpha = alpha


                        update = true
                    }
            }


        }


        if (update)
        {

            if (mListener != null)
            {
                mListener!!.onColorChanged(Color.HSVToColor(mAlpha, floatArrayOf(mHue, mSat, mVal)))
            }

            invalidate()
            return true
        }


        return super.onTrackballEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean
    {

        var update = false

        when (event.action)
        {

            MotionEvent.ACTION_DOWN ->
            {

                mStartTouchPoint = Point(event.x.toInt(), event.y.toInt())

                update = moveTrackersIfNeeded(event)
            }

            MotionEvent.ACTION_MOVE ->

                update = moveTrackersIfNeeded(event)

            MotionEvent.ACTION_UP ->
            {

                mStartTouchPoint = null

                update = moveTrackersIfNeeded(event)
            }
        }

        if (update)
        {

            if (mListener != null)
            {
                mListener!!.onColorChanged(Color.HSVToColor(mAlpha, floatArrayOf(mHue, mSat, mVal)))
            }

            invalidate()
            return true
        }


        return super.onTouchEvent(event)
    }

    private fun moveTrackersIfNeeded(event: MotionEvent): Boolean
    {

        if (mStartTouchPoint == null) return false

        var update = false

        val startX = mStartTouchPoint!!.x
        val startY = mStartTouchPoint!!.y


        if (mHueRect!!.contains(startX.toFloat(), startY.toFloat()))
        {
            mLastTouchedPanel = PANEL_HUE

            mHue = pointToHue(event.y)

            update = true
        } else if (mSatValRect!!.contains(startX.toFloat(), startY.toFloat()))
        {

            mLastTouchedPanel = PANEL_SAT_VAL

            val result = pointToSatVal(event.x, event.y)

            mSat = result[0]
            mVal = result[1]

            update = true
        } else if (mAlphaRect != null && mAlphaRect!!.contains(startX.toFloat(), startY.toFloat()))
        {

            mLastTouchedPanel = PANEL_ALPHA

            mAlpha = pointToAlpha(event.x.toInt())

            update = true
        }


        return update
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {

        var width = 0
        var height = 0

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        var widthAllowed = View.MeasureSpec.getSize(widthMeasureSpec)
        var heightAllowed = View.MeasureSpec.getSize(heightMeasureSpec)

        widthAllowed = chooseWidth(widthMode, widthAllowed)
        heightAllowed = chooseHeight(heightMode, heightAllowed)

        if (!alphaSliderVisible)
        {

            height = (widthAllowed.toFloat() - PANEL_SPACING - HUE_PANEL_WIDTH).toInt()

            //If calculated height (based on the width) is more than the allowed height.
            if (height > heightAllowed || tag == "landscape")
            {
                height = heightAllowed
                width = (height.toFloat() + PANEL_SPACING + HUE_PANEL_WIDTH).toInt()
            } else
            {
                width = widthAllowed
            }
        } else
        {

            width = (heightAllowed - ALPHA_PANEL_HEIGHT + HUE_PANEL_WIDTH).toInt()

            if (width > widthAllowed)
            {
                width = widthAllowed
                height = (widthAllowed - HUE_PANEL_WIDTH + ALPHA_PANEL_HEIGHT).toInt()
            } else
            {
                height = heightAllowed
            }

        }

        setMeasuredDimension(width, height)
    }

    private fun chooseWidth(mode: Int, size: Int): Int
    {
        return if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY)
        {
            size
        } else
        { // (mode == MeasureSpec.UNSPECIFIED)
            prefferedWidth
        }
    }

    private fun chooseHeight(mode: Int, size: Int): Int
    {
        return if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY)
        {
            size
        } else
        { // (mode == MeasureSpec.UNSPECIFIED)
            prefferedHeight
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
    {
        super.onSizeChanged(w, h, oldw, oldh)

        mDrawingRect = RectF()
        mDrawingRect!!.left = drawingOffset + paddingLeft
        mDrawingRect!!.right = w.toFloat() - drawingOffset - paddingRight.toFloat()
        mDrawingRect!!.top = drawingOffset + paddingTop
        mDrawingRect!!.bottom = h.toFloat() - drawingOffset - paddingBottom.toFloat()

        setUpSatValRect()
        setUpHueRect()
        setUpAlphaRect()
    }

    private fun setUpSatValRect()
    {

        val dRect = mDrawingRect
        var panelSide = dRect!!.height() - BORDER_WIDTH_PX * 2

        if (alphaSliderVisible)
        {
            panelSide -= PANEL_SPACING + ALPHA_PANEL_HEIGHT
        }

        val left = dRect.left + BORDER_WIDTH_PX
        val top = dRect.top + BORDER_WIDTH_PX
        val bottom = top + panelSide
        val right = left + panelSide

        mSatValRect = RectF(left, top, right, bottom)
    }

    private fun setUpHueRect()
    {
        val dRect = mDrawingRect

        val left = dRect!!.right - HUE_PANEL_WIDTH + BORDER_WIDTH_PX
        val top = dRect.top + BORDER_WIDTH_PX
        val bottom = dRect.bottom - BORDER_WIDTH_PX - if (alphaSliderVisible) PANEL_SPACING + ALPHA_PANEL_HEIGHT else 0f
        val right = dRect.right - BORDER_WIDTH_PX

        mHueRect = RectF(left, top, right, bottom)
    }

    private fun setUpAlphaRect()
    {

        if (!alphaSliderVisible) return

        val dRect = mDrawingRect

        val left = dRect!!.left + BORDER_WIDTH_PX
        val top = dRect.bottom - ALPHA_PANEL_HEIGHT + BORDER_WIDTH_PX
        val bottom = dRect.bottom - BORDER_WIDTH_PX
        val right = dRect.right - BORDER_WIDTH_PX

        mAlphaRect = RectF(left, top, right, bottom)

        mAlphaPattern = AlphaPatternDrawable((5 * mDensity).toInt())
        mAlphaPattern!!.setBounds(Math.round(mAlphaRect!!.left), Math.round(mAlphaRect!!.top), Math.round(mAlphaRect!!.right), Math.round(mAlphaRect!!.bottom))

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

    /**
     * Set the color this view should show.
     *
     * @param color    The color that should be selected.
     * @param callback If you want to get a callback to
     * your OnColorChangedListener.
     */
    fun setColor(color: Int, callback: Boolean)
    {

        val alpha = Color.alpha(color)

        val hsv = FloatArray(3)

        Color.colorToHSV(color, hsv)

        mAlpha = alpha
        mHue = hsv[0]
        mSat = hsv[1]
        mVal = hsv[2]

        if (callback && mListener != null)
        {
            mListener!!.onColorChanged(Color.HSVToColor(mAlpha, floatArrayOf(mHue, mSat, mVal)))
        }

        invalidate()
    }

    /**
     * Set the text that should be shown in the
     * alpha slider. Set to null to disable text.
     *
     * @param res string resource id.
     */
    fun setAlphaSliderText(res: Int)
    {
        val text = context.getString(res)
        setAlphaSliderText(text)
    }

    /**
     * Set the text that should be shown in the
     * alpha slider. Set to null to disable text.
     *
     * @param text Text that should be shown.
     */
    fun setAlphaSliderText(text: String)
    {
        mAlphaSliderText = text
        invalidate()
    }

    /**
     * Get the current value of the text
     * that will be shown in the alpha
     * slider.
     *
     * @return
     */
    fun getAlphaSliderText(): String?
    {
        return mAlphaSliderText
    }

    companion object
    {
        private val PANEL_SAT_VAL = 0
        private val PANEL_HUE = 1
        private val PANEL_ALPHA = 2

        /**
         * The width in pixels of the border
         * surrounding all color panels.
         */
        private val BORDER_WIDTH_PX = 1f
    }
}
