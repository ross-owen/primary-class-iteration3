package com.myowencode.primaryclass.fragments.list

import kotlinx.serialization.Serializable

@Serializable
public class StudentDto(
    var firstName: String,
    var lastName: String,
    var gender: String,
    var birthDay: String) {
}