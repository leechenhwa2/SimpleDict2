package com.uniuwo.simpledict

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.uniuwo.simpledict.databinding.ActivityMainBinding
import com.uniuwo.simpledcit.core.databus.SimpleDataBus
import com.uniuwo.simpledict.utils.Toastx


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    var lastBackPressedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Note: assure wordListRepo is populated first
        requirePermissions(this)
        initDatabases()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        binding.appBarMain.fab.visibility = View.GONE

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_word_list, R.id.nav_word_favorite, R.id.nav_help
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //Note: assure wordListRepo is populated before fragments, here is late
//        requirePermissions(this)
//        initDatabases()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val time = System.currentTimeMillis()
        if((time - lastBackPressedTime) > 1000 * 2){
            lastBackPressedTime = time
            Toastx.short(this, "再按一次 [返回键] 退出")
            return
        } else {
            finish()
        }

        super.onBackPressed()
    }

    private fun requirePermissions(activity: Activity) {
        val permissionCheck = ContextCompat.checkSelfPermission(
            activity.applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf<String>(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
            return
        }
    }


    private fun initDatabases() {
        Log.d(TAG, "Call --- initDatabases")
        requirePermissions(this)

        SimpleDataBus.checkFolders(applicationContext)
        SimpleDataBus.initWordList(applicationContext)
        Thread {
            SimpleDataBus.initDatabases(applicationContext)
        }.start()
    }

    companion object {
        private const val TAG = "Main"
        private const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: Int = 1
    }
}