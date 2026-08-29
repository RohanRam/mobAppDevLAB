package com.example.testapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText

class EnrollmentFragment : Fragment(R.layout.fragment_enrollment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameEditText = view.findViewById<TextInputEditText>(R.id.studentNameEditText)
        val usnEditText = view.findViewById<TextInputEditText>(R.id.usnEditText)
        val programRadioGroup = view.findViewById<RadioGroup>(R.id.programRadioGroup)
        val termsCheckBox = view.findViewById<CheckBox>(R.id.enrollTermsCheckBox)
        val progressBar = view.findViewById<ProgressBar>(R.id.enrollProgressBar)
        val submitButton = view.findViewById<Button>(R.id.confirmEnrollButton)

        // logic to update progress bar based on fields filled
        val updateProgress = {
            var progress = 0
            if (nameEditText.text?.isNotEmpty() == true) progress += 25
            if (usnEditText.text?.isNotEmpty() == true) progress += 25
            if (programRadioGroup.checkedRadioButtonId != -1) progress += 25
            if (termsCheckBox.isChecked) progress += 25
            progressBar.progress = progress
        }

        // Add listeners to update progress
        nameEditText.setOnFocusChangeListener { _, _ -> updateProgress() }
        usnEditText.setOnFocusChangeListener { _, _ -> updateProgress() }
        programRadioGroup.setOnCheckedChangeListener { _, _ -> updateProgress() }
        termsCheckBox.setOnCheckedChangeListener { _, _ -> updateProgress() }

        submitButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val usn = usnEditText.text.toString()
            val selectedProgramId = programRadioGroup.checkedRadioButtonId
            val program = if (selectedProgramId != -1) {
                view.findViewById<RadioButton>(selectedProgramId).text
            } else {
                "Not Selected"
            }
            val termsAccepted = termsCheckBox.isChecked

            if (name.isEmpty() || usn.isEmpty() || !termsAccepted) {
                Toast.makeText(requireContext(), "Please complete the enrollment form", Toast.LENGTH_SHORT).show()
            } else {
                val message = "Student: $name\nUSN: $usn\nProgram: $program\nEnrollment Successful!"
                CustomToaster.show(requireContext(), message)
                findNavController().popBackStack()
            }
        }
    }
}
