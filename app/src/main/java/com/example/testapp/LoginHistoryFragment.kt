package com.example.testapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.testapp.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginHistoryFragment : Fragment(R.layout.fragment_login_history) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.historyRecyclerView)

        val db = AppDatabase.getDatabase(requireContext())
        val dao = db.userCredentialDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val records = dao.getAllLoginRecords()
            
            withContext(Dispatchers.Main) {
                recyclerView.adapter = LoginHistoryAdapter(records)
            }
        }
    }
}
