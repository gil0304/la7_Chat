package app.ochiai.gil.chat

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.ochiai.gil.chat.databinding.ActivityIconSettingBinding

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
        val editor = sharedPref.edit()

        saveIconPrompt(editor, "GenderPrompt", binding.genderText.text.toString())
        saveIconPrompt(editor, "AgePrompt", binding.ageText.text.toString())
        saveIconPrompt(editor, "HairstylePrompt", binding.hairstyleText.text.toString())
        saveIconPrompt(editor, "HairColorPrompt", binding.hairColorText.text.toString())
        saveIconPrompt(editor, "EyesPrompt", binding.eyesText.text.toString())
        saveIconPrompt(editor, "NosePrompt", binding.noseText.text.toString())
        saveIconPrompt(editor, "MouthPrompt", binding.mouthText.text.toString())
        saveIconPrompt(editor, "NosePrompt", binding.noseText.text.toString())
        saveIconPrompt(editor, "StylePrompt", binding.styleText.text.toString())

        editor.apply()
    }

    private fun saveIconPrompt(editor: SharedPreferences.Editor, key: String, prompt: String) {
        if (prompt.isEmpty()) {
            editor.remove(key)
        } else {
            editor.putString(key, prompt)
        }
    }
}