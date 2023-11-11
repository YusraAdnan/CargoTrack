package com.CargoTrack.cargotrack


import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cargotrack.cargotrack.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.net.URI

class PDFActivity : AppCompatActivity() {
    private var imageView: ImageView? = null
    private var isActivityDestroyed = false
    lateinit var generatePDFBtn: FloatingActionButton
    lateinit var SendEmailButton: Button
    lateinit var retreivedText : TextView
    var pageHeight = 1120
    var pageWidth = 792
    var pictureHeight = 600
    var pictureWidth = 600
    var bitmap :Bitmap?=null
    var scaledbmp :Bitmap?=null
    var PERMISSION_CODE = 101


    override fun onCreate(savedInstanceState: Bundle?) {

        isActivityDestroyed = false
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfactivity)
        imageView = findViewById(R.id.imageviewPDf)
        retreivedText = findViewById(R.id.textViewExtractText)
        val filepath = intent.getStringExtra("FilePath")
        val receivedText = intent.getStringExtra("ExtractedText")
       // bitmap = BitmapFactory.decodeResource(resources, R.drawable.horizontaldummypic)
         bitmap = BitmapFactory.decodeFile(filepath)//converts filepath back to bitmap
        imageView?.setImageBitmap(bitmap)
        retreivedText.text=receivedText
        SendEmailButton = findViewById(R.id.EmailButton)
        SendEmailButton.setOnClickListener {
            val intent = Intent(this, EmailActivity::class.java)
            startActivity(intent)
        }

        bitmap?.let {
            scaledbmp = Bitmap.createScaledBitmap(it, 140, 140, false)
        }
        // }
        generatePDFBtn = findViewById(R.id.idBtnGeneratePdf)
        if (checkPermissions()) {
            Toast.makeText(this, "Permissions Granted..", Toast.LENGTH_SHORT).show()
        } else {

            requestPermission()
        }
        generatePDFBtn.setOnClickListener {

            generatePDF()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        isActivityDestroyed = true
    }
    /*_______Code attribution_______
    * The following website link was referred to, to program the pdf document
    * website link: https://www.geeksforgeeks.org/generate-pdf-file-in-android-using-kotlin/ */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun generatePDF(){
        var pdfPictureDocument: PdfDocument = PdfDocument()
        var paint: Paint = Paint()
        var title: Paint = Paint()

        var myPageInfo: PdfDocument.PageInfo? =
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()

        var myPage: PdfDocument.Page = pdfPictureDocument.startPage(myPageInfo)
        var canvas: Canvas = myPage.canvas

        scaledbmp = bitmap?.let { Bitmap.createScaledBitmap(it, pictureWidth, pictureHeight, false) }
        scaledbmp?.let {canvas.drawBitmap(it, 56F, 220F, paint)}
        // our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
        title.textSize = 30F
        title.setColor(ContextCompat.getColor(this, R.color.black))
        canvas.drawText(retreivedText.text.toString(), 340F, 180F, title)
        // and then we are passing our variable of paint which is title.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
        title.textSize = 30F
        title.setColor(ContextCompat.getColor(this, R.color.black))
        canvas.drawText("Barcode: ", 130F, 180F, title)

        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        title.setColor(ContextCompat.getColor(this, R.color.black))
        title.textSize = 40F
        title.textAlign = Paint.Align.CENTER
        canvas.drawText("Harbour master barcode scan report", 340F, 110F, title)

        // PDF file we will be finishing the page.
        pdfPictureDocument.finishPage(myPage)

        // PDF file and its path.
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val timestamp  = System.currentTimeMillis()
        val fileName = "HarbourMasterReport_$timestamp.pdf"
        val file = File(downloadsDir, fileName)
        val pdfUri: Uri = Uri.fromFile(file)
        try {
            val fos = FileOutputStream(file)
            pdfPictureDocument.writeTo(fos)
            pdfPictureDocument.close()

            Log.e("Success","PDF generation success")
            Toast.makeText(this, "PDF Generating...", Toast.LENGTH_LONG).show()
            DownloadDialog()
            Toast.makeText(this, "Download succeeded ${file.path}", Toast.LENGTH_LONG).show()


        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Failed", "PDF generation fail: ${e.message}")
        }

        pdfPictureDocument.close()
    }
    private fun DownloadDialog() {

        val addDialog = AlertDialog.Builder(this)
        addDialog.setMessage("PDF is downloaded in the devices Download folder")
        addDialog.setPositiveButton(android.R.string.ok) { dialog, which ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }
    fun checkPermissions(): Boolean {

        var writeStoragePermission = ContextCompat.checkSelfPermission(
            applicationContext,
            WRITE_EXTERNAL_STORAGE
        )

        var readStoragePermission = ContextCompat.checkSelfPermission(
            applicationContext,
            READ_EXTERNAL_STORAGE
        )

        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && readStoragePermission == PackageManager.PERMISSION_GRANTED
    }
    fun requestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE
            ), PERMISSION_CODE
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CODE) {

            if (grantResults.size > 0) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                    == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show()

                } else {


                    Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

}