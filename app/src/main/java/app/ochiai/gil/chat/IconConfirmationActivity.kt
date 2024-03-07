package app.ochiai.gil.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.ochiai.gil.chat.databinding.ActivityIconConfirmationBinding
import com.squareup.picasso.Picasso

class IconConfirmationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIconConfirmationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIconConfirmationBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }

        val imageUrl = intent.getStringExtra("image_url") ?: ""
        Picasso.get().load(imageUrl).into(binding.icon)
    }
}