package com.example.fyproject.admin

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.fyproject.R
import com.example.fyproject.databinding.FragmentAdminUploadAnnouncementBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AdminUploadAnnouncementFragment : Fragment() {


    private lateinit var firebaseStoreRef : FirebaseFirestore
    lateinit var imageUri: Uri
    private lateinit var firebaseImage:ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_upload_announcement, container, false)

        firebaseStoreRef = FirebaseFirestore.getInstance()

        val selectBtn: Button = view.findViewById(R.id.uploadImgBtn)
        val saveBtn:Button = view.findViewById(R.id.annSaveBtn)
        firebaseImage = view.findViewById(R.id.firebaseImage)

        selectBtn.setOnClickListener{
            selectImg()
        }

        saveBtn.setOnClickListener{
            saveImageToFirebase()
        }

        return view





    }

    private fun saveImageToFirebase() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_DD_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(imageUri).addOnSuccessListener {
            firebaseImage.setImageURI(null)
            Toast.makeText(requireContext(), "Uploaded successful", Toast.LENGTH_SHORT).show()
            if(progressDialog.isShowing) progressDialog.dismiss()

        }.addOnFailureListener{
            if(progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImg() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK){
            imageUri = data?.data!!
            firebaseImage.setImageURI(imageUri)
        }
    }

}