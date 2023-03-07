package org.worshipsongs.registry

//import android.support.v4.app.FragmentActivity

import androidx.fragment.app.FragmentActivity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.util.*

/**
 *  Author : Madasamy
 *  Version : 3.x.x
 */

@RunWith(RobolectricTestRunner::class)
class FragmentRegistryTest {
    val fragmentRegistry = FragmentRegistry()
    val activity =
        Robolectric.buildActivity(FragmentActivity::class.java).create().start().resume().get()

    @Test
    fun `Get default titles`() {
        // expect:
        assertEquals(0, fragmentRegistry.getTitles(activity).size)
    }

    @Test
    fun `Get tab Fragment`() {
        // expect:
        assertEquals(
            1,
            fragmentRegistry.getTabFragment(DummyFragment1::class.java)!!.defaultSortOrder()
        )
    }

    @Test
    fun `Sort fragment`() {
        // given:
        val fragmentList = arrayListOf(DummyFragment2(), DummyFragment1())

        // when:
        Collections.sort(fragmentList, fragmentRegistry.TabFragmentComparator())

        // then:
        assertEquals("DummyFragment1", fragmentList[0]::class.simpleName)
    }
}
