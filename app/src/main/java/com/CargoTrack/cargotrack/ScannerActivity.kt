package com.CargoTrack.cargotrack

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import okhttp3.MediaType

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.CargoTrack.cargotrack.API.ApiService
import com.CargoTrack.cargotrack.Client.ApiClient
import com.CargoTrack.cargotrack.Model.ApiResponse
import com.CargoTrack.cargotrack.Model.ImageRequest
import com.cargotrack.cargotrack.R
import com.cargotrack.cargotrack.databinding.ActivityMainBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class ScannerActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var btnTakePhoto: Button
    private lateinit var imageview: ImageView
    private lateinit var viewFinder: PreviewView
    private lateinit var convertToPdf: Button
    private var compositeDisposable = CompositeDisposable()
    private var bitmap: Bitmap? = null
    var filepath: String? = null
    private lateinit var textView: TextView
    var savedUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        imageview = findViewById(R.id.imageview)
        viewFinder = findViewById(R.id.viewFinder)
        convertToPdf = findViewById(R.id.BtnCnvertPdf)
        textView = findViewById(R.id.textViewExtractText)

        outputDirectory =
            getOutputDirectory() //gets file directory where all captred photos are stored
        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, Constants.REQUIRED_PERMISSIONS,
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }
        btnTakePhoto.setOnClickListener {
            takePhoto()
        }
        val intent = Intent(this, PDFActivity::class.java)

        convertToPdf.setOnClickListener {

            intent.putExtra(
                "FilePath",
                filepath
            )//getting file path of taken picture from ImageCapture sending it to PDFActivity
            startActivity(intent)
        }
       // val imageFileName = "horizontaldummypic"
       // val resourceId = resources.getIdentifier(imageFileName, "drawable", packageName)
        /*if (resourceId != 0) {
            BitmapPictureSend = BitmapFactory.decodeResource(resources, resourceId)
            imageview.setImageBitmap(BitmapPictureSend)
        }*/
    }
    fun sendImage(file: File) {

        val imageFile = bitmap?.let {
            bitmapToFile(applicationContext, it,"sentfile" )
        }
           val requestBody =
               imageFile?.let { RequestBody.create("image/*".toMediaTypeOrNull(), it) }?.let {
                   MultipartBody.Builder()
                       .setType(MultipartBody.FORM)
                       .addFormDataPart(
                           "sentfile",
                           imageFile?.name,
                           it
                       )
                       .build()
               }

        Log.e("Enter Message", "Entered the sendImage function")
        val apiService = ApiClient.buildService()
        requestBody?.let {
            apiService.SendImage(it)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { response: ApiResponse ->
                        val text = response.text
                        textView.text = text
                        Log.e("SuccessSending", "returned text: $text")


                    }, { error: Throwable ->
                        Log.e("SendingImageError", "Error sending image: ${error.message}")

                    }
                )
        }?.let {
            compositeDisposable.add(
                it
            )
        }

    }

    fun uriToFile(uri: Uri, directory: File, fileName: String): File {

        val file = File(directory, fileName)
        file.createNewFile()
        try {
            val inputStream: InputStream? = this.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val outputStream: OutputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                inputStream.close()
                outputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
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
    fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File? {
        val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(imagesDir, fileName)

        try {
            val outputStream: OutputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return imageFile
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
    private fun takePhoto(){
        val imageCapture =imageCapture?: return
        val fileName = SimpleDateFormat(Constants.FILE_NAME_FORMAT, Locale.getDefault())
            .format(System.currentTimeMillis()) + ".jpg"

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
                    savedUri = Uri.fromFile(photoFile)
                    filepath = photoFile.absolutePath// this is used to send the file path to the next activity as the bitmap cannot be sent through intent

                   val capturedBitmap = BitmapFactory.decodeFile(photoFile.absolutePath) //decodes image file specified by photofile and converts to bitmap
                   imageview.setImageBitmap(bitmap) //uncomment when not using dummy data
                    bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    imageview.setImageBitmap(bitmap) //uncomment when not using dummy data
                    val imageFile = bitmapToFile(applicationContext, capturedBitmap,fileName.toString() )
                    if(imageFile != null)
                    {
                        sendImage(imageFile)
                    }

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