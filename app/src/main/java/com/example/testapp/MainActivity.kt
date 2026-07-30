package com.example.testapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    override fun onStart() {
        super.onStart()
        // It will show a message on the screen
        // then onStart is invoked
        CustomToaster.show(applicationContext, "Application Started", Toast.LENGTH_LONG)
    }

    override fun onRestart() {
        super.onRestart()
        // It will show a message on the screen
        // then onRestart is invoked
        CustomToaster.show(applicationContext, "onRestart Called", Toast.LENGTH_LONG)
    }

    override fun onResume() {
        super.onResume()
        // It will show a message on the screen
        // then onResume is invoked
        CustomToaster.show(applicationContext, "Name : Rohan Ram ", Toast.LENGTH_LONG)
    }

    override fun onPause() {
        super.onPause()
        // It will show a message on the screen
        // then onPause is invoked
        CustomToaster.show(applicationContext, "USN : 25MCAR0114", Toast.LENGTH_LONG)
    }

    override fun onStop() {
        super.onStop()
        // It will show a message on the screen
        // then onStop is invoked
        CustomToaster.show(applicationContext, "Application Stopped", Toast.LENGTH_LONG)
    }

    override fun onDestroy() {
        super.onDestroy()
        // It will show a message on the screen
        // then onDestroy is invoked
        CustomToaster.show(applicationContext, "onDestroy Called", Toast.LENGTH_LONG)
    }

}