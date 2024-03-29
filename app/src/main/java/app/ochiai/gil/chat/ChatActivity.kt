package app.ochiai.gil.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import app.ochiai.gil.chat.databinding.ActivityChatBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private val messages = mutableListOf<Message>()
    private val adapter = ChatAdapter(messages)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        intent.getStringExtra("autoMessage")?.let { autoMessage ->
            addMessage(Message(autoMessage, true))
        }

        intent.getStringExtra("imageUrl")?.let { imageUrl ->
            if(imageUrl.isNotEmpty()) {
                addMessage(Message(imageUrl, true, isImage = true))
            }
        }

        binding.sendButton.setOnClickListener {
            val messageText = binding.inputEditText.text.toString()
            sendMessage(messageText)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun sendMessage(text: String) {
        if (text.isNotEmpty()) {
            Log.d("ChatActivity", "Sending message: $text")
            addMessage(Message(text, false))
            binding.inputEditText.setText("") // Clear the input field
            callChatGPT(text) // Send the message text to ChatGPT API
        }
    }

    private fun callChatGPT(message: String) {
        val client = OkHttpClient()
        val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val jsonBody = """
            {
                "model": "gpt-4-turbo-preview",
                "messages": [{"role": "user", "content": "$message"},{"role": "user", "content": "友達口調で会話して、そして「友達口調で会話して」に対する返答はいらない"}]
            }
        """.trimIndent()
        val requestBody = jsonBody.toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(requestBody)
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .build()

        println(request)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ChatActivity", "API call failed", e)
                e.printStackTrace()
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        it.body?.string()?.let { responseBody ->
                            Log.d("ChatActivity", "API Response: $responseBody")
                            val recievedMessage = parseResponse(responseBody)
                            runOnUiThread {
                                addMessage(Message(recievedMessage, true))
                            }
                        }
                    }
                }
            }
        })
    }

    private fun parseResponse(responseBody: String): String {
        // レスポンスの JSON をパースする
        val jsonObject = JSONObject(responseBody)

        // "choices" 配列から最初のオブジェクトを取得し、その "text" フィールドの値を返す
        val choicesArray = jsonObject.getJSONArray("choices")
        if (choicesArray.length() > 0) {
            val firstChoice = choicesArray.getJSONObject(0)
            val messageObject = firstChoice.getJSONObject("message")
            return messageObject.getString("content")
        }

        return "No response"
    }

    private fun addMessage(message: Message) {
        messages.add(message)
        adapter.notifyItemInserted(messages.size - 1)
        binding.recyclerView.scrollToPosition(messages.size - 1)
    }
}