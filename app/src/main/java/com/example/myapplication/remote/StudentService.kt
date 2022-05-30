package com.example.myapplication.remote

import com.example.myapplication.classes.StudentItem
import retrofit2.Call
import retrofit2.http.GET

interface StudentService {

    @GET("getStudents")
    fun getData() : Call<List<StudentItem>>
}