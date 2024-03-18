package app.ochiai.gil.chat

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class SendMessageWorker (appContext: Context, workerParams: WorkerParameters):
        Worker(appContext, workerParams){
    override fun doWork(): Result {
        sendChantMessage("Hello! This is a test message from WorkManager.")
        return Result.success()
    }

    private fun sendChantMessage(message: String) {
        val intent = Intent(applicationContext, ChatActivity::class.java).apply {
            putExtra("message", message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        applicationContext.startActivity(intent)
        Log.d("SendMessageWorker", "チャットメッセージを送信")
    }
}