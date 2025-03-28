package hu.a1sttech.headword

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    // UI elemek
    private lateinit var resultTextView: TextView
    private lateinit var restartButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)

        val correctWords = intent.getIntExtra("correctWords", 0)
        val skippedWords = intent.getIntExtra("skippedWords", 0)

        //UI elemek inicializálása
        resultTextView = findViewById(R.id.resultTextView)
        restartButton = findViewById(R.id.restartButton)

        resultTextView.text = "Eltalált szavak: $correctWords\nKihagyott szavak: $skippedWords"

        restartButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}