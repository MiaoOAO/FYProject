import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fyproject.R

class PdfAdapter(private val pdfList: List<String>, private val onItemClick: (String) -> Unit) : RecyclerView.Adapter<PdfAdapter.PdfViewHolder>() {

    inner class PdfViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pdfName: TextView = itemView.findViewById(R.id.pdfName) // Replace with your TextView ID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pdf_item_layout, parent, false)
        return PdfViewHolder(view)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        val pdfUrl = pdfList[position]
        // Extract PDF name from URL or provide a default name
        val pdfName = extractPdfNameFromUrl(pdfUrl) // Replace with your logic
        holder.pdfName.text = pdfName

        holder.itemView.setOnClickListener {
            onItemClick(pdfUrl)
        }
    }

    override fun getItemCount(): Int = pdfList.size

    private fun extractPdfNameFromUrl(pdfUrl: String): String {
        // Implement logic to extract PDF name from URL
        // For example, using regular expressions or substring manipulation
        return pdfUrl.substringAfterLast("/") // A basic example
    }
}
