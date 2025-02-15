package com.myowencode.primaryclass.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.myowencode.primaryclass.R
import com.myowencode.primaryclass.db.PrimaryClassDatabase
import com.myowencode.primaryclass.db.Student
import com.myowencode.primaryclass.db.StudentViewModel
import java.util.Calendar
import java.util.Date

abstract class StudentUpdaterFragmentBase : Fragment() {

    lateinit var viewModel : StudentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(getLayoutId(), container, false)

        viewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        view.findViewById<Button>(getSaveButtonId()).setOnClickListener {
            upsertStudent(view)
        }
        return view
    }

    abstract fun getLayoutId() : Int
    abstract fun getSaveButtonId() : Int
    abstract fun getBackNavigationId() : Int
    abstract fun getStudentId() : Int

    private fun upsertStudent(view: View) {
        val firstName = view.findViewById<EditText>(R.id.first_name).text.toString()
        val lastName = view.findViewById<EditText>(R.id.last_name).text.toString()
        val gender = getGender(view)
        val birthday = getBirthday(view)

        if (isValid(firstName, lastName, gender)) {
            val student = Student(getStudentId(), firstName, lastName, gender, birthday)

            viewModel.upsert(student)
            Toast.makeText(requireContext(), "Save complete", Toast.LENGTH_SHORT).show()
            findNavController().navigate(getBackNavigationId())
        }
        else {
            Toast.makeText(requireContext(), "Please provide gender, name, and birthday", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getGender(view: View) : String {
        val genderSelector = view.findViewById<RadioGroup>(R.id.gender).checkedRadioButtonId
        var gender : String = ""
        if (genderSelector == R.id.male) {
            gender = PrimaryClassDatabase.GENDER_MALE
        }
        else if (genderSelector == R.id.female) {
            gender = PrimaryClassDatabase.GENDER_FEMALE
        }
        return gender
    }

    private fun getBirthday(view: View) : Date {
        val picker = view.findViewById<DatePicker>(R.id.birthday_picker)
        val c = Calendar.getInstance()
        c.set(2000, picker.month, picker.dayOfMonth) // force year to 2000 because i don't care
        return Date(c.timeInMillis)
    }

    private fun isValid(firstName: String, lastName: String, gender: String) : Boolean {
        return !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(gender)
    }
}