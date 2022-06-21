package com.example.myapplication.ui.activity

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import java.io.File
import java.io.FileOutputStream


class AddStudentPictureActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var file_name: TextView
    private var imageUri: Uri? = null
    private var id = 0
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), 1)

        setContentView(R.layout.activity_add_student_picture)

        getRequest()

        spinner = findViewById(R.id.spinner)

        val button: Button = findViewById(R.id.add_photo)
        button.setOnClickListener {
            startChooseImageIntentForResult()
        }

        file_name = findViewById(R.id.file_name)

        val button_add: Button = findViewById(R.id.button_startAttendance)
        button_add.setOnClickListener {
            postRequest()
        }

    }

    fun getRequest() {
        Log.d("hi: ", "ning agi ko diri")
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(config.BASE_URL)
            .build()
            .create(ApiService::class.java)

        val student = retrofit.getStudents()

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

//        saveBitmap(applicationContext, "file_name")
//        uriToFile(this).let { file ->
//            compressImage(file.absolutePath, .5)
//        }

        Log.d("Imageuri: ", imageUri!!.path.toString())
    }

    private fun createImageFile(fileName:String = "temp_image") : File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    private fun uriToFile(context: Context): File {
//        var fileName = imageUri!!.lastPathSegment.toString()
//            .substring(imageUri!!.lastPathSegment.toString().lastIndexOf("/") + 1)
        var fileName = "reyreyrey"
        Log.d("imageUri: ", imageUri.toString())
        context.contentResolver.openInputStream(imageUri!!).let { inputStream ->
            val tempFile: File = createImageFile(fileName)
            val fileOutputStream = FileOutputStream(tempFile)

            inputStream?.copyTo(fileOutputStream)
            inputStream?.close()
            fileOutputStream.close()

            return tempFile;
        }
    }

    fun saveBitmap(context: Context, image: Bitmap, name: String) {
        val fileOutputStream = FileOutputStream(File( context.filesDir.absolutePath + "/$name.png"))
        image.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
    }

    private fun compressImage(filePath: String, targetMB: Double = 1.0) {
        var image : Bitmap = BitmapFactory.decodeFile(filePath)
        val exif = ExifInterface(filePath)
        val exifOrientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val exifDegree: Int = exifOrientationToDegree(exifOrientation)

        image = rotateImage(image, exifDegree.toFloat())

        try {
            val file = File(filePath)
            val length = file.length()
            val fileSizeInKB = (length / 1024).toString().toDouble()
            val fileSizeInMB = (fileSizeInKB / 1024).toString().toDouble()
            var quality = 100

            if(fileSizeInMB > targetMB) {
                quality = ((targetMB / fileSizeInMB) * 100).toInt()
            }

            val fileOutputStream = FileOutputStream(filePath)
            image.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun exifOrientationToDegree(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                90
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                180
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                270
            }

            else -> 0
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float):Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)

        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
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
}