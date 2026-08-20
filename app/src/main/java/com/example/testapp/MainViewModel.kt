package com.example.testapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _selectedCourse = MutableLiveData<Course>()
    val selectedCourse: LiveData<Course> = _selectedCourse

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    fun selectCourse(course: Course) {
        _selectedCourse.value = course
    }

    fun setUsername(name: String) {
        _username.value = name
    }
}
