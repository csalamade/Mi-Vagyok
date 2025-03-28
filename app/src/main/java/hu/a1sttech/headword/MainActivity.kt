package hu.a1sttech.headword

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.IOException

// JSON beolvasása az assets mappából
fun loadJsonFromAssets(context: Context, fileName: String): String {
    return try {
        context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (e: IOException) {
        e.printStackTrace()
        "{}" // Hiba esetén üres JSON-t ad vissza
    }
}

// JSON feldolgozása és kategóriák listába mentése
fun parseJson(context: Context): Map<String, List<String>> {
    val jsonString = loadJsonFromAssets(context, "words.json")
    val jsonObject = JSONObject(jsonString)  // JSON objektummá alakítás

    val categories = mutableMapOf<String, List<String>>()  // Üres map létrehozása

    // Végigmegyünk a JSON kulcsain (kategóriákon)
    for (key in jsonObject.keys()) {
        val wordsArray = jsonObject.getJSONArray(key)  // Lekérjük a kulcsokhoz tartozó tömböt
        val wordsList = mutableListOf<String>()

        for (i in 0 until wordsArray.length()) {
            wordsList.add(wordsArray.getString(i))  // Átalakítjuk listává
        }

        categories[key] = wordsList
    }

    return categories
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wordCategories = parseJson(this)  // JSON feldolgozása
        val categories = wordCategories.keys.toList()  // Csak a kategóriák listája

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CategoryAdapter(categories) { selectedCategory ->
            //Log.d("MainActivity", "Kategória kiválasztva: $selectedCategory")
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("category", selectedCategory)
            startActivity(intent)
        }
    }
}
