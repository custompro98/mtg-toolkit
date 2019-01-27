package com.custompro98.mtgtoolkit

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.view.View
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

@SuppressLint("StaticFieldLeak")
class ParseImageTask(private var activity: MainActivity, imagePath: String) : AsyncTask<Void, Void, String>() {
    private val image: File
    private val parsingService: AmazonRekognitionService

    init {
        this.image = File(imagePath)
        this.parsingService = AmazonRekognitionService(image, activity.applicationContext)
    }

    override fun onPreExecute() {
        super.onPreExecute()

        activity.textView.visibility = View.INVISIBLE
        activity.progressBar.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg p0: Void?): String {
        return try {
            parsingService.parse()
        } catch (e: Exception) {
            e.printStackTrace()
            "Error!"
        } finally {
            image.delete()
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        activity.textView.text = result
        activity.progressBar.visibility = View.INVISIBLE
        activity.textView.visibility = View.VISIBLE
    }
}