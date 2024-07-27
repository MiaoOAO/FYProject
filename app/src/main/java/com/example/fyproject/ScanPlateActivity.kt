package com.example.fyproject

import TFLiteModel
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

class ScanPlateActivity : AppCompatActivity() {

    private lateinit var result:TextView
    private lateinit var confidence:TextView
    private lateinit var imageView:ImageView
    private lateinit var picture: Button
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var imagePath: String
    private lateinit var tfliteModel: TFLiteModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scan_plate)

        result = findViewById(R.id.result)
        confidence = findViewById(R.id.confidence)
        imageView = findViewById(R.id.imageViewScanPlate)
        picture = findViewById(R.id.buttonScanPlate)

        tfliteModel = TFLiteModel(this)


        picture.setOnClickListener{
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }else{
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }
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
        val file = File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "plate.jpg")
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
            result.text = plate
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null)
//        {
//            val image: Bitmap? = data.extras?.get("data") as? Bitmap
//            image?.let {
//                val dimension = Math.min(it.width, it.height)
//                val thumbnail = ThumbnailUtils.extractThumbnail(it, dimension, dimension)
//                imageView.setImageBitmap(thumbnail)
//
//                val scaledImage = Bitmap.createScaledBitmap(thumbnail, imageView, imageView, false)
//                classifyImage(scaledImage);
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }
}