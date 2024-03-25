package app.ochiai.gil.chat

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class SendMessageWorker (appContext: Context, workerParams: WorkerParameters):
        Worker(appContext, workerParams){
    override fun doWork(): Result {
        val sharedPref = applicationContext.getSharedPreferences("Icon", Context.MODE_PRIVATE)
        val imageUrl = sharedPref.getString("ImageUrl", "")

        val intent = Intent(applicationContext, ChatActivity::class.java).apply {
            putExtra("autoMessage", "おはよー") // テキストメッセージ
            putExtra("imageUrl", imageUrl) // 画像 URL
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        applicationContext.startActivity(intent)
        Log.d("SendMessageWorker", "チャットメッセージを送信")
        return Result.success()
    }
}