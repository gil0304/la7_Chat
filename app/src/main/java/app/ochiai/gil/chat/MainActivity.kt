package app.ochiai.gil.chat

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import app.ochiai.gil.chat.databinding.ActivityMainBinding
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTimePicker(binding.mondayTime, Calendar.MONDAY)
        setupTimePicker(binding.tuesdayTime, Calendar.TUESDAY)
        setupTimePicker(binding.wednesdayTime, Calendar.WEDNESDAY)
        setupTimePicker(binding.thursdayTime, Calendar.THURSDAY)
        setupTimePicker(binding.fridayTime, Calendar.FRIDAY)
        setupTimePicker(binding.saturdayTime, Calendar.SATURDAY)
        setupTimePicker(binding.sundayTime, Calendar.SUNDAY)

        binding.saveButton.setOnClickListener {
            saveTimePreferences()
            setAlarmsForWeekdays()
            navigateToIconSettingActivity()
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

    private fun saveTimePreferences() {
        val sharedPref = getSharedPreferences("WeekdayTimes", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // 各曜日の時間を保存
        saveTimeForDay(editor, "MondayTime", binding.mondayTime.text.toString())
        saveTimeForDay(editor, "TuesdayTime", binding.tuesdayTime.text.toString())
        saveTimeForDay(editor, "WednesdayTime", binding.wednesdayTime.text.toString())
        saveTimeForDay(editor, "ThursdayTime", binding.thursdayTime.text.toString())
        saveTimeForDay(editor, "FridayTime", binding.fridayTime.text.toString())
        saveTimeForDay(editor, "SaturdayTime", binding.saturdayTime.text.toString())
        saveTimeForDay(editor, "SundayTime", binding.sundayTime.text.toString())

        editor.apply()
    }

    private fun saveTimeForDay(editor: SharedPreferences.Editor, key: String, time: String) {
        if (time != "--:--") { // "--:--"の時は保存しない
            editor.putString(key, time)
        } else {
            editor.remove(key) // 既存の時間をクリア
        }
    }

    private fun navigateToIconSettingActivity() {
        val intent = Intent(this, IconSettingActivity::class.java)
        startActivity(intent)
    }

    private fun setAlarmsForWeekdays() {
        val sharedPref = getSharedPreferences("WeekdayTimes", Context.MODE_PRIVATE)
        setWorkRequestForChat(Calendar.MONDAY, sharedPref.getString("MondayTime", "--:--") ?: "--:--")
        setWorkRequestForChat(Calendar.TUESDAY, sharedPref.getString("TuesdayTime", "--:--") ?: "--:--")
        setWorkRequestForChat(Calendar.WEDNESDAY, sharedPref.getString("WednesdayTime", "--:--") ?: "--:--")
        setWorkRequestForChat(Calendar.THURSDAY, sharedPref.getString("ThursdayTime", "--:--") ?: "--:--")
        setWorkRequestForChat(Calendar.FRIDAY, sharedPref.getString("FridayTime", "--:--") ?: "--:--")
        setWorkRequestForChat(Calendar.SATURDAY, sharedPref.getString("SaturdayTime", "--:--") ?: "--:--")
        setWorkRequestForChat(Calendar.SUNDAY, sharedPref.getString("SundayTime", "--:--") ?: "--:--")
    }

    private fun setWorkRequestForChat(dayOfWeek: Int, time: String) {
        if (time == "--:--") return // 時間が設定されていない場合は何もしない

        val hour = time.split(":")[0].toInt()
        val minute = time.split(":")[1].toInt()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            // 現在時刻より前の場合は、次の週に設定
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 7)
            }
        }

        // 現在時刻から指定時刻までの遅延時間を計算
        val delay = calendar.timeInMillis - System.currentTimeMillis()

        // WorkRequest を作成
        val workRequest = OneTimeWorkRequestBuilder<SendMessageWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        // WorkManager に WorkRequest をスケジュール
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }
}
