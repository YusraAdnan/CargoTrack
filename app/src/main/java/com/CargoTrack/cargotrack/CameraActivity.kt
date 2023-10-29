package com.CargoTrack.cargotrack

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.cargotrack.cargotrack.R

class CameraActivity : AppCompatActivity() {

    var bitmap : Bitmap?=null
    private var imageView: ImageView? = null
    lateinit var retreivedText : TextView
    private lateinit var convertToPdf: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        imageView = findViewById(R.id.imageview)
        retreivedText = findViewById(R.id.textViewExtractText)
        convertToPdf = findViewById(R.id.BtnCnvertPdf)

        val filepath = intent.getStringExtra("CapturedImagePath")
        val receivedText = intent.getStringExtra("ExtractedText")
        retreivedText.text=receivedText

        bitmap = BitmapFactory.decodeFile(filepath)//converts filepath back to bitmap
        imageView?.setImageBitmap(bitmap)

        val intent = Intent(this, PDFActivity::class.java)

        convertToPdf.setOnClickListener {

            intent.putExtra(
                "FilePath",
                filepath
            )
            intent.putExtra(
                "ExtractedText",
                retreivedText.text.toString()
            )//getting file path of taken picture from ImageCapture sending it to PDFActivity
            startActivity(intent)
        }

    }
}