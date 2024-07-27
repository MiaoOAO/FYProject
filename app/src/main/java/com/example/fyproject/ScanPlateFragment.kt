package com.example.fyproject

import TFLiteModel
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import java.io.File
import java.io.FileOutputStream
import android.Manifest
import android.content.pm.PackageManager


class ScanPlateFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var imagePath: String
    private lateinit var tfliteModel: TFLiteModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scan_plate, container, false)
        tfliteModel = TFLiteModel(requireContext())

        val openBtn = view.findViewById<Button>(R.id.scanOpenBtn)
        // Initialize UI elements and set up camera button click listener

        openBtn.setOnClickListener{
            captureImage()
        }


        return view
    }

    private fun captureImage() {
        val cameraIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(cameraIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imagePath = saveImage(imageBitmap)
            recognizePlate(imageBitmap)
        }
    }

    private fun saveImage(bitmap: Bitmap): String {
        val file = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "plate.jpg")
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
        return file.absolutePath
    }

    private fun recognizePlate(bitmap: Bitmap) {
        val results = tfliteModel.recognizePlate(bitmap)
        displayResults(results)
    }

    private fun displayResults(results: List<String>) {
        // Update your UI with the recognized plate numbers
        results.forEach { plate ->
            println("Recognized Plate: $plate")
        }
    }
//
//    private fun displayResults(results: List<String>) {
//        // Assuming you have a TextView to display results
//        val resultTextView: TextView = view?.findViewById(R.id.resultTextView) ?: return
//        resultTextView.text = results.joinToString("\n")
//    }

}