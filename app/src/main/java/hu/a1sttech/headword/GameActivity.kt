package hu.a1sttech.headword

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.pm.ActivityInfo
import android.os.CountDownTimer
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class GameActivity : AppCompatActivity() {

    //visszaszámláló változói

    private lateinit var countDownTimer: CountDownTimer
    private val totalTimeInMillis = 30000L // 30 másodperc
    private val countDownInterval = 1000L // 1 másodpercenként frissít

    //json ból olvasás változói
    private lateinit var words: List<String>  // A szavak listája
    private var currentWordIndex = 0  // Melyik szónál járunk


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // A kijelzőt fektetett módba állítjuk
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContentView(R.layout.activity_game)

        // Képernyő ébren tartása játék közben
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        startCountDown()

        val category = intent.getStringExtra("category")  // Kategória neve
        val wordCategories = parseJson(this)  // JSON beolvasása
        words = wordCategories[category] ?: listOf("Nincs szó elérhető")  // Kategória szavai

        val wordTextView: TextView = findViewById(R.id.wordTextView)
        val nextButton: Button = findViewById(R.id.nextButton)

        // Az első szó megjelenítése
        wordTextView.text = words[currentWordIndex]

        // "Következő" gomb eseménykezelője
        nextButton.setOnClickListener {
            currentWordIndex = (currentWordIndex + 1) % words.size  // Körforgás
            wordTextView.text = words[currentWordIndex]
            restartTimer()
        }

    }

    private fun startCountDown() {
        countDownTimer = object : CountDownTimer(totalTimeInMillis, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                // Minden másodpercben frissíti a felületet
                val secondsRemaining = millisUntilFinished / 1000
                findViewById<TextView>(R.id.timerTextView).text =
                    "Hátralévő idő: $secondsRemaining másodperc"
            }

            override fun onFinish() {
                // Amikor lejár az idő
                findViewById<TextView>(R.id.timerTextView).text = "Lejárt az idő!"
            }
        }.start()
    }

    private fun restartTimer() {
        // Meglévő timer leállítása
        countDownTimer?.cancel()

        // Új timer indítása
        startCountDown()
    }


    //timer leállítása a memória szívárgás elkerülése érdekében
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}