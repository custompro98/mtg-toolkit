package com.custompro98.mtgtoolkit.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import com.custompro98.mtgtoolkit.R
import com.custompro98.mtgtoolkit.asyncTasks.ParseImageTask
import com.custompro98.mtgtoolkit.enums.ServiceName
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        inflateSpeedDial()
    }

    private val REQUEST_TAKE_PHOTO = 1
    private val REQUEST_IMAGE_CAPTURE = 1

    private var mCurrentPhotoPath: String = ""
    private var mParsingService: ServiceName = ServiceName.NONE


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ParseImageTask(this, mCurrentPhotoPath, mParsingService).execute()
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.custompro98.mtgtoolkit.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = this.absolutePath
        }
    }

    private fun inflateSpeedDial() {
        speedDial.inflate(R.menu.menu_speed_dial)
        speedDial.setOnActionSelectedListener {
            when (it.id) {
                R.id.fabMLKit -> {
                    mParsingService = ServiceName.MLKIT
                    dispatchTakePictureIntent()
                    false
                }
                R.id.fabRekognition -> {
                    mParsingService = ServiceName.REKOGNITION
                    dispatchTakePictureIntent()
                    false
                }
                else -> {
                    true
                }
            }
        }
    }
}
