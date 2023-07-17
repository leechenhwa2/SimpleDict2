package com.uniuwo.simpledict.app2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.uniuwo.simpledcit.core.databus.SimpleDataBus
import com.uniuwo.simpledict.app2.ui.theme.SimpleDictTheme

class MainActivity : ComponentActivity() {
    var lastBackPressedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initDatabases()

        setContent {
            SimpleDictTheme {
                DictAppNavHost()
            }
        }
    }

    private fun initDatabases() {
        SimpleDataBus.checkFolders(applicationContext)
        SimpleDataBus.initWordList(applicationContext)
        Thread {
            SimpleDataBus.initDatabases(applicationContext)
        }.start()
    }

    /*override fun onBackPressed() {
        val time = System.currentTimeMillis()
        if ((time - lastBackPressedTime) > 1000 * 2) {
            lastBackPressedTime = time
            Toast.makeText(this, "再按一次 [返回键] 退出", Toast.LENGTH_SHORT).show()
            return
        } else {
            finish()
        }

        super.onBackPressed()
    }*/
}

