package com.myowencode.primaryclass.fragments.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.myowencode.primaryclass.R
import com.myowencode.primaryclass.db.PrimaryClassDatabase
import com.myowencode.primaryclass.db.Student
import com.myowencode.primaryclass.fragments.StudentUpdaterFragmentBase
import java.util.Calendar

class UpdateStudentFragment : StudentUpdaterFragmentBase() {

    private val args by navArgs<UpdateStudentFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = super.onCreateView(inflater, container, savedInstanceState)

        if (view != null) {
            populateStudentData(view, args.student)
        }

        setHasOptionsMenu(true)

        return view
    }

    private fun populateStudentData(view: View, student: Student) {
        view.findViewById<EditText>(R.id.first_name)?.setText(student.firstName)
        view.findViewById<EditText>(R.id.last_name)?.setText(student.lastName)

        // birthday
        val picker = view.findViewById<DatePicker>(R.id.birthday_picker)
        if (student.birthDate != null) {
            val c : Calendar = Calendar.getInstance()
            c.time = student.birthDate!!
            picker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        }

        // gender
        var gender = R.id.female
        if (student.gender.equals(PrimaryClassDatabase.GENDER_MALE)) {
            gender = R.id.male
        }
        view.findViewById<RadioGroup>(R.id.gender).check(gender)
    }

    override fun getStudentId(): Int {
        return args.student.id
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_update_student
    }

    override fun getSaveButtonId(): Int {
        return R.id.update
    }

    override fun getBackNavigationId(): Int {
        return R.id.action_updateStudentFragment_to_studentListFragment
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete) {
            deleteUser()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteUser() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") {_, _ ->
            viewModel.delete(args.student)
            Toast.makeText(requireContext(), "${args.student.firstName} was deleted", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateStudentFragment_to_studentListFragment)
        }
        builder.setNegativeButton("No") {_, _ ->}
        builder.setTitle("Delete ${args.student.firstName} ${args.student.lastName}?")
        builder.setMessage("Are you sure you want to delete ${args.student.firstName}")
        builder.create().show()
    }
}