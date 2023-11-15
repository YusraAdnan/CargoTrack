package com.CargoTrack.cargotrack

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.cargotrack.cargotrack.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream

class CameraActivity : AppCompatActivity() {

    var bitmap : Bitmap?=null
    private var imageView: ImageView? = null
    lateinit var retreivedText : TextView
    private lateinit var convertToPdf: FloatingActionButton
    private var isActivityDestroyed = false
    lateinit var generatePDFBtn: Button
    lateinit var SendEmailButton: Button
    var pageHeight = 1120
    var pageWidth = 792
    var pictureHeight = 600
    var pictureWidth = 600
    var scaledbmp :Bitmap?=null
    var PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        supportActionBar?.hide()
        imageView = findViewById(R.id.imageview)
        retreivedText = findViewById(R.id.textViewExtractText)
        SendEmailButton = findViewById(R.id.EmailButton)
        SendEmailButton.setOnClickListener {
            sendEmail()
        }


        val filepath = intent.getStringExtra("CapturedImagePath")
        val receivedText = intent.getStringExtra("ExtractedText")
        retreivedText.text=receivedText

        bitmap = BitmapFactory.decodeFile(filepath)//converts filepath back to bitmap
        imageView?.setImageBitmap(bitmap)



       /* convertToPdf.setOnClickListener {

            intent.putExtra(
                "FilePath",
                filepath
            )
            intent.putExtra(
                "ExtractedText",
                retreivedText.text.toString()
            )//getting file path of taken picture from ImageCapture sending it to PDFActivity
            startActivity(intent)
        }*/

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
        uri = FileProvider.getUriForFile(this, "com.CargoTrack.cargotrack.provider", file)

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
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        var readStoragePermission = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && readStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun sendEmail() {
        try {

            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "plain/text"
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
            this.startActivity(Intent.createChooser(emailIntent, "Sending email..."))
            Toast.makeText(this, "Email sent", Toast.LENGTH_LONG).show()

        }
        catch (t: Throwable) {
            Toast.makeText(this, "Request failed try again: $t", Toast.LENGTH_LONG).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uri = data.data!!
            }

        }
    }
    companion object {
        public const val PDF_REQUEST_CODE = 123
        var uri: Uri? = null// An arbitrary request code
    }
    fun requestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
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