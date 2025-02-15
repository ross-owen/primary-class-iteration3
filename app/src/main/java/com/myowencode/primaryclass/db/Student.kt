package com.myowencode.primaryclass.db

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date

@Parcelize
@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var firstName: String,
    var lastName: String,
    var gender: String = PrimaryClassDatabase.GENDER_MALE,
    var birthDate: Date?
) : Parcelable {
    fun isMale(): Boolean {
        return gender.equals(PrimaryClassDatabase.GENDER_MALE)
    }

    fun getFullName(): String {
        return "$firstName $lastName"
    }
    
    @SuppressLint("SimpleDateFormat")
    fun getBirthday(): String {
        var bday = ""
        if (birthDate != null) {
            bday = SimpleDateFormat("MMM dd").format(birthDate!!)
        }
        return bday
    }
}