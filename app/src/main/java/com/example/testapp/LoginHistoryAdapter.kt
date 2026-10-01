package com.example.testapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.data.LoginRecord

class LoginHistoryAdapter(private val records: List<LoginRecord>) :
    RecyclerView.Adapter<LoginHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameTextView: TextView = view.findViewById(R.id.historyItemUsername)
        val passwordTextView: TextView = view.findViewById(R.id.historyItemPassword)
        val dividerView: View = view.findViewById(R.id.historyItemDivider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_login_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        
        holder.usernameTextView.text = record.username
        
        val maskedPassword = if (record.password.length > 2) {
            record.password.dropLast(2) + "**"
        } else if (record.password.isNotEmpty()) {
            "*".repeat(record.password.length)
        } else {
            "No Password"
        }
        holder.passwordTextView.text = maskedPassword

        // Hide divider on the last item to match iOS grouped table style
        if (position == records.size - 1) {
            holder.dividerView.visibility = View.GONE
        } else {
            holder.dividerView.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = records.size
}