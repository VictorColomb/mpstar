package com.example.mpstar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mpstar.model.Student

class MyViewModel : ViewModel(){

    private val students: MutableLiveData<List<Student>> by lazy {
        MutableLiveData<List<Student>>().also {
            loadUsers()
        }
    }

    fun getUsers(): LiveData<List<Student>>{
        return students
    }

    private fun loadUsers(){
        //does asynchronous shit
    }
}