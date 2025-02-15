package com.myowencode.primaryclass.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentViewModel(application: Application): AndroidViewModel(application) {
    val students: LiveData<List<Student>>
    private val repository: StudentRepository
    
    init {
        val studentDao = PrimaryClassDatabase.getDatabase(application).getStudentDao()
        repository = StudentRepository(studentDao)
        students = repository.list
    }

    fun upsert(student: Student) {
        viewModelScope.launch(Dispatchers.IO) { 
            repository.upsert(student)
        }
    }

    fun delete(student: Student) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(student)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}