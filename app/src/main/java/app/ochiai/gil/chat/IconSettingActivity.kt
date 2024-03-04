package app.ochiai.gil.chat

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.ochiai.gil.chat.databinding.ActivityIconSettingBinding
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import app.ochiai.gil.chat.BuildConfig

class IconSettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIconSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIconSettingBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }

        binding.createButton.setOnClickListener {
            saveIconPromptPreferences()
        }
    }

    private fun saveIconPromptPreferences() {
        val sharedPref = getSharedPreferences("IconPrompt", Context.MODE_PRIVATE)

        with(sharedPref.edit()) {
            putString("GenderPrompt", binding.genderText.text.toString())
            putString("AgePrompt", binding.ageText.text.toString())
            putString("HairstylePrompt", binding.hairstyleText.text.toString())
            putString("HairColorPrompt", binding.hairColorText.text.toString())
            putString("EyesPrompt", binding.eyesText.text.toString())
            putString("NosePrompt", binding.noseText.text.toString())
            putString("MouthPrompt", binding.mouthText.text.toString())
            putString("NosePrompt", binding.noseText.text.toString())
            putString("StylePrompt", binding.styleText.text.toString())

            apply()
        }
    }

    fun generateImage(prompt: String) {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = createJsonRequestBody(prompt).toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/images/generations")
            .post(body)
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .build()

        // OkHttpClientのインスタンス化とリクエストの実行
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // エラーハンドリング
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody = it.body?.string()
                    val imageUrl = parseImageUrl(responseBody)
                    // TODO: UIスレッドでImageViewに画像を表示
                }
            }
        })
    }

    fun createJsonRequestBody(prompt: String): String {
        return Gson().toJson(mapOf("prompt" to prompt))
    }

    fun parseImageUrl(jsonResponse: String?): String {
        // JSONレスポンスから画像URLを抽出
        return ""
    }
}