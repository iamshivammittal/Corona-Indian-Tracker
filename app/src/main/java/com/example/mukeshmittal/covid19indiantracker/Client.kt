package com.example.mukeshmittal.covid19indiantracker

import okhttp3.OkHttpClient
import okhttp3.Request

object Client {
    private val okhttpClient= OkHttpClient()
    private val request= Request.Builder()
        .url("https://api.covid19india.org/data.json")
        .build()
    val api= okhttpClient.newCall(request)
}
