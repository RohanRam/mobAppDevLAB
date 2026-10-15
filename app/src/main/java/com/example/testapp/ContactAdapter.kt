package com.example.testapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private val contacts: List<ContactItem>) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.contactName)
        val phoneTextView: TextView = view.findViewById(R.id.contactPhone)
        val dividerView: View = view.findViewById(R.id.contactDivider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.nameTextView.text = contact.name.ifEmpty { "Unknown" }
        holder.phoneTextView.text = contact.phoneNumber.ifEmpty { "No Phone Number" }

        if (position == contacts.size - 1) {
            holder.dividerView.visibility = View.GONE
        } else {
            holder.dividerView.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = contacts.size
}
