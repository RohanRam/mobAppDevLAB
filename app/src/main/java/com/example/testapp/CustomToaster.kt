package com.example.testapp

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast

class CustomToaster {
    companion object {
        fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.layout_custom_toast, null)

            val text: TextView = layout.findViewById(R.id.toast_message)
            text.text = message

            with(Toast(context)) {
                setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
                this.duration = duration
                view = layout
                show()
            }
        }
    }
}