package hu.a1sttech.headword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // ViewPager beállítása
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        // Képek hozzáadása (adja hozzá ezeket a képeket a drawable mappába)
        val onboardingItems = listOf(
            OnboardingItem(
                "Többféle témakör, egy csomó nevetés!",
                "Mielőtt játszani kezdenél, válassz egy kategóriát!",
                R.drawable.kategoriak
            ),
            OnboardingItem(
                "Talált? Billents balra új szóért!\n" +
                        "Nem megy? Billents jobbra a passzoláshoz!",
                "A játék mozdulatra reagál – így pörög a játék, nincs szükség gombokra!\n" +
                        "Gondolkodj gyorsan, és minél több szót találj ki 30 másodperc alatt!",
                R.drawable.ujszo_pasz
            ),
            OnboardingItem(
                "Nézd meg az eredményt!",
                "A kör végén megtudhatod, hány szót találtál el",
                R.drawable.kupa // Cseréld le a saját képed nevére
            )
        )

        // Adapter beállítása
        val adapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = adapter

        // Pontjelző beállítása
        val dotsIndicator = findViewById<DotsIndicator>(R.id.dotsIndicator)
        dotsIndicator.attachTo(viewPager)

        // Befejezés gomb
        val finishButton = findViewById<Button>(R.id.buttonFinish)
        finishButton.setOnClickListener {
            // Jelezd, hogy a súgó már megtörtént
            val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("onboarding_completed", true).apply()

            // Navigálj a fő képernyőre
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Bezárja a súgó aktivitást
        }
    }
}
