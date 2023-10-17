package com.CargoTrack.cargotrack

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.CargoTrack.cargotrack.Client.ApiClient
import com.CargoTrack.cargotrack.Model.ApiResponse
import com.cargotrack.cargotrack.R
import com.cargotrack.cargotrack.databinding.ActivityMainBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private  var imageCapture: ImageCapture?=null
    private lateinit var outputDirectory: File
    private lateinit var btnTakePhoto: Button
    private lateinit var buttonSendPic: Button
    private lateinit var imageview: ImageView
    private lateinit var viewFinder: PreviewView
    private lateinit var convertToPdf: Button
    var BitmapPictureSend: Bitmap? = null
    private var compositeDisposable = CompositeDisposable()
    private var bitmap: Bitmap? = null
    var filepath:String? = null
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        imageview = findViewById(R.id.imageview)
        viewFinder = findViewById(R.id.viewFinder)
        convertToPdf = findViewById(R.id.BtnCnvertPdf)
        textView = findViewById(R.id.textViewExtractText)
        buttonSendPic = findViewById(R.id.buttonsend)

        outputDirectory = getOutputDirectory() //gets file directory where all captred photos are stored
        if(allPermissionGranted()){
            startCamera()
        }else{
            ActivityCompat.requestPermissions(
                this, Constants.REQUIRED_PERMISSIONS,
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }
          btnTakePhoto.setOnClickListener{
            takePhoto()
        }
        val intent = Intent(this, PDFActivity::class.java)

        convertToPdf.setOnClickListener {

            intent.putExtra("FilePath", filepath)//getting file path of taken picture from ImageCapture sending it to PDFActivity
            startActivity(intent)
        }
        val imageFileName = "horizontaldummypic"
        val resourceId = resources.getIdentifier(imageFileName, "drawable", packageName)
        if (resourceId != 0) {
            BitmapPictureSend = BitmapFactory.decodeResource(resources, resourceId)
            imageview.setImageBitmap(BitmapPictureSend)
        }
        buttonSendPic.setOnClickListener { BitmapPictureSend?.let { it1 -> sendImage(it1) } }

    }
    fun sendImage(bitmap: Bitmap) {

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        //https://www.youtube.com/watch?v=aY9xsGMlC5c
        val requestFile = RequestBody.create(MediaType.parse("image/jpeg"),byteArray)//request body showing data in binary format
        val body = MultipartBody.Part.createFormData("image","image.jpg", requestFile)
        Log.e("Enter Message","Entered the sendImage function")

        val apiService = ApiClient.buildService()
        compositeDisposable.add(
            apiService.SendImage(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { response: ApiResponse ->
                        val extractedText = response.extractedText
                        textView.text = extractedText
                        //  val resp = bitmap.let { ImageRequest(it) }?.let { apiService.sendImage(bitmap) }
                    }, {error: Throwable ->
                        Log.e("SendingImageError","Error sending image: ${error.message}")

                    }
                ))
    }
    private fun getOutputDirectory(): File{
        val mediaDir = externalMediaDirs.firstOrNull()?.let { mFile ->
            File(mFile, resources.getString(R.string.app_name)).apply{
                mkdir()
            }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
    private fun takePhoto(){
        val imageCapture =imageCapture?: return
        val photoFile = File(
            outputDirectory, SimpleDateFormat(Constants.FILE_NAME_FORMAT,
                Locale.getDefault()).
            format(System
                .currentTimeMillis()) + ".jpg") //saves captured photo to directory

        //picture saved in jpg format
        val outputOption = ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        imageCapture.takePicture(
            outputOption, ContextCompat.getMainExecutor(this),
            object: ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    //saves captured picture in the file directory
                    val savedUri = Uri.fromFile(photoFile)
                    filepath = photoFile.absolutePath// this is used to send the file path to the next activity as the bitmap cannot be sent through intent
                    bitmap = BitmapFactory.decodeFile(photoFile.absolutePath) //decodes image file specified by photofile and converts to bitmap
                    //imageview.setImageBitmap(bitmap) //uncomment when not using dummy data

                    val msg = "Photo Saved"
                    Toast.makeText(this@ScannerActivity,
                        "$msg $savedUri",
                        Toast.LENGTH_SHORT).show()
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, photoFile.name)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/${resources.getString(R.string.app_name)}")
                    }
                    sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, savedUri))

                    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(Constants.TAG,
                        "onError: ${exception.message}", exception)
                }
            }
        )
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider
            .getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also{mPreview->
                    mPreview.setSurfaceProvider(
                        viewFinder.surfaceProvider
                    )

                }
            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
            }catch(e: Exception){
                Log.e(Constants.TAG, "startCamera Failed:", e)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult( //when user responds to permissions
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == Constants.REQUEST_CODE_PERMISSIONS){
            if(allPermissionGranted()){
                startCamera()
            }else{
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted()=
        Constants.REQUIRED_PERMISSIONS.all{
            ContextCompat.checkSelfPermission(
                baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }
}