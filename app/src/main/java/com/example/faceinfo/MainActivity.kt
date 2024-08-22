package com.example.faceinfo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory.Options
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    var buttoncamera=findViewById<Button>(R.id.btnCamera)
     buttoncamera.setOnClickListener {
         val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
         if(intent.resolveActivity(packageManager) != null)
         {
            startActivityForResult(intent, 123)
         }
         else
         {
             Toast.makeText(this,"Oops something went wrong" , Toast.LENGTH_SHORT).show()
         }

     }




}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123 && resultCode== RESULT_OK)
        {
            val extras= data?.extras
            val bitmap=extras?.get("data") as? Bitmap
            if (bitmap != null) {
                detectFace(bitmap)
            }
        }
    }
     fun detectFace(bitmap :Bitmap)
     {
         val Options = FaceDetectorOptions.Builder()
             .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
             .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
             .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
             .build()
         val detector = FaceDetection.getClient(Options)
         val image = InputImage.fromBitmap(bitmap, 0)
         val result=detector.process(image).addOnSuccessListener {
             faces->
             var resultText=""
             var i=1
             val resultTextView: TextView = findViewById(R.id.resulttextView)
             for(face in faces)
             {
                 resultText="Face Number : $i"+
                         "\nSmile : ${face.smilingProbability?.times(100)}%"+
                         "\nLeft Eye Open : ${face.leftEyeOpenProbability?.times(100)}%"+
                         "\nRight Eye Open :${face.rightEyeOpenProbability?.times(100)}%"

                 i++
             }
             resultTextView.text=resultText

             if(faces.isEmpty())
             {
                 Toast.makeText(this,"NO FACE DETECTED" , Toast.LENGTH_SHORT).show()
             }
             else
             {
                 Toast.makeText(this,resultText, Toast.LENGTH_LONG).show()
             }
         }
             .addOnFailureListener { e->
                 Toast.makeText(this,"Something went wrong" , Toast.LENGTH_SHORT).show()
             }
     }



}