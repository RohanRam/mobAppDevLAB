package com.example.testapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class ItemDetailFragment : Fragment(R.layout.fragment_item_detail) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = view.findViewById<TextView>(R.id.detailTitle)
        val description = view.findViewById<TextView>(R.id.detailDescription)

        viewModel.selectedCourse.observe(viewLifecycleOwner) { course ->
            title.text = course.title
            description.text = course.description
        }
    }
}
