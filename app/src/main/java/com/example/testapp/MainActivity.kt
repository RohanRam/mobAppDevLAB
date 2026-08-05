package com.example.testapp

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.slidingpanelayout.widget.SlidingPaneLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val slidingPaneLayout = findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        
        // Lock the detail pane initially if it's meant to be a list-first flow
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED_CLOSED

        val backCallback = object : OnBackPressedCallback(
            slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen
        ) {
            override fun handleOnBackPressed() {
                slidingPaneLayout.closePane()
            }
        }
        onBackPressedDispatcher.addCallback(this, backCallback)

        slidingPaneLayout.addPanelSlideListener(object : SlidingPaneLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {}
            override fun onPanelOpened(panel: View) {
                backCallback.isEnabled = true
            }
            override fun onPanelClosed(panel: View) {
                backCallback.isEnabled = false
            }
        })
    }
}
