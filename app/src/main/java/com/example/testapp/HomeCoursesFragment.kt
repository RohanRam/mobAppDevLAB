package com.example.testapp

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout

class HomeCoursesFragment : Fragment(R.layout.fragment_home_courses) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val slidingPaneLayout = view.findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
        
        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED_CLOSED

        val backCallback = object : OnBackPressedCallback(
            slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen
        ) {
            override fun handleOnBackPressed() {
                slidingPaneLayout.closePane()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

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

    fun scrollToTop() {
        childFragmentManager.findFragmentById(R.id.list_container)?.let { fragment ->
            (fragment as? ItemListFragment)?.scrollToTop()
        }
    }
}
