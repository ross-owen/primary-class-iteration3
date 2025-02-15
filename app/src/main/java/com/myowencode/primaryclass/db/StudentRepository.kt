package com.myowencode.primaryclass.db

import androidx.lifecycle.LiveData

class StudentRepository(private val studentDao: StudentDao) {
    val list: LiveData<List<Student>> = studentDao.list()
    
    suspend fun upsert(student: Student) {
        if (student.id == 0) {
            studentDao.create(student)
        } else {
            studentDao.update(student)
        }
    }

    suspend fun delete(student: Student) {
        studentDao.delete(student)
    }

    suspend fun deleteAll() {
        studentDao.deleteAll()
    }
}