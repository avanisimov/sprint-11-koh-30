package ru.practicum.yandex.sprint11koh30

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SPRINT_11"
    }

    private val adapter = NewsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val uri: Uri = Uri.parse("https://myserver.com:5051/api/v1/path?text=android&take=1#last")

        Log.d(TAG, "uri.scheme ${uri.scheme}")
        Log.d(TAG, "uri.host ${uri.host}")
        Log.d(TAG, "uri.authority ${uri.authority}")
        Log.d(TAG, "uri.pathSegments ${uri.pathSegments}")
        Log.d(TAG, "uri.lastPathSegment ${uri.lastPathSegment}")
        Log.d(TAG, "uri.queryParameterNames ${uri.queryParameterNames}")
        Log.d(TAG, "uri.getQueryParameter(\"text\") ${uri.getQueryParameter("text")}")
        Log.d(TAG, "uri.fragment ${uri.fragment}")


        val itemsRv: RecyclerView = findViewById(R.id.items)
        itemsRv.adapter = adapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/avanisimov/sprint-11-koh-30/")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(Date::class.java, CustomDateTypeAdapter())
                        .registerTypeAdapter(NewsItem::class.java, NewsItemTypeAdapter())
                        .create()
                )
            )
            .build()
        val serverApi = retrofit.create(Sprint11ServerApi::class.java)


//        val response = serverApi.getNews1().execute()
        serverApi.getNews1().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                Log.i(TAG, "onResponse: ${response.body()}")
                adapter.items = response.body()?.data?.items ?: emptyList()
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
            }
        })
        val durationInMillis = (5 * 60 + 32) * 1000L
        Log.d(
            "DURATION",
            "durationInMillis -> ${
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(durationInMillis)
            }"
        )

    }


}

// https://raw.githubusercontent.com/avanisimov/sprint-11-koh-30/main/jsons/news_1.json

interface Sprint11ServerApi {


    @GET("main/jsons/news_1.json")
    fun getNews1(): Call<NewsResponse>
}
