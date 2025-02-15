package com.myowencode.primaryclass.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.myowencode.primaryclass.R
import com.myowencode.primaryclass.db.PrimaryClassDatabase
import com.myowencode.primaryclass.db.Student

class StudentListAdapter : RecyclerView.Adapter<StudentListAdapter.StudentViewHolder>() {
    private var studentList = emptyList<Student>()

    class StudentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        return StudentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.student_row, parent, false))
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.itemView.findViewById<TextView>(R.id.full_name).text = student.getFullName()
        holder.itemView.findViewById<TextView>(R.id.birthday).text = student.getBirthday()
        var gender = R.drawable.female
        if (student.gender.equals(PrimaryClassDatabase.GENDER_MALE)) {
            gender = R.drawable.male
        }
        holder.itemView.findViewById<ImageView>(R.id.gender_image).setImageResource(gender)

        holder.itemView.findViewById<LinearLayout>(R.id.student_row).setOnClickListener {
            val action = StudentListFragmentDirections.actionStudentListFragmentToUpdateStudentFragment(student)
            holder.itemView.findNavController().navigate(action)
        }
    }

    fun setStudentList(studentList: List<Student>) {
        this.studentList = studentList
        notifyDataSetChanged()
    }
}