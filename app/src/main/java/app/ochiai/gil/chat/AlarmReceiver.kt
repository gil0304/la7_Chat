package app.ochiai.gil.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val chatIntent = Intent(context, ChatActivity::class.java)
        chatIntent.putExtra("autoMessage", "おはよー")
        chatIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(chatIntent)
    }
}