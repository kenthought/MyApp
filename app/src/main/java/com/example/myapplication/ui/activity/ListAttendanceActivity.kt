package com.example.myapplication.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.classes.ListAttendance


class ListAttendanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val b = intent.extras
        val timeType = b!!.getString("timeType")
        val subject = b!!.getString("subject")
        val attendance = b.getParcelableArrayList<ListAttendance>("attendance")
        Log.d("VALUE", "$timeType $subject")

        attendance!!.forEach({ println(it.student_name + " " + it.time) })

        val i = intent
        setContentView(R.layout.activity_list_attendance)
    }
}