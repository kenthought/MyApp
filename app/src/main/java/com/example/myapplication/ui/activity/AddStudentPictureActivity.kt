package com.example.myapplication.ui.activity

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.classes.StudentItem
import com.example.myapplication.classes.StudentPictureItem
import com.example.myapplication.classes.config
import com.example.myapplication.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*


class AddStudentPictureActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var file_name: TextView
    private var imageUri: Uri? = null
    private var id = 0
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_student_picture)

        getRequest()

        spinner = findViewById(R.id.spinner)

        val button: Button = findViewById(R.id.add_photo)
        button.setOnClickListener {
            startChooseImageIntentForResult()
        }

        file_name = findViewById(R.id.file_name)

        val button_add: Button = findViewById(R.id.button_addPic)
        button_add.setOnClickListener {
            postRequest()
        }

    }

    fun getRequest() {
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(config.BASE_URL)
            .build()
            .create(ApiService::class.java)

        val student = retrofit.getData()

        student.enqueue(object : Callback<List<StudentItem>?> {
            override fun onResponse(
                call: Call<List<StudentItem>?>,
                response: Response<List<StudentItem>?>
            ) {
                val responseBody = response.body()

                Log.d("Get Response: ", responseBody.toString())

                if (responseBody != null) {
                    var options: ArrayList<String> = ArrayList()
                    for (item in responseBody) {
                        options.add(item.id.toString() + ". " + item.first_name + " " + item.last_name)
                    }
                    val dataAdapter = ArrayAdapter(
                        applicationContext, android.R.layout.simple_spinner_dropdown_item, options
                    )
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = dataAdapter
                } else {
                    val text = "Error Fetching Student!"
                    val duration = Toast.LENGTH_SHORT

                    val toast = Toast.makeText(applicationContext, text, duration)
                    toast.show()
                }
            }

            override fun onFailure(call: Call<List<StudentItem>?>, t: Throwable) {
            }
        })
    }

    fun postRequest() {
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(config.BASE_URL)
            .build()
            .create(ApiService::class.java)

        val studentPictureItem = StudentPictureItem(id, file_name.text.toString())

        val student_picture = retrofit.addPicture(studentPictureItem);

        student_picture.enqueue(object : Callback<StudentPictureItem?> {
            override fun onResponse(
                call: Call<StudentPictureItem?>,
                response: Response<StudentPictureItem?>
            ) {
                println("RESPONSE: " + response.body().toString())
                val text = "Student Picture Added!"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }

            override fun onFailure(call: Call<StudentPictureItem?>, t: Throwable) {
//                println(t.printStackTrace())
//                val text = "Error Adding Picture!"
//                val duration = Toast.LENGTH_SHORT
//
//                val toast = Toast.makeText(applicationContext, text, duration)
//                toast.show()
            }
        })
//        sendImage()
    }

    fun sendImage() {
        Log.d("PATH: ", imageUri!!.lastPathSegment.toString())
        var file = File(imageUri.toString())
        Log.d("PATH2: ", file.toString())
        val bitmap : Bitmap = BitmapFactory.decodeFile(file.path);
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray : ByteArray = byteArrayOutputStream.toByteArray()

        val encodedImage: String = Base64.encodeToString(byteArray, Base64.DEFAULT)

        Log.d("Encoded String: ", encodedImage)
    }

    private fun startChooseImageIntentForResult() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            1002
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1002 && resultCode == Activity.RESULT_OK) {
            // In this case, imageUri is returned by the chooser, save it.
            imageUri = data!!.data

            file_name.text = imageUri!!.lastPathSegment.toString()
                .substring(imageUri!!.lastPathSegment.toString().lastIndexOf("/") + 1)

            name = spinner.selectedItem.toString().substring(
                spinner.selectedItem.toString().indexOf(".") + 1,
                spinner.selectedItem.toString().length
            )
            id = Integer.parseInt(
                spinner.selectedItem.toString()
                    .substring(0, spinner.selectedItem.toString().indexOf(".", -1))
            )
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    fun addStudentPicture() {
        val fileName: String = file_name.text.substring(file_name.text.lastIndexOf("/") + 1)
        val text = "Id: " + id.toString() + "Name: " + name + " Image: " + fileName
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }

}