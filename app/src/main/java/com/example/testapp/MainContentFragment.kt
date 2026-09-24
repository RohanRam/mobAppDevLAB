package com.example.testapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class MainContentFragment : Fragment(R.layout.fragment_main_content) {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabGallery: ImageView
    private lateinit var tabHome: ImageView
    private lateinit var tabWeb: ImageView

    private val activeColor = Color.parseColor("#007AFF")
    private val inactiveColor = Color.parseColor("#8E8E93")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager)
        tabGallery = view.findViewById(R.id.tabGallery)
        tabHome = view.findViewById(R.id.tabHome)
        tabWeb = view.findViewById(R.id.tabWeb)

        val adapter = MainPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = true

        // Set Home (position 1) as default tab
        if (savedInstanceState == null) {
            viewPager.setCurrentItem(1, false)
            updateNavTabUI(1)
        }

        tabGallery.setOnClickListener { handleTabClick(0) }
        tabHome.setOnClickListener { handleTabClick(1) }
        tabWeb.setOnClickListener { handleTabClick(2) }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateNavTabUI(position)
            }
        })
    }

    private fun handleTabClick(tabIndex: Int) {
        view?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        if (viewPager.currentItem == tabIndex) {
            // Tab re-selected: trigger scroll to top
            val currentFragment = childFragmentManager.findFragmentByTag("f${viewPager.currentItem}")
            when (tabIndex) {
                0 -> (currentFragment as? GalleryFragment)?.scrollToTop()
                1 -> (currentFragment as? HomeCoursesFragment)?.scrollToTop()
            }
        } else {
            viewPager.setCurrentItem(tabIndex, true)
            updateNavTabUI(tabIndex)
        }
    }

    private fun updateNavTabUI(selectedTab: Int) {
        tabGallery.imageTintList = ColorStateList.valueOf(if (selectedTab == 0) activeColor else inactiveColor)
        tabHome.imageTintList = ColorStateList.valueOf(if (selectedTab == 1) activeColor else inactiveColor)
        tabWeb.imageTintList = ColorStateList.valueOf(if (selectedTab == 2) activeColor else inactiveColor)
    }

    fun selectTab(position: Int) {
        if (::viewPager.isInitialized) {
            viewPager.setCurrentItem(position, true)
            updateNavTabUI(position)
        }
    }

    private class MainPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> GalleryFragment()
                1 -> HomeCoursesFragment()
                2 -> WebViewFragment()
                else -> HomeCoursesFragment()
            }
        }
    }
}
