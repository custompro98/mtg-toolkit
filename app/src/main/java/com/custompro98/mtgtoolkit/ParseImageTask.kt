package com.custompro98.mtgtoolkit

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.view.View
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

@SuppressLint("StaticFieldLeak")
class ParseImageTask(private var activity: MainActivity, imagePath: String) : AsyncTask<Void, Void, Unit>() {
    private val image: File
    private val parsingService: ParsingService

    init {
        this.image = File(imagePath)
//        this.parsingService = AmazonRekognitionService(image, activity.applicationContext)
        this.parsingService = MLKitService(image, activity.applicationContext)
    }

    override fun onPreExecute() {
        super.onPreExecute()

        activity.textView.visibility = View.INVISIBLE
        activity.progressBar.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg p0: Void?) {
        try {
            parsingService.parse(object : ParsingCallback {
                override fun onParsed(cardName: String) {
                    activity.runOnUiThread {
                        activity.textView.text = cardName
                        activity.progressBar.visibility = View.INVISIBLE
                        activity.textView.visibility = View.VISIBLE
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            image.delete()
        }
    }
}