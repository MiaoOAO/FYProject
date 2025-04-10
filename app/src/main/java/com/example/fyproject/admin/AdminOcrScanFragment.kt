package com.example.fyproject.admin

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.fyproject.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class AdminOcrScanFragment : Fragment() {



    private lateinit var previewView: PreviewView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var textRecognizer: TextRecognizer
    private var lastRecognizedText = ""
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_admin_ocr_scan, container, false)
        previewView = view.findViewById(R.id.previewView)
        firestore = FirebaseFirestore.getInstance()
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, PlateAnalyzer())
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    inner class PlateAnalyzer : ImageAnalysis.Analyzer {
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                textRecognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        val detectedText = visionText.textBlocks.joinToString(" ") { it.text }.replace(" ", "")
                        if (detectedText.isNotEmpty() && detectedText != lastRecognizedText) {
                            lastRecognizedText = detectedText
                            matchPlateWithFirestore(detectedText)
                        }
                    }
                    .addOnFailureListener {
                        Log.e("OCR", "Text recognition failed", it)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
    }

    private fun matchPlateWithFirestore(plateNumber: String) {

        firestore.collection("visitor").whereEqualTo("plateNo", plateNumber)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val doc = querySnapshot.documents[0]
                    val owner = doc.getString("name") ?: "Unknown"
                    showMatchedCard(plateNumber, owner, "Match Found")
                } else {
                    showMatchedCard(plateNumber, "-", "No Match")
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error checking plate", it)
            }
    }

    private fun showMatchedCard(plate: String, owner: String, status: String) {
        requireActivity().runOnUiThread {
            view?.findViewById<TextView>(R.id.tvPlateNumber)?.text = "Plate: $plate"
            view?.findViewById<TextView>(R.id.tvOwnerName)?.text = "Owner: $owner"
            view?.findViewById<TextView>(R.id.tvStatus)?.text = "Status: $status"
            view?.findViewById<CardView>(R.id.cardMatchedInfo)?.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}