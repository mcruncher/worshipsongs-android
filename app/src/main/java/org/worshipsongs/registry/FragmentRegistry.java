package org.worshipsongs.registry;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.worshipsongs.CommonConstants;
import org.worshipsongs.WorshipSongApplication;
import org.worshipsongs.domain.DragDrop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dalvik.system.DexFile;

/**
 * Author : Madasamy
 * Version : 4.x.x
 */

public class FragmentRegistry
{

    private static final String CLASS_NAME = FragmentRegistry.class.getSimpleName();
    private static final String PACKAGE_NAME = "org.worshipsongs.fragment";
    private static final String ABSTRACT = "Abstract";

    public List<String> getTitles(Activity activity)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        List<DragDrop> configuredDragDrops = DragDrop.toArrays(sharedPreferences.getString(CommonConstants.TAB_CHOICE_KEY, ""));
        List<String> titles = new ArrayList<>();
        ArrayList<DragDrop> defaultDragDrops = getDragDrops(activity);
        if (configuredDragDrops == null || configuredDragDrops.isEmpty()) {
            configuredDragDrops = defaultDragDrops;
        } else if (defaultDragDrops.size() > configuredDragDrops.size()) {
            defaultDragDrops.removeAll(configuredDragDrops);
            configuredDragDrops.addAll(defaultDragDrops);
        }
        for (DragDrop configuredDragDrop : configuredDragDrops) {
            if (configuredDragDrop.isChecked()) {
                int identifier = WorshipSongApplication.getContext().getResources().getIdentifier(configuredDragDrop.getTitle(),
                        "string", WorshipSongApplication.getContext().getPackageName());
                titles.add(activity.getString(identifier));
            }
        }
        return titles;
    }

    public ArrayList<DragDrop> getDragDrops(Activity activity)
    {
        ArrayList<DragDrop> dragDrops = new ArrayList<>();
        for (ITabFragment tabFragment : findAll(activity)) {
            dragDrops.add(new DragDrop(tabFragment.defaultSortOrder(), tabFragment.getTitle(), tabFragment.checked()));
        }
        return dragDrops;
    }

    public ITabFragment findByTitle(Activity activity, String title)
    {
        List<ITabFragment> fragments = findAll(activity);
        for (ITabFragment fragment : fragments) {
            if (title.equalsIgnoreCase(fragment.getTitle())) {
                return fragment;
            }
        }
        return null;
    }

    public List<ITabFragment> findAll(Activity activity)
    {
        List<ITabFragment> sectionListeners = new ArrayList<>(findAllClasses(activity));
        Collections.sort(sectionListeners, new TabFragmentComparator());
        return sectionListeners;
    }

    private Set<ITabFragment> findAllClasses(Activity activity)
    {
        Set<ITabFragment> sectionListeners = new HashSet<>();
        try {
            DexFile dex = new DexFile(activity.getBaseContext().getPackageCodePath());
            Thread.currentThread().setContextClassLoader(activity.getBaseContext().getClassLoader());
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<String> entries = dex.entries();
            while (entries.hasMoreElements()) {
                String entry = entries.nextElement();
                if (entry.toLowerCase().startsWith(PACKAGE_NAME.toLowerCase())) {
                    Class clazz = classLoader.loadClass(entry);
                    if (ITabFragment.class.isAssignableFrom(clazz) &&
                            !clazz.getName().equalsIgnoreCase(ITabFragment.class.getName()) &&
                            !clazz.getSimpleName().startsWith(ABSTRACT)) {
                        ITabFragment sectionListener = getTabFragment(clazz);
                        if (sectionListener != null) {
                            sectionListeners.add(sectionListener);
                        }
                    }
                }
            }
            Log.i(CLASS_NAME, "No. of fragments " + sectionListeners.size());
        } catch (Exception ex) {
            Log.e(CLASS_NAME, "Error occurred while finding classes" + ex);
        }
        return sectionListeners;
    }

    ITabFragment getTabFragment(Class clazz)
    {
        try {
            return (ITabFragment) clazz.getConstructor().newInstance();
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), "Error occurred while creating tab fragment instance", ex);
            return null;
        }
    }

    class TabFragmentComparator implements Comparator<ITabFragment>
    {

        @Override
        public int compare(ITabFragment tabFragment, ITabFragment tabFragment2)
        {
            int comparedValue = tabFragment.defaultSortOrder() - tabFragment2.defaultSortOrder();
            if (comparedValue == 0) {
                return tabFragment2.getTitle().compareTo(tabFragment2.getTitle());
            }
            return comparedValue;
        }
    }

}
