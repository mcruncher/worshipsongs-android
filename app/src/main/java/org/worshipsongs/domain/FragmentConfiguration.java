package org.worshipsongs.domain;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.worshipsongs.registry.TabFragment;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Author : Madasamy
 * Version : 4.x.x
 */

public class FragmentConfiguration implements Serializable
{
    private Class<? extends Fragment> fragmentClass;
    private int sortOrder;
    @StringRes
    private int title;
    private boolean checked;
    private Fragment fragment;

    public FragmentConfiguration()
    {

    }

    public FragmentConfiguration(Class<? extends Fragment> fragmentClass)
    {
        this.fragmentClass = fragmentClass;
        TabFragment tabFragment = (TabFragment) fragmentClass.getAnnotation(TabFragment.class);
        this.sortOrder = tabFragment.sortOrder();
        this.title = tabFragment.title();
        this.checked = tabFragment.checked();
        init();
    }

    public FragmentConfiguration(Class<? extends Fragment> fragmentClass, int sortOrder, @StringRes int title, boolean checked)
    {
        this.fragmentClass = fragmentClass;
        this.sortOrder = sortOrder;
        this.title = title;
        this.checked = checked;
        init();
    }

    private void init()
    {
        try {
            setFragment(fragmentClass.getConstructor().newInstance());
        } catch (Exception ex) {
        }
    }

    public Class<? extends Fragment> getFragmentClass()
    {
        return fragmentClass;
    }

    public void setFragmentClass(Class<? extends Fragment> fragmentClass)
    {
        this.fragmentClass = fragmentClass;
    }

    public int getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public int getTitle()
    {
        return title;
    }

    public void setTitle(int title)
    {
        this.title = title;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    public Fragment getFragment()
    {
        return fragment;
    }

    public void setFragment(Fragment fragment)
    {
        this.fragment = fragment;
    }

    @Override
    public String toString()
    {
        return "FragmentConfiguration{" +
                "fragmentClass=" + fragmentClass +
                ", title=" + title +
                ", checked=" + checked +
                ", fragment=" + fragment +
                '}';
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof FragmentConfiguration) {
            FragmentConfiguration otherObject = (FragmentConfiguration) object;
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(otherObject.getTitle(), getTitle());
            return equalsBuilder.isEquals();
        }
        return false;

    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getTitle());
        return hashCodeBuilder.hashCode();
    }


}
