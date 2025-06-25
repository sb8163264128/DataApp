package com.unplugged.dataapp.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// Needed to launch the app first time to get permissions
class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "DataApp Launcher Activity"
            textSize = 24f
            gravity = android.view.Gravity.CENTER
        }
        setContentView(textView)

    }
}