package com.example.mukeshmittal.covid19indiantracker

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var stateListAdapter: StateListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list.addHeaderView(LayoutInflater.from(this).inflate(R.layout.item_header,list,false))
        fetchResults()
    }

    private fun fetchResults() {
        GlobalScope.launch {
            val response = withContext(Dispatchers.IO) { Client.api.clone().execute() }
            if (response.isSuccessful) {
                val data = Gson().fromJson(response.body?.string(), Response::class.java)
                launch(Dispatchers.Main) {
                    bindCombinedData(data.statewise[0])
                    bindStatewiseData(data.statewise.subList(1,data.statewise.size))
                }
            }
        }
    }

    private fun bindStatewiseData(subList: List<StatewiseItem>) {
        stateListAdapter = StateListAdapter(subList)
        list.adapter = stateListAdapter
    }

    private fun bindCombinedData(data: StatewiseItem) {
      val lastUpdatedTime= data.lastupdatedtime
        val simpleDateFormat= SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        lastUpdatedTv.text="Last Updated\n ${getTimeAgo(
            simpleDateFormat.parse(lastUpdatedTime)
        )}"
       confirmedTv.text=data.confirmed
        recoveredTv.text=data.recovered
        activeTv.text=data.active
        deceasedTv.text=data.deaths
    }

    fun getTimeAgo(past: Date): String {
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)

        return when {
            seconds < 60 -> {
                "Few seconds ago"
            }
            minutes < 60 -> {
                "$minutes minutes ago"
            }
            hours < 24 -> {
                "$hours hour ${minutes % 60} min ago"
            }
            else -> {
                SimpleDateFormat("dd/MM/yy, hh:mm a").format(past).toString()
            }
        }
    }
}
