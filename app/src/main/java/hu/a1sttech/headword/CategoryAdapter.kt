package hu.a1sttech.headword
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categories: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    // ViewHolder osztály - itt kapcsoljuk össze az item_category.xml elemeivel
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryTextView: TextView = view.findViewById(R.id.categoryTextView)
    }

    // Itt hozzuk létre az egyes elemek nézetét
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    // Itt határozzuk meg, hogy mi történik egy adott elem megjelenítésekor
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryTextView.text = category
        holder.itemView.setOnClickListener { onClick(category) }
    }

    override fun getItemCount() = categories.size
}
