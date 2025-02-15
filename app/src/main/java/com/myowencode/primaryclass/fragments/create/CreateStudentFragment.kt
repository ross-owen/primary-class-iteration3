package com.myowencode.primaryclass.fragments.create

import com.myowencode.primaryclass.R
import com.myowencode.primaryclass.fragments.StudentUpdaterFragmentBase

class CreateStudentFragment : StudentUpdaterFragmentBase() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_create_student
    }

    override fun getSaveButtonId(): Int {
        return R.id.save
    }

    override fun getStudentId(): Int {
        return 0
    }

    override fun getBackNavigationId(): Int {
        return R.id.action_createStudentFragment_to_studentListFragment
    }
}