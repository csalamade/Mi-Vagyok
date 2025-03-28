package hu.a1sttech.headword

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlin.math.abs

class GameActivity : AppCompatActivity(), SensorEventListener {

    // Szenzorok kezeléséhez
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    //visszaszámláló változói

    private lateinit var countDownTimer: CountDownTimer
    private val totalTimeInMillis = 30000L // 30 másodperc
    private val countDownInterval = 1000L // 1 másodpercenként frissít

    //json ból olvasás változói
    private lateinit var words: List<String>  // A szavak listája
    private var currentWordIndex = 0  // Melyik szónál járunk

    // Billenés változói
    private var isTiltDetectionEnabled = true  // Érzékelés engedélyezve
    private val tiltCooldownMillis = 1500L  // Várakozási idő billenések között milliszekundumban
    private var isGameActive = false  // Kezdetben a játék inaktív
    private var isPhoneFacingAway = false  // Telefon kijelzője a másik játékos felé néz-e

    // UI elemek
    private lateinit var wordTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var nextButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        // A kijelzőt fektetett módba állítjuk
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContentView(R.layout.activity_game)

        // Képernyő ébren tartása játék közben
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Senzor inicializálása
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        ///UI elemek inicializálása
        wordTextView = findViewById(R.id.wordTextView)
        nextButton = findViewById(R.id.nextButton)
        timerTextView = findViewById(R.id.timerTextView)



        val category = intent.getStringExtra("category")  // Kategória neve
        val wordCategories = parseJson(this)  // JSON beolvasása
        words = wordCategories[category] ?: listOf("Nincs szó elérhető")  // Kategória szavai

        // Kezdeti szöveg beállítása - instrukció a játékosnak
        wordTextView.text = "Fordítsd a kijelzőt a másik játékos felé!"
        timerTextView.text = ""
        startCountDown()
        // Az első szó megjelenítése
        //wordTextView.text = words[currentWordIndex]



        // "Következő" gomb eseménykezelője
        nextButton.setOnClickListener {
            skipWord()

        }

    }
    override fun onResume() {
        super.onResume()
        accelerometer?.also { acc ->
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL)

        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                // Ha nem aktív a játék, ellenőrizzük a telefon orientációját
                if (!isGameActive) {
                    // Fekvő módban vagyunk (requestedOrientation miatt)
                    // Ha z értéke pozitív és jelentős, akkor a kijelző felfelé néz
                    // Ez a helyzet, amikor a telefon a játékos homlokán van kijelzővel kifelé
                    if (z > 7) {
                        isPhoneFacingAway = true
                        // Ha a telefon megfelelő helyzetben van, indítjuk a játékot
                        startGame()
                    }
                }
                // Ha aktív a játék és engedélyezve van a billenés érzékelése
                else if (isTiltDetectionEnabled) {
                    // Felfelé billenés (y pozitív)
                    if (y > 7) { // Felfelé billentés küszöbértéke
                        acceptWord()
                    }
                    // Lefelé billenés (y negatív)
                    else if (y < -7) { // Lefelé billentés küszöbértéke
                        skipWord()
                    }
                }
            }
        }
    }



    override fun onAccuracyChanged(p0: Sensor?, accuracy: Int) {
        // Nem kell semmit csinálni, de kell az implementáció!
    }

    private fun enableTiltDetection() {
        isTiltDetectionEnabled = true
    }

    private fun disableTiltDetection() {
        isTiltDetectionEnabled = false
    }

    private fun startGame() {
        // Játék indítása csak egyszer
        if (isGameActive) return

        isGameActive = true
        isTiltDetectionEnabled = true

        showWord()

        // Időzítő indítása
        startCountDown()

        // Játék indításának jelzése
        Toast.makeText(this, "Játék elindult!", Toast.LENGTH_SHORT).show()
    }


    private fun showWord() {
        disableTiltDetection()
        restartTimer()
        if (currentWordIndex < words.size) {
            wordTextView.text = words[currentWordIndex]
        } else {
            wordTextView.text = "Vége a játéknak!"
        }

        // Újra engedélyezzük a billenés érzékelést egy kis késleltetés után
        Handler(Looper.getMainLooper()).postDelayed({
            enableTiltDetection()
        }, tiltCooldownMillis)
    }

    private fun acceptWord() {
        currentWordIndex++
        changeBackgroundColor(Color.GREEN)
        showWord()
    }

    private fun skipWord() {
        currentWordIndex++
        changeBackgroundColor(Color.RED)
        showWord()


    }

    private fun startCountDown() {
        countDownTimer = object : CountDownTimer(totalTimeInMillis, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                // Minden másodpercben frissíti a felületet
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView.text =
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

    private fun changeBackgroundColor(color: Int) {
        val rootView = findViewById<View>(android.R.id.content)
        val originalColor = rootView.background // Eredeti szín mentése

        rootView.setBackgroundColor(color) // Háttér szín beállítása

        // 500ms múlva visszaállítjuk az eredeti színt
        Handler(Looper.getMainLooper()).postDelayed({
            rootView.background = originalColor
        }, 500)
    }
}