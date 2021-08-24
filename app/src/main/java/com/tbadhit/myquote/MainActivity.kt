package com.tbadhit.myquote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.tbadhit.myquote.databinding.ActivityMainBinding
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

// API from : https://quote-api.dicoding.dev
// API use :
// https://quote-api.dicoding.dev/random (Random Quotes)
// https://quote-api.dicoding.dev/list (List Quotes)

// Codelab (get json object) :
// Add library "AsyncHttpClient (LoopJ)" (build.gradle module)
// update "activit_main.xml"
// add code "MainActivity" (1) add getRandomQuote()
// add permission "AndroidManifest" (1) ""INTERNET

// Codelab (get json array) :
// Create new activity "ListQuotesActivity"
// update "activit_list_quotes.xm" "add listview & progress bar"
// add code "ListQuotesActivity" (1) "add getListQuotes()"
// add code "MainAcitvity" (2)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // (1)
        getRandomQuote()

        // (2)
        binding.btnAllQuotes.setOnClickListener {
            startActivity(Intent(this@MainActivity, ListQuotesActivity::class.java))
        }
    }

    // (1)
    private fun getRandomQuote() {
        binding.progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://quote-api.dicoding.dev/random"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray
            ) {
                // Jika berhasil
                binding.progressBar.visibility = View.INVISIBLE

                val result = String(responseBody)
                Log.d(TAG, result)

                try {
                    val responseObject = JSONObject(result)

                    val quote = responseObject.getString("en")
                    val author = responseObject.getString("author")

                    binding.tvQuote.text = quote
                    binding.tvAuthor.text = author
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                // Jika gagal
                binding.progressBar.visibility = View.VISIBLE

                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }

                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }

    // (1)
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}