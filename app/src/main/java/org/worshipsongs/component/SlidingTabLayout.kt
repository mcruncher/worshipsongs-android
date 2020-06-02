package org.worshipsongs.component

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.preference.PreferenceManager

import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager

import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.domain.Theme

/**
 * Created by madasamy on 8/16/15.
 */
class SlidingTabLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : HorizontalScrollView(context, attrs, defStyle)
{
    private val mTitleOffset: Int
    private var mTabViewLayoutId: Int = 0
    private var mTabViewTextViewId: Int = 0
    private var mDistributeEvenly: Boolean = false
    private var mViewPager: ViewPager? = null
    private val mContentDescriptions = SparseArray<String>()
    private var mViewPagerPageChangeListener: ViewPager.OnPageChangeListener? = null
    private val mTabStrip: SlidingTabStrip
    private val sharedPreferences: SharedPreferences

    private val tabColorStateList: ColorStateList
        get()
        {
            val states = arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf(android.R.attr.state_focused), intArrayOf(android.R.attr.state_pressed), intArrayOf())
            val themeName = sharedPreferences.getString(CommonConstants.THEME_KEY, Theme.DAY.name)
            val tabBackgroundColor = if (themeName!!.equals(Theme.DAY.name, ignoreCase = true)) R.color.shade_blue
            else R.color.list_item_background
            val colors = intArrayOf(resources.getColor(R.color.white), resources.getColor(R.color.white), resources.getColor(R.color.white), resources.getColor(tabBackgroundColor))
            return ColorStateList(states, colors)
        }

    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     * [.setCustomTabColorizer].
     */
    interface TabColorizer
    {
        /**
         * @return return the color of the indicator used when `position` is selected.
         */
        fun getIndicatorColor(position: Int): Int
    }

    init
    {
        // Disable the Scroll Bar
        isHorizontalScrollBarEnabled = false
        // Make sure that the Tab Strips fills this View
        isFillViewport = true
        mTitleOffset = (TITLE_OFFSET_DIPS * resources.displayMetrics.density).toInt()
        mTabStrip = SlidingTabStrip(context)
        addView(mTabStrip, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    /**
     * Set the custom [TabColorizer] to be used.
     *
     *
     * If you only require simple custmisation then you can use
     * [.setSelectedIndicatorColors] to achieve
     * similar effects.
     */
    fun setCustomTabColorizer(tabColorizer: TabColorizer)
    {
        mTabStrip.setCustomTabColorizer(tabColorizer)
    }

    fun setDistributeEvenly(distributeEvenly: Boolean)
    {
        mDistributeEvenly = distributeEvenly
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    fun setSelectedIndicatorColors(vararg colors: Int)
    {
        mTabStrip.setSelectedIndicatorColors(*colors)
    }

    /**
     * Set the [ViewPager.OnPageChangeListener]. When using [SlidingTabLayout] you are
     * required to set any [ViewPager.OnPageChangeListener] through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @see ViewPager.setOnPageChangeListener
     */
    fun setOnPageChangeListener(listener: ViewPager.OnPageChangeListener)
    {
        mViewPagerPageChangeListener = listener
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     *
     * @param layoutResId Layout id to be inflated
     * @param textViewId  id of the [TextView] in the inflated view
     */
    fun setCustomTabView(layoutResId: Int, textViewId: Int)
    {
        mTabViewLayoutId = layoutResId
        mTabViewTextViewId = textViewId
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    fun setViewPager(viewPager: ViewPager?)
    {
        mTabStrip.removeAllViews()
        mViewPager = viewPager
        if (viewPager != null)
        {
            viewPager.setOnPageChangeListener(InternalViewPagerListener())
            populateTabStrip()
        }
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * [.setCustomTabView].
     */
    protected fun createDefaultTabView(context: Context): TextView
    {
        val textView = TextView(context)
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP.toFloat())
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val outValue = TypedValue()
        getContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        textView.setBackgroundResource(outValue.resourceId)
        textView.isAllCaps = true
        val padding = (TAB_VIEW_PADDING_DIPS * resources.displayMetrics.density).toInt()
        textView.setPadding(padding, padding, padding, padding)
        //textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        return textView
    }

    private fun populateTabStrip()
    {
        val adapter = mViewPager!!.adapter
        val tabClickListener = TabClickListener()
        for (i in 0 until adapter!!.count)
        {
            var tabView: View? = null
            var tabTitleView: TextView? = null
            if (mTabViewLayoutId != 0)
            {
                // If there is a custom tab view layout id set, try and inflate it
                tabView = LayoutInflater.from(context).inflate(mTabViewLayoutId, mTabStrip, false)
                tabTitleView = tabView!!.findViewById<View>(mTabViewTextViewId) as TextView
            }
            if (tabView == null)
            {
                tabView = createDefaultTabView(context)
            }
            if (tabTitleView == null && TextView::class.java!!.isInstance(tabView))
            {
                tabTitleView = tabView as TextView?
            }
            if (mDistributeEvenly)
            {
                val lp = tabView.layoutParams as LinearLayout.LayoutParams
                lp.width = 0
                lp.weight = 1f
            }
            tabTitleView!!.text = adapter!!.getPageTitle(i)

            tabView.setOnClickListener(tabClickListener)
            val desc = mContentDescriptions.get(i, null)
            if (desc != null)
            {
                tabView.contentDescription = desc
            }
            mTabStrip.addView(tabView)
            if (i == mViewPager!!.currentItem)
            {
                tabView.isSelected = true
            }
            tabTitleView.setTextColor(tabColorStateList)
            tabTitleView.textSize = 14f
        }
    }

    fun setContentDescription(i: Int, desc: String)
    {
        mContentDescriptions.put(i, desc)
    }

    override fun onAttachedToWindow()
    {
        super.onAttachedToWindow()
        if (mViewPager != null)
        {
            scrollToTab(mViewPager!!.currentItem, 0)
        }
    }

    private fun scrollToTab(tabIndex: Int, positionOffset: Int)
    {
        val tabStripChildCount = mTabStrip.childCount
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount)
        {
            return
        }
        val selectedChild = mTabStrip.getChildAt(tabIndex)
        if (selectedChild != null)
        {
            var targetScrollX = selectedChild.left + positionOffset
            if (tabIndex > 0 || positionOffset > 0)
            {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset
            }
            scrollTo(targetScrollX, 0)
        }
    }

    private inner class InternalViewPagerListener : ViewPager.OnPageChangeListener
    {
        private var mScrollState: Int = 0

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
        {
            val tabStripChildCount = mTabStrip.childCount
            if (tabStripChildCount == 0 || position < 0 || position >= tabStripChildCount)
            {
                return
            }
            mTabStrip.onViewPagerPageChanged(position, positionOffset)
            val selectedTitle = mTabStrip.getChildAt(position)
            val extraOffset = if (selectedTitle != null) (positionOffset * selectedTitle.width).toInt()
            else 0
            scrollToTab(position, extraOffset)
            if (mViewPagerPageChangeListener != null)
            {
                mViewPagerPageChangeListener!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        }

        override fun onPageScrollStateChanged(state: Int)
        {
            mScrollState = state
            if (mViewPagerPageChangeListener != null)
            {
                mViewPagerPageChangeListener!!.onPageScrollStateChanged(state)
            }
        }

        override fun onPageSelected(position: Int)
        {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE)
            {
                mTabStrip.onViewPagerPageChanged(position, 0f)
                scrollToTab(position, 0)
            }
            for (i in 0 until mTabStrip.childCount)
            {
                mTabStrip.getChildAt(i).isSelected = position == i
            }
            if (mViewPagerPageChangeListener != null)
            {
                mViewPagerPageChangeListener!!.onPageSelected(position)
            }
        }
    }

    private inner class TabClickListener : View.OnClickListener
    {
        override fun onClick(v: View)
        {
            for (i in 0 until mTabStrip.childCount)
            {
                if (v === mTabStrip.getChildAt(i))
                {
                    mViewPager!!.currentItem = i
                    return
                }
            }
        }
    }

    companion object
    {

        private val TITLE_OFFSET_DIPS = 24
        private val TAB_VIEW_PADDING_DIPS = 16
        private val TAB_VIEW_TEXT_SIZE_SP = 12
    }
}
