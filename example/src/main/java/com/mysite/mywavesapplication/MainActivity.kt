package com.mysite.mywavesapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.mysite.mywavesapplication.keeper.KeeperFragment
import com.mysite.mywavesapplication.sdk.SdkFragment
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.utils.Environment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val fragments = arrayListOf(
        R.string.bottom_navigation_sdk_title to SdkFragment.newInstance(),
        R.string.bottom_navigation_keeper_title to KeeperFragment.newInstance()
    )

    private var currentFragment = fragments.first().second

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_keeper -> {
                    showFragment(fragments[KEEPER_POSITION])
                }
                R.id.action_sdk -> {
                    showFragment(fragments[SDK_POSITION])
                }
                else -> {
                    showFragment(fragments[SDK_POSITION])
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        showFragment(fragments[SDK_POSITION])
    }

    private fun showFragment(pair: Pair<Int, androidx.fragment.app.Fragment>) {
        val (title, fragment) = pair
        val fragmentTitle = getString(title)

        toolbar.title = fragmentTitle

        if (supportFragmentManager.findFragmentByTag(fragmentTitle) == null) {
            supportFragmentManager.beginTransaction()
                .hide(currentFragment)
                .add(R.id.frame_fragment_container, fragment, fragmentTitle)
                .show(fragment)
                .commitAllowingStateLoss()
        } else {
            supportFragmentManager.beginTransaction()
                .hide(currentFragment)
                .show(fragment)
                .commitAllowingStateLoss()
        }
        currentFragment = fragment
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
                Environment.STAGE_NET -> {
                    getString(R.string.environment_stage)
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
                        item.title = getString(R.string.environment_stage)
                        WavesSdk.setEnvironment(Environment.STAGE_NET)
                    }
                    getString(R.string.environment_stage) -> {
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

    companion object {
        const val SDK_POSITION = 0
        const val KEEPER_POSITION = 1
    }
}
