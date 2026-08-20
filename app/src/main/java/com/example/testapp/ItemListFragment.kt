package com.example.testapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.slidingpanelayout.widget.SlidingPaneLayout

class ItemListFragment : Fragment(R.layout.fragment_item_list) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameText = view.findViewById<TextView>(R.id.usernameText)
        viewModel.username.observe(viewLifecycleOwner) { name ->
            usernameText.text = name
        }

        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            viewModel.setUsername("") // Clear username
            findNavController().navigate(R.id.action_mainContentFragment_to_loginFragment)
        }

        val courses = listOf(
            Course(1, "Android Development", "Learn to build Android apps with Kotlin."),
            Course(2, "Web Development", "Master HTML, CSS, and JavaScript."),
            Course(3, "Data Science", "Analyze data with Python and SQL."),
            Course(4, "Machine Learning", "Build intelligent systems with AI.")
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = CourseAdapter(courses) { course ->
            viewModel.selectCourse(course)
            requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout).openPane()
        }
    }
}
