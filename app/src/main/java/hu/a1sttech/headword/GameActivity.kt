package hu.a1sttech.headword

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.pm.ActivityInfo
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class GameActivity : AppCompatActivity() {

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
        }

    }
}