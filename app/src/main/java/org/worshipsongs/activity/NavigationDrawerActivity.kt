package org.worshipsongs.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log

import androidx.core.view.GravityCompat


import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import org.worshipsongs.CommonConstants
import org.worshipsongs.R
import org.worshipsongs.fragment.HomeFragment
import org.worshipsongs.service.PresentationScreenService
import org.worshipsongs.service.SongService
import org.worshipsongs.utils.CommonUtils
import org.worshipsongs.utils.ThemeUtils


class NavigationDrawerActivity : AbstractAppCompactActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var sharedPreferences: SharedPreferences? = null
    private var presentationScreenService: PresentationScreenService? = null

    val colorStateList: ColorStateList
        get() {
            val typedValue = TypedValue()
            theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
            val state = arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_enabled), intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_pressed))
            val color = intArrayOf(typedValue.data, typedValue.data, typedValue.data, typedValue.data)
            return ColorStateList(state, color)
        }

    val flags: Int
        get() = Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtils.setNoActionBarTheme(this)
        setContentView(R.layout.activity_main)
        presentationScreenService = PresentationScreenService(this)
        setSongCount()
        setDrawerLayout()
        setNavigationView(savedInstanceState)
    }

    private fun setSongCount() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (!sharedPreferences!!.all.containsKey(CommonConstants.NO_OF_SONGS)) {
            val songService = SongService(this)
            val count = songService.count()
            sharedPreferences!!.edit().putLong(CommonConstants.NO_OF_SONGS, count).apply()
        }
    }

    private fun setDrawerLayout() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            toolbar.elevation = 0f
        }
        setSupportActionBar(toolbar)

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setNavigationView(savedInstanceState: Bundle?) {
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        setHeaderView(navigationView)
        if (savedInstanceState == null) {
            val item = navigationView.menu.getItem(0)
            onNavigationItemSelected(item)
            navigationView.setCheckedItem(R.id.home)
        }
        val colorStateList = colorStateList
        navigationView.itemTextColor = colorStateList
        navigationView.itemIconTintList = colorStateList
        val versionTextView = navigationView.findViewById<View>(R.id.version) as TextView
        versionTextView.text = getString(R.string.version, CommonUtils.projectVersion)
    }

    private fun setHeaderView(navigationView: NavigationView) {
        val headerView = navigationView.getHeaderView(0)
        val headerSubTitleTextView = headerView.findViewById<View>(R.id.header_subtitle) as TextView
        headerSubTitleTextView.text = getString(R.string.noOfSongsAvailable,
                sharedPreferences!!.getLong(CommonConstants.NO_OF_SONGS, 0))
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> setHomeView()
            R.id.updateSongs -> setUpdateView()
            R.id.settings -> startActivity(Intent(this@NavigationDrawerActivity,
                    UserSettingActivity::class.java))
            R.id.rateUs -> setRateUsView()
            R.id.share -> setShareView()
            R.id.feedback -> setEmail()
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setHomeView() {
        val fragmentManager = supportFragmentManager
        val existingHomeTabFragment = fragmentManager
                .findFragmentByTag(HomeFragment::class.java!!.getSimpleName()) as HomeFragment?
        if (existingHomeTabFragment == null) {
            val fragment = HomeFragment.newInstance()
            fragment.arguments = intent.extras
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.content_frame, fragment, HomeFragment::class.java!!.getSimpleName())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }


    private fun setUpdateView() {
        val updateSongs = Intent(this@NavigationDrawerActivity,
                UpdateSongsDatabaseActivity::class.java)
        startActivityForResult(updateSongs, UPDATE_DB_REQUEST_CODE)
    }

    private fun setRateUsView() {
        val uri = Uri.parse("market://details?id=" + this@NavigationDrawerActivity.applicationContext.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(flags)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this@NavigationDrawerActivity.applicationContext.packageName)))
        }

    }

    private fun setShareView() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.app_description) + getString(R.string.app_download_info))
        shareIntent.type = "text/plain"
        val intent = Intent.createChooser(shareIntent, getString(R.string.share) + " "
                + getString(R.string.app_name) + " in")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(shareIntent)
    }

    private fun setEmail() {
        val mailIntent = Intent(Intent.ACTION_SENDTO)
        mailIntent.data = Uri.parse("mailto:$SENDER_MAIL")
        mailIntent.putExtra(Intent.EXTRA_EMAIL, "")
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, getEmailSubject(applicationContext))
        startActivity(Intent.createChooser(mailIntent, ""))
    }

     fun getEmailSubject(context: Context): String {
        try {
            val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
            return String.format(context.getString(R.string.feedback_subject), versionName)
        } catch (e: PackageManager.NameNotFoundException) {
            return getString(R.string.feedback)
        }

    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            UPDATE_DB_REQUEST_CODE -> {
                val noOfSongs = sharedPreferences!!.getLong(CommonConstants.NO_OF_SONGS, 0)
                sharedPreferences!!.edit().putLong(CommonConstants.NO_OF_SONGS, noOfSongs).apply()
                val headerSubTitleTextView = findViewById<View>(R.id.header_subtitle) as TextView
                headerSubTitleTextView.text = getString(R.string.noOfSongsAvailable, noOfSongs)
            }
            else -> {
            }
        }
    }

    public override fun onResume()
    {
        super.onResume()
        presentationScreenService!!.onResume()
    }
    override fun onPause() {
        super.onPause()
        presentationScreenService!!.onPause()
    }

    override fun onStop() {
        super.onStop()
        presentationScreenService!!.onStop()
    }

    companion object {
        private val UPDATE_DB_REQUEST_CODE = 555
        private val SENDER_MAIL = "appfeedback@mcruncher.com"
    }
}
