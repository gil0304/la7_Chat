package app.ochiai.gil.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.ochiai.gil.chat.databinding.ItemMessageReceivedBinding
import app.ochiai.gil.chat.databinding.ItemMessageSentBinding

class ChatAdapter(private val messages: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val viewTypeMessageSent = 1
    private val viewTypeMessageReceived = 2

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isReceived) {
            viewTypeMessageReceived
        } else {
            viewTypeMessageSent
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == viewTypeMessageReceived) {
            val binding = ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedMessageViewHolder(binding)
        } else {
            val binding = ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is ReceivedMessageViewHolder) {
            holder.binding.textView.text = message.text
        } else if (holder is SentMessageViewHolder) {
            holder.binding.textView.text = message.text
        }
    }

    override fun getItemCount(): Int = messages.size

    class ReceivedMessageViewHolder(val binding: ItemMessageReceivedBinding) : RecyclerView.ViewHolder(binding.root)

    class SentMessageViewHolder(val binding: ItemMessageSentBinding) : RecyclerView.ViewHolder(binding.root)
}