package com.example.randomstringgenerator.repository

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.example.randomstringgenerator.R
import com.example.randomstringgenerator.model.RandomStringData
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class RandomStringRepository(private val contentResolver: ContentResolver) {
    private val CONTENT_URI = "content://com.iav.contestdataprovider/text"
    private val DATA_COLUMN = "data"

    @RequiresApi(Build.VERSION_CODES.O)
    fun getRandomString(noOfCharacters: Int, displayToast: (Int) -> Unit): RandomStringData? {
        val uri = Uri.parse(CONTENT_URI)
        val queryArgs = Bundle().apply {
            putInt(ContentResolver.QUERY_ARG_LIMIT, noOfCharacters)
        }
        val projection = arrayOf(DATA_COLUMN)
        var result: RandomStringData? = null

        try {
            val cursor = contentResolver.query(
                uri,
                projection,
                queryArgs,
                null
            )
            cursor?.use { c ->
                if (c.moveToFirst().not()) {
                    Handler(Looper.getMainLooper()).post {
                        displayToast(R.string.data_not_found)
                    }
                    return null
                }

                val jsonData = c.getString(0)
                val jsonObject = JSONObject(jsonData)
                val randomText = jsonObject.getJSONObject("randomText")

                val value = randomText.getString("value")
                val length = randomText.getInt("length")
                val createdStr = randomText.getString("created")
                val createdDate = formatDate(createdStr)
                result = RandomStringData(value, length, createdDate)
            } ?: run {
                Handler(Looper.getMainLooper()).post {
                    displayToast(R.string.something_went_wrong)
                }
            }
        } catch (e: SecurityException) {
            Handler(Looper.getMainLooper()).post {
                displayToast(R.string.permission_denied)
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                displayToast(R.string.something_went_wrong)
            }
        }
        return result
    }

    fun formatDate(dateStr: String): String {
        return try {
            val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormatter.timeZone = TimeZone.getTimeZone("Asia/Kolkata")

            val date = inputFormatter.parse(dateStr) ?: return ""

            val outputFormatter = SimpleDateFormat("dd MMMM, yyyy HH:mm", Locale.getDefault())
            outputFormatter.format(date)
        } catch (e: Exception) {
            ""
        }
    }


}