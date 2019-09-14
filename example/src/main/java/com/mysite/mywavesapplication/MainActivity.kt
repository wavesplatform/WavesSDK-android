package com.mysite.mywavesapplication

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.mysite.mywavesapplication.keeper.KeeperFragment
import com.mysite.mywavesapplication.sdk.SdkFragment
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.utils.Environment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val sdkFragment = SdkFragment.newInstance()
        val keeperFragment = KeeperFragment.newInstance()

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_keeper -> {
                    showFragment(keeperFragment, R.string.bottom_navigation_keeper_title)
                }
                R.id.action_sdk -> {
                    showFragment(sdkFragment, R.string.bottom_navigation_sdk_title)
                }
                else -> {
                    showFragment(sdkFragment, R.string.bottom_navigation_sdk_title)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        showFragment(sdkFragment, R.string.bottom_navigation_sdk_title)
    }

    private fun showFragment(fragment: Fragment, @StringRes title: Int) {
        toolbar.setTitle(title)
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.frame_fragment_container, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menu.findItem(R.id.action_change_environment).title =
            when (WavesSdk.getEnvironment()) {
                Environment.MAIN_NET -> {
                    getString(R.string.environment_main)
                }
                Environment.TEST_NET -> {
                    getString(R.string.environment_test)
                }
                else -> {
                    getString(R.string.environment_custom)
                }
            }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_change_environment -> {
                when (item.title) {
                    getString(R.string.environment_main) -> {
                        item.title = getString(R.string.environment_test)
                        WavesSdk.setEnvironment(Environment.TEST_NET)
                    }
                    getString(R.string.environment_test) -> {
                        item.title = getString(R.string.environment_main)
                        WavesSdk.setEnvironment(Environment.MAIN_NET)
                    }
                    else -> {
                        item.title = getString(R.string.environment_test)
                        WavesSdk.setEnvironment(Environment.TEST_NET)
                    }
                }

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
