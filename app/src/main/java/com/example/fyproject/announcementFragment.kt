
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fyproject.adapter.PdfAdapter
import com.example.fyproject.databinding.FragmentAnnouncementBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference

class announcementFragment : Fragment() {
    private lateinit var binding: FragmentAnnouncementBinding
    private lateinit var storageRef: StorageReference
    private lateinit var pdfList: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnnouncementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        storageRef = FirebaseStorage.getInstance().reference.child("pdfs")
        // Replace with your storage path
        pdfList = mutableListOf()

        loadPdfList()

        val pdfAdapter = PdfAdapter(pdfList) { pdfUrl ->
            downloadPdf(pdfUrl)
        }
        binding.pdfRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.pdfRecyclerView.adapter = pdfAdapter
    }

    private fun loadPdfList() {
        storageRef.listAll()
            .addOnSuccessListener { listResult: ListResult ->
                for (item in listResult.items) {
                    item.downloadUrl.addOnSuccessListener { uri ->
                        pdfList.add(uri.toString())
                        binding.pdfRecyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun downloadPdf(pdfUrl: String) {
        val storageRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent)
        }.addOnFailureListener {
            // Handle download error
        }
    }
}