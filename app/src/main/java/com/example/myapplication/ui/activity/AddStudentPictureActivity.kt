package com.example.myapplication.ui.activity

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.classes.StudentItem
import com.example.myapplication.classes.config
import com.example.myapplication.remote.StudentService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection


class AddStudentPictureActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var file_name: TextView
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_student_picture)
        spinner = findViewById(R.id.spinner)
        getRequest()
        val button: Button = findViewById(R.id.add_photo)
        button.setOnClickListener {
            startChooseImageIntentForResult()
        }
        file_name = findViewById(R.id.file_name)
    }

    fun getRequest() {
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(config.BASE_URL)
            .build()
            .create(StudentService::class.java)

        val student = retrofit.getData()

        student.enqueue(object : Callback<List<StudentItem>?> {
            override fun onResponse(
                call: Call<List<StudentItem>?>,
                response: Response<List<StudentItem>?>
            ) {
                val responseBody = response.body()

                Log.d("Response: ", responseBody.toString())

                if (responseBody != null) {
                    var options: ArrayList<String> = ArrayList()
                    for (item in responseBody) {
                        options.add(item.first_name + " " + item.last_name)
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
                TODO("Not yet implemented")
            }
        })
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

            val name : String = spinner.selectedItem.toString()
        } else
        super.onActivityResult(requestCode, resultCode, data)
    }


    fun requestGET(url: String?): String? {
        val obj = URL(url)
        val con = obj.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        val responseCode = con.responseCode
        println("Response Code :: $responseCode")
        return if (responseCode == HttpURLConnection.HTTP_OK) {
            val `in` =
                BufferedReader(InputStreamReader(con.inputStream))
            var inputLine: String?
            val response = StringBuffer()
            while (`in`.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
                Log.d("resp", inputLine.toString())
            }
            `in`.close()
            response.toString()
        } else {
            ""
        }
    }

    fun requestPOST(r_url: String?, postDataParams: JSONObject): String? {
        val url = URL(r_url)
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        conn.readTimeout = 3000
        conn.connectTimeout = 3000
        conn.requestMethod = "POST"
        conn.doInput = true
        conn.doOutput = true
        val os: OutputStream = conn.outputStream
        val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
        writer.write(encodeParams(postDataParams))
        writer.flush()
        writer.close()
        os.close()
        val responseCode: Int = conn.responseCode // To Check for 200
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            val `in` = BufferedReader(InputStreamReader(conn.inputStream))
            val sb = StringBuffer("")
            var line: String? = ""
            while (`in`.readLine().also { line = it } != null) {
                sb.append(line)
                break
            }
            `in`.close()
            return sb.toString()
        }
        return null
    }

    private fun encodeParams(params: JSONObject): String? {
        val result = StringBuilder()
        var first = true
        val itr = params.keys()
        while (itr.hasNext()) {
            val key = itr.next()
            val value = params[key]
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }
        return result.toString()
    }
}