package com.example.testapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.example.testapp.data.AppDatabase
import com.example.testapp.data.UserCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: MainViewModel by activityViewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted.
        } else {
            // Permission denied.
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val usernameEditText = view.findViewById<TextInputEditText>(R.id.usernameEditText)
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val enrollmentButton = view.findViewById<Button>(R.id.enrollmentButton)

        // --- 1. LOAD SAVED DATA ON OPEN ---
        val sharedPref = requireActivity().getSharedPreferences("preference_file", Context.MODE_PRIVATE)

        val savedUsername = sharedPref.getString("saved_username_key", "")
        if (!savedUsername.isNullOrEmpty()) {
            usernameEditText.setText(savedUsername)
            usernameEditText.setSelection(savedUsername.length) // Place cursor at the end
        }

        val savedPassword = sharedPref.getString("saved_password_key", "")
        if (!savedPassword.isNullOrEmpty()) {
            passwordEditText.setText(savedPassword)
        }

        // --- 2. SAVE USERNAME WHILE TYPING ---
        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                savePreference("saved_username_key", s.toString())
            }
        })

        // --- 3. SAVE PASSWORD WHILE TYPING ---
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                savePreference("saved_password_key", s.toString())
            }
        })

        enrollmentButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_enrollmentFragment)
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val sharedPref = requireActivity().getSharedPreferences("preference_file", Context.MODE_PRIVATE)
                val prefUsername = sharedPref.getString("saved_username_key", username) ?: username
                val prefPassword = sharedPref.getString("saved_password_key", password) ?: password

                val db = AppDatabase.getDatabase(requireContext())
                val dao = db.userCredentialDao()
                
                lifecycleScope.launch(Dispatchers.IO) {
                    dao.insertCredential(UserCredential("saved_username_key", prefUsername))
                    dao.insertCredential(UserCredential("saved_password_key", prefPassword))
                    dao.insertLoginRecord(com.example.testapp.data.LoginRecord(username = prefUsername, password = prefPassword))
                }

                viewModel.setUsername(username)
                showLoginNotification()
                findNavController().navigate(R.id.action_loginFragment_to_mainContentFragment)
            } else {
                Toast.makeText(requireContext(), getString(R.string.login_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- 4. UNIFIED HELPER FUNCTION FOR SAVING ---
    private fun savePreference(key: String, value: String) {
        val sharedPref = requireActivity().getSharedPreferences("preference_file", Context.MODE_PRIVATE)
        sharedPref.edit().putString(key, value).apply() // .apply() runs asynchronously so typing stays smooth
    }

    private fun showLoginNotification() {
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(requireContext(), MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setSound(soundUri)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(requireContext())) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            ) {
                notify(1001, builder.build())
            }
        }
    }
}
