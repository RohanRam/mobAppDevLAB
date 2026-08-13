package com.example.testapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameEditText = view.findViewById<TextInputEditText>(R.id.usernameEditText)
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = view.findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Save username to ViewModel
                viewModel.setUsername(username)
                // For this experiment, any non-empty credentials work
                findNavController().navigate(R.id.action_loginFragment_to_mainContentFragment)
            } else {
                Toast.makeText(requireContext(), getString(R.string.login_error), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
