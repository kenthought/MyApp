package com.example.myapplication.remote

import com.example.myapplication.classes.StudentItem
import com.example.myapplication.classes.StudentPictureItem
import com.example.myapplication.classes.SubjectsItem
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("getStudents")
    fun getStudents() : Call<List<StudentItem>>

    @GET("getSubjects")
    fun getSubjects() : Call<List<SubjectsItem>>

    @POST("addStudentPicture")
    @Streaming
    fun addPicture(@Body studentPictureItem : StudentPictureItem) : Call<StudentPictureItem>

//    @FormUrlEncoded
//    @POST("addStudentPicture")
//    fun addPicture(@Field("student_id") student_id : Int, @Field("file_name") file_name : String) : Call<StudentPictureItem>
}