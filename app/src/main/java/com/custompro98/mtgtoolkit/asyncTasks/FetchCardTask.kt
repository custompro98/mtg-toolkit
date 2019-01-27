package com.custompro98.mtgtoolkit.asyncTasks

import android.os.AsyncTask
import com.custompro98.mtgtoolkit.activities.MainActivity
import com.squareup.picasso.Picasso
import io.magicthegathering.kotlinsdk.api.MtgCardApiClient
import io.magicthegathering.kotlinsdk.model.card.MtgCard
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Response

class FetchCardTask(private var activity: MainActivity, var cardName: String) : AsyncTask<Void, Void, String>() {
    override fun doInBackground(vararg p0: Void?): String {
        val exactName: String = cardName
        val pageSize = 1
        val page = 1

        var returnValue = "https://en.wikipedia.org/wiki/Rickrolling#/media/File:RickRoll.png"

        val cardsResponse: Response<List<MtgCard>> = MtgCardApiClient.getCardsByExactName(exactName, pageSize, page)
        val cards = cardsResponse.body()

        if (cards != null && cards.size > 0) {
            returnValue = cards.first().imageUrl ?: returnValue
        }

        return returnValue
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        Picasso.get().load(result).into(activity.imageView)
    }
}