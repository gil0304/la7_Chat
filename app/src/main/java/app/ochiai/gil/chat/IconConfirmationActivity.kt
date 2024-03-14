package app.ochiai.gil.chat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import app.ochiai.gil.chat.databinding.ActivityIconConfirmationBinding
import com.squareup.picasso.Picasso
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class IconConfirmationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIconConfirmationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIconConfirmationBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }

        val imageUrl = intent.getStringExtra("image_url") ?: ""
        Picasso.get().load(imageUrl).into(binding.icon)

        binding.saveButton.setOnClickListener {
            val sharedPref = getSharedPreferences("Icon", Context.MODE_PRIVATE)
            sharedPref.edit().putString("ImageUrl", imageUrl).apply()
            navigateToChatScreen()
        }

        binding.noChangeRecreateButton.setOnClickListener {
            val sharedPref = getSharedPreferences("IconPrompt", Context.MODE_PRIVATE)
            val genderPrompt = sharedPref.getString("GenderPrompt", "特に指定なし")
            val agePrompt = sharedPref.getString("AgePrompt", "特に指定なし")
            val hairstylePrompt = sharedPref.getString("HairstylePrompt", "特に指定なし")
            val hairColorPrompt = sharedPref.getString("HairColorPrompt", "特に指定なし")
            val eyesPrompt = sharedPref.getString("EyesPrompt", "特に指定なし")
            val nosePrompt = sharedPref.getString("NosePrompt", "特に指定なし")
            val mouthPrompt = sharedPref.getString("MouthPrompt", "特に指定なし")
            val earPrompt = sharedPref.getString("EarPrompt", "特に指定なし")
            val stylePrompt = sharedPref.getString("StylePrompt", "特に指定なし")
            generateImage(
                "性別：${genderPrompt}\n" +
                        "年齢：${agePrompt}\n" +
                        "髪型：${hairstylePrompt}\n" +
                        "髪色：${hairColorPrompt}\n" +
                        "耳：${eyesPrompt}\n" +
                        "鼻：${nosePrompt}\n" +
                        "口：${mouthPrompt}\n" +
                        "耳：${earPrompt}\n" +
                        "雰囲気：${stylePrompt}\n" +
                        "以上の指定で、顔写真をアニメ風で作ってください"
            )
        }

        binding.changeRecreateButton.setOnClickListener {
            navigateToSettingScreen()
        }
    }

    private fun generateImage(prompt: String) {
        val logging = HttpLoggingInterceptor().apply{
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.MINUTES)
            .build()

        showLoading()

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = createJsonRequestBody(prompt).toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/images/generations")
            .post(body)
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .build()

        // OkHttpClientのインスタンス化とリクエストの実行
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                println("Error: ${e.message}")
                hideLoading()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody = it.body?.string()
                    val imageUrl = parseImageUrl(responseBody)
                    runOnUiThread {
                        hideLoading()
                        navigateToConfirmationScreen(imageUrl)
                    }
                }
            }
        })
    }

    fun createJsonRequestBody(prompt: String): String {
        val requestBody = mapOf(
            "prompt" to prompt,
            "model" to "dall-e-3",
            "quality" to "hd",
            "n" to 1,
            "size" to "1024x1024",
        )
        return Gson().toJson(requestBody)
    }

    fun parseImageUrl(jsonResponse: String?): String {
        // JSONレスポンスから画像URLを抽出
        if (jsonResponse.isNullOrEmpty()) return ""

        val jsonElement = JsonParser.parseString(jsonResponse)
        val jsonObject = jsonElement.asJsonObject

        val dataArray = jsonObject.getAsJsonArray("data")

        if (dataArray != null && dataArray.size() > 0) {
            val firstObject = dataArray.get(0).asJsonObject
            val url = firstObject.get("url").asString
            return url
        }

        return ""
    }

    fun showLoading() {
        runOnUiThread {
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    fun hideLoading() {
        runOnUiThread {
            binding.progressBar.visibility = View.GONE
        }
    }

    fun navigateToConfirmationScreen(imageUrl: String) {
        val intent = Intent(this, IconConfirmationActivity::class.java).apply {
            putExtra("image_url", imageUrl)
        }
        startActivity(intent)
    }

    private fun navigateToSettingScreen() {
        val intent = Intent(this, IconSettingActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToChatScreen() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }
}