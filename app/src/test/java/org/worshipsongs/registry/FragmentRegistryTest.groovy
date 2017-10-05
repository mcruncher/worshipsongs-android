package org.worshipsongs.registry

import android.support.v4.app.FragmentActivity
import hkhc.electricspock.ElectricSpecification
import org.robolectric.Robolectric

/**
 *  Author : Madasamy
 *  Version : 4.x.x
 */
class FragmentRegistryTest extends ElectricSpecification
{
    def fragmentRegistry = new FragmentRegistry();
    def activity = Robolectric.buildActivity(FragmentActivity.class).create().start().resume().get()

    def "Get default titles"()
    {

        expect:
        fragmentRegistry.getTitles(activity).size() == 0
    }

    def "Get tab Fragment"()
    {
        expect:
        fragmentRegistry.getTabFragment(DummyFragment1.class).defaultSortOrder() == 1
    }

    def "Sort fragment"()
    {
        given:
        List<ITabFragment> fragmentList = new ArrayList<>();
        fragmentList.add(new DummyFragment2())
        fragmentList.add(new DummyFragment1())

        when:
        Collections.sort(fragmentList,  new FragmentRegistry.TabFragmentComparator(fragmentRegistry))

        then:
        fragmentList[0].class.getSimpleName() == "DummyFragment1"
    }
}
