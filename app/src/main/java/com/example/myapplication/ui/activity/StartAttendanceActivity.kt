package com.example.myapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.classes.SubjectsItem
import com.example.myapplication.classes.config
import com.example.myapplication.remote.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class StartAttendanceActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton: RadioButton
    private lateinit var startAttendanceButton: Button;
    private var timeType : String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_start_attendance)

        spinner = findViewById(R.id.spinner)
        radioGroup = findViewById(R.id.radioGroup);
        startAttendanceButton = findViewById(R.id.button_startAttendance)

        getRequest()

        startAttendanceButton.setOnClickListener{
            val selectedId = radioGroup.checkedRadioButtonId
            radioButton = findViewById<View>(selectedId) as RadioButton

            Log.d("startAttendance:", radioButton.text.toString() + " " + spinner.selectedItem.toString())

            val intent = Intent(applicationContext, FaceRecognitionActivity::class.java).apply {
                putExtra("time_type", radioButton.text.toString())
                putExtra("subject", spinner.selectedItem.toString())
            }
            startActivity(intent)
        }
    }

    fun getRequest() {
        Log.d("hi: ", "ning agi ko diri")
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(config.BASE_URL)
            .build()
            .create(ApiService::class.java)

        val subjects = retrofit.getSubjects()

        subjects.enqueue(object : Callback<List<SubjectsItem>?> {
            override fun onResponse(
                call: Call<List<SubjectsItem>?>,
                response: Response<List<SubjectsItem>?>
            ) {
                val responseBody = response.body()

                Log.d("Get Response: ", responseBody.toString())

                if (responseBody != null) {
                    var options: ArrayList<String> = ArrayList()
                    for (item in responseBody) {
                        options.add(item.id.toString() + ". " + item.subject_name)
                    }
                    val dataAdapter = ArrayAdapter(
                        applicationContext, android.R.layout.simple_spinner_dropdown_item, options
                    )
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = dataAdapter
                } else {
                    val text = "Error Fetching Subject"
                    val duration = Toast.LENGTH_SHORT

                    val toast = Toast.makeText(applicationContext, text, duration)
                    toast.show()
                }
            }

            override fun onFailure(call: Call<List<SubjectsItem>?>, t: Throwable) {
            }
        })
    }
}