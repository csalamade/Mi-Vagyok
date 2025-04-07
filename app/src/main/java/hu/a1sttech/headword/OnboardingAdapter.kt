package hu.a1sttech.headword
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    // ViewHolder - tartalmazza az oldalon lévő elemek hivatkozásait
    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Elemek azonosítása
        val imageView = view.findViewById<ImageView>(R.id.imageOnboarding)
        val titleView = view.findViewById<TextView>(R.id.textTitle)
        val descriptionView = view.findViewById<TextView>(R.id.textDescription)
    }

    // Létrehozza a ViewHolder-t és a hozzá tartozó nézetet
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }

    // Beállítja az adatokat a nézetben
    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val currentItem = items[position]
        holder.imageView.setImageResource(currentItem.image)
        holder.titleView.text = currentItem.title
        holder.descriptionView.text = currentItem.description
    }

    // Visszaadja az elemek számát
    override fun getItemCount() = items.size
}
