package com.example.fyproject

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fyproject.databinding.ActivityProfileFormBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileFormActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityProfileFormBinding
    private lateinit var fStore: FirebaseFirestore
    lateinit var imageUri: Uri
    private lateinit var profileImg: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        profileImg = binding.imageViewProfile

        binding.uploadProfileImgBtn.setOnClickListener{
            selectImg()
        }

        binding.profileFormBtn.setOnClickListener{
            val icNo = binding.icNoReg.text.toString()
            val fName = binding.nameReg.text.toString()
            val hAddress = binding.addressReg.text.toString()
            val plateNo = binding.plateNoReg.text.toString().uppercase()

            val userId = FirebaseAuth.getInstance().currentUser!!.uid


            val updateUserMap = hashMapOf(
                "icNo" to icNo,
                "name" to fName,
                "address" to hAddress,
                "plateNo" to plateNo,
                "userId" to userId
            )

            if (::imageUri.isInitialized){

                val progressDialog = ProgressDialog(this@ProfileFormActivity)
                progressDialog.setMessage("Submitting....")
                progressDialog.setCancelable(false)
                progressDialog.show()

                val storageReference = FirebaseStorage.getInstance().getReference("images/$userId")

                storageReference.putFile(imageUri).addOnSuccessListener {
                    profileImg.setImageURI(null)
                    updateUserMap["profileImg"] = userId
                    Toast.makeText(this@ProfileFormActivity, "Uploaded successful", Toast.LENGTH_SHORT).show()
                    if(progressDialog.isShowing) progressDialog.dismiss()

                }.addOnFailureListener{
                    if(progressDialog.isShowing) progressDialog.dismiss()
                    Toast.makeText(this@ProfileFormActivity, "Failed", Toast.LENGTH_SHORT).show()
                }


            }else{
                updateUserMap["profileImg"] = ""
            }


            if(icNo.isNotEmpty() && fName.isNotEmpty() && plateNo.isNotEmpty() && hAddress.isNotEmpty()){

//                val intent = Intent(this, UserMainPage::class.java)

                fStore.collection("user").document(userId)
                                .update(updateUserMap as Map<String, Any>)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this, "Profile update failed $userId", Toast.LENGTH_SHORT).show()
                                }

//                startActivity(intent)

            }else{
                Toast.makeText(this, "Empty Fields is not allowed", Toast.LENGTH_SHORT).show()
            }


            val ref = fStore.collection("user").document(userId)

            ref.get().addOnSuccessListener {
                if(it != null){
                    val getIc = it.data?.get("icNo").toString()
                    val getName = it.data?.get("name").toString()
                    val getPlateNo = it.data?.get("plateNo").toString()
                    val getAddress = it.data?.get("address").toString()
                    val getApprove = it.data?.get("approve").toString()

                    if(getIc.isNotEmpty() && getName.isNotEmpty() && getPlateNo.isNotEmpty() && getAddress.isNotEmpty() && getApprove == "1"){
//                    Toast.makeText(this, "Success to get Email:$getIc, name:$getName, phone:$getPlateNo", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, UserMainPage::class.java)
                        startActivity(intent)
                    }else if(getIc.isNotEmpty() && getName.isNotEmpty() && getPlateNo.isNotEmpty() && getAddress.isNotEmpty() && getApprove == "0"){
                        Toast.makeText(this, "Verifying account, if exceed 48 hours, please contact management", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }

                }
            }
                .addOnFailureListener{
                    Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
                }

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
            profileImg.setImageURI(imageUri)
        }
    }
}
