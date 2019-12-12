package org.worshipsongs.preference

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable


/**
 * @author: Madasamy
 * @version: 3.3.x
 */
class AlphaPatternDrawable(rectangleSize: Int) : Drawable()
{
    private var mRectangleSize = 10

    private val mPaint = Paint()
    private val mPaintWhite = Paint()
    private val mPaintGray = Paint()

    private var numRectanglesHorizontal: Int = 0
    private var numRectanglesVertical: Int = 0

    /**
     * Bitmap in which the pattern will be cahched.
     */
    private var mBitmap: Bitmap? = null

    init
    {
        mRectangleSize = rectangleSize
        mPaintWhite.color = -0x1
        mPaintGray.color = -0x343435
    }

    override fun draw(canvas: Canvas)
    {
        canvas.drawBitmap(mBitmap!!, null, bounds, mPaint)
    }

    override fun getOpacity(): Int
    {
        return 0
    }

    override fun setAlpha(alpha: Int)
    {
        throw UnsupportedOperationException("Alpha is not supported by this drawwable.")
    }

    override fun setColorFilter(cf: ColorFilter?)
    {
        throw UnsupportedOperationException("ColorFilter is not supported by this drawwable.")
    }

    override fun onBoundsChange(bounds: Rect)
    {
        super.onBoundsChange(bounds)

        val height = bounds.height()
        val width = bounds.width()

        numRectanglesHorizontal = Math.ceil((width / mRectangleSize).toDouble()).toInt()
        numRectanglesVertical = Math.ceil((height / mRectangleSize).toDouble()).toInt()

        generatePatternBitmap()

    }

    /**
     * This will generate a bitmap with the pattern
     * as big as the rectangle we were allow to draw on.
     * We do this to chache the bitmap so we don't need to
     * recreate it each time draw() is called since it
     * takes a few milliseconds.
     */
    private fun generatePatternBitmap()
    {

        if (bounds.width() <= 0 || bounds.height() <= 0)
        {
            return
        }

        mBitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mBitmap!!)

        val r = Rect()
        var verticalStartWhite = true
        for (i in 0..numRectanglesVertical)
        {

            var isWhite = verticalStartWhite
            for (j in 0..numRectanglesHorizontal)
            {

                r.top = i * mRectangleSize
                r.left = j * mRectangleSize
                r.bottom = r.top + mRectangleSize
                r.right = r.left + mRectangleSize

                canvas.drawRect(r, if (isWhite) mPaintWhite else mPaintGray)

                isWhite = !isWhite
            }

            verticalStartWhite = !verticalStartWhite

        }

    }
}
