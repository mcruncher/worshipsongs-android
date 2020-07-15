package org.worshipsongs.registry

import android.app.Activity
import android.preference.PreferenceManager
import android.util.Log

import org.worshipsongs.CommonConstants
import org.worshipsongs.domain.DragDrop

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.HashSet

import dalvik.system.DexFile

/**
 * @author: Madasamy
 * @version: 3.x.x
 */

class FragmentRegistry
{

    fun getTitles(activity: Activity): List<String>
    {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        var configuredDragDrops: MutableList<DragDrop>? = DragDrop.toArrays(sharedPreferences.getString(CommonConstants.TAB_CHOICE_KEY, "")!!)
        val titles = ArrayList<String>()
        val defaultDragDrops = getDragDrops(activity)
        if (configuredDragDrops == null || configuredDragDrops.isEmpty())
        {
            configuredDragDrops = defaultDragDrops
        } else if (defaultDragDrops.size > configuredDragDrops.size)
        {
            defaultDragDrops.removeAll(configuredDragDrops)
            configuredDragDrops.addAll(defaultDragDrops)
        }
        for (configuredDragDrop in configuredDragDrops)
        {
            if (configuredDragDrop.isChecked)
            {
                titles.add(configuredDragDrop.title!!)
            }
        }
        setDefaultTitles(titles, defaultDragDrops)
        return titles
    }

    private fun setDefaultTitles(titles: ArrayList<String>, defaultDragDrops: ArrayList<DragDrop>)
    {
        if (titles.isEmpty())
        {
            defaultDragDrops.forEach { it.title?.let { existingTitle ->
                titles.add(existingTitle) } }
        }
    }

    fun getDragDrops(activity: Activity): ArrayList<DragDrop>
    {
        val dragDrops = ArrayList<DragDrop>()
        for (tabFragment in findAll(activity))
        {
            dragDrops.add(DragDrop(tabFragment.defaultSortOrder().toLong(), tabFragment.title, tabFragment.checked()))
        }
        return dragDrops
    }

    fun findByTitle(activity: Activity, title: String): ITabFragment?
    {
        val fragments = findAll(activity)
        Log.i(this.javaClass.simpleName, "Fragement $fragments")
        for (fragment in fragments)
        {
            if (title.equals(fragment.title, ignoreCase = true))
            {
                return fragment
            }
        }
        return null
    }

    fun findAll(activity: Activity): List<ITabFragment>
    {
        val sectionListeners = ArrayList(findAllClasses(activity))
        Collections.sort(sectionListeners, TabFragmentComparator())
        return sectionListeners
    }

    private fun findAllClasses(activity: Activity): Set<ITabFragment>
    {
        val sectionListeners = HashSet<ITabFragment>()
        try
        {
            val dex = DexFile(activity.baseContext.packageCodePath)
            Thread.currentThread().contextClassLoader = activity.baseContext.classLoader
            val classLoader = Thread.currentThread().contextClassLoader
            val entries = dex.entries()
            while (entries.hasMoreElements())
            {
                val entry = entries.nextElement()
                if (entry.toLowerCase().startsWith(PACKAGE_NAME.toLowerCase()))
                {
                    val clazz = classLoader.loadClass(entry)
                    if (ITabFragment::class.java.isAssignableFrom(clazz) && !clazz.name.equals(ITabFragment::class.java.name, ignoreCase = true) && !clazz.simpleName.startsWith(ABSTRACT))
                    {
                        val sectionListener = getTabFragment(clazz)
                        if (sectionListener != null)
                        {
                            sectionListeners.add(sectionListener)
                        }
                    }
                }
            }
            Log.i(CLASS_NAME, "No. of fragments " + sectionListeners.size)
        } catch (ex: Exception)
        {
            Log.e(CLASS_NAME, "Error occurred while finding classes$ex")
        }

        return sectionListeners
    }

    internal fun getTabFragment(clazz: Class<*>): ITabFragment?
    {
        try
        {
            return clazz.getConstructor().newInstance() as ITabFragment
        } catch (ex: Exception)
        {
            Log.e(this.javaClass.simpleName, "Error occurred while creating tab fragment instance", ex)
            return null
        }

    }

    internal inner class TabFragmentComparator : Comparator<ITabFragment>
    {

        override fun compare(tabFragment: ITabFragment, tabFragment2: ITabFragment): Int
        {
            val comparedValue = tabFragment.defaultSortOrder() - tabFragment2.defaultSortOrder()
            return if (comparedValue == 0)
            {
                tabFragment2.title.compareTo(tabFragment2.title)
            } else comparedValue
        }
    }

    companion object
    {

        private val CLASS_NAME = FragmentRegistry::class.java.simpleName
        private val PACKAGE_NAME = "org.worshipsongs.fragment"
        private val ABSTRACT = "Abstract"
    }

}
