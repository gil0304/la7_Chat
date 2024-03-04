package app.ochiai.gil.chat

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.ochiai.gil.chat.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTimePicker(binding.mondayTime, Calendar.MONDAY)
        setupTimePicker(binding.thursdayTime, Calendar.THURSDAY)
        setupTimePicker(binding.wednesdayTime, Calendar.WEDNESDAY)
        setupTimePicker(binding.thursdayTime, Calendar.THURSDAY)
        setupTimePicker(binding.fridayTime, Calendar.FRIDAY)
        setupTimePicker(binding.saturdayTime, Calendar.SATURDAY)
        setupTimePicker(binding.sundayTime, Calendar.SUNDAY)

        binding.saveButton.setOnClickListener {
            // 保存ロジックを実装
        }
    }

    private fun setupTimePicker(textView: TextView, dayOfWeek: Int) {
        textView.setOnClickListener {
            val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                textView.text = String.format("%02d:%02d", hourOfDay, minute)
            }, 12, 0, true)
            timePickerDialog.show()
        }
    }
}
