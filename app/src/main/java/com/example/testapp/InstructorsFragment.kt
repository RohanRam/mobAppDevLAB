package com.example.testapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation

data class Instructor(val name: String, val dept: String, val imageUrl: String)

class InstructorsFragment : Fragment(R.layout.fragment_instructors) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val instructors = listOf(
            Instructor("Dr. Alice Smith", "Computer Science", "https://i.pravatar.cc/150?u=alice"),
            Instructor("Prof. Bob Johnson", "Mathematics", "https://i.pravatar.cc/150?u=bob"),
            Instructor("Dr. Charlie Brown", "Physics", "https://i.pravatar.cc/150?u=charlie"),
            Instructor("Ms. Diana Prince", "Information Technology", "https://i.pravatar.cc/150?u=diana"),
            Instructor("Mr. Edward Norton", "Software Engineering", "https://i.pravatar.cc/150?u=edward")
        )

        val listView = view.findViewById<ListView>(R.id.instructorsListView)
        listView.adapter = InstructorAdapter(requireContext(), instructors)
    }

    private class InstructorAdapter(context: Context, private val dataSource: List<Instructor>) : BaseAdapter() {

        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int = dataSource.size

        override fun getItem(position: Int): Any = dataSource[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val holder: ViewHolder

            if (convertView == null) {
                view = inflater.inflate(R.layout.item_instructor, parent, false)
                holder = ViewHolder()
                holder.nameTextView = view.findViewById(R.id.instructorName)
                holder.deptTextView = view.findViewById(R.id.instructorDept)
                holder.imageView = view.findViewById(R.id.instructorImage)
                view.tag = holder
            } else {
                view = convertView
                holder = convertView.tag as ViewHolder
            }

            val instructor = getItem(position) as Instructor
            holder.nameTextView.text = instructor.name
            holder.deptTextView.text = instructor.dept
            
            // Modern image loading with Coil and circular crop
            holder.imageView.load(instructor.imageUrl) {
                crossfade(true)
                placeholder(R.mipmap.ic_launcher_round)
                transformations(CircleCropTransformation())
            }

            return view
        }

        private class ViewHolder {
            lateinit var nameTextView: TextView
            lateinit var deptTextView: TextView
            lateinit var imageView: ImageView
        }
    }
}
