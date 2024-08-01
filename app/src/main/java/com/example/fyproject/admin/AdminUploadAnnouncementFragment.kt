package com.example.fyproject.admin

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fyproject.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminUploadAnnouncementFragment : Fragment() {


    private lateinit var firebaseStoreRef: FirebaseFirestore
    lateinit var fileUri: Uri // Change to Uri for any file type
    private lateinit var selectBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var pdfName: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_upload_announcement, container, false)

        firebaseStoreRef = FirebaseFirestore.getInstance()

        selectBtn = view.findViewById(R.id.uploadImgBtn)
        saveBtn = view.findViewById(R.id.annSaveBtn)
        pdfName = view.findViewById(R.id.pdfNameTv)

        selectBtn.setOnClickListener {
            selectPdf()
        }

        saveBtn.setOnClickListener {
            savePdfToFirebase()
        }

        return view
    }

    private fun savePdfToFirebase() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_DD_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now) + ".pdf" // Add extension for PDF
        val storageReference = FirebaseStorage.getInstance().getReference("pdfs/$fileName")

        storageReference.putFile(fileUri).addOnSuccessListener {
            Toast.makeText(requireContext(), "Uploaded successful", Toast.LENGTH_SHORT).show()
            if (progressDialog.isShowing) progressDialog.dismiss()

        }.addOnFailureListener {
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectPdf() {
        val intent = Intent()
        intent.type = "application/pdf" // Specify PDF MIME type
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            fileUri = data?.data!!
            val fileName = fileUri.lastPathSegment ?: "Unknown File"

            val normalizedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)

            pdfName.text = normalizedFileName
        }
    }
}
