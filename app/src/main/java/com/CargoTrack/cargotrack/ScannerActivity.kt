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
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.w3c.dom.Text
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
    var text: String? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnTakePhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_camera_alt_24,0,0,0)
        viewFinder = findViewById(R.id.viewFinder)


        val intent2 = Intent(this, CameraActivity::class.java)
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


    }
    fun sendImage(file: File) {    //sends the captured image to the API and returns the text

        val imageFile = bitmap?.let {
            bitmapToFile(applicationContext, it,"sentfile" )
        }
        //adds the bitmap picture to the request body for the multi-part body
        val requestBody =
               imageFile?.let { RequestBody.create("image/*".toMediaTypeOrNull(), it) }?.let {
                   MultipartBody.Builder()        //multipart helps send files to API efficiently
                       .setType(MultipartBody.FORM)
                       .addFormDataPart(
                           "sentfile",
                           imageFile?.name,
                           it
                       )
                       .build()
               }

        Log.e("Enter Message", "Entered the sendImage function")
        Toast.makeText(this, "Generating barcode..", Toast.LENGTH_LONG).show()
        val apiService = ApiClient.buildService()
        requestBody?.let {
            apiService.SendImage(it)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { response: ApiResponse ->
                        text = response.text
                       // textView.text = text
                        Log.e("SuccessSending", "returned text: $text")
                        val intent = Intent(this, CameraActivity::class.java)
                        intent.putExtra("CapturedImagePath", file.absolutePath)
                        intent.putExtra("ExtractedText", text)
                        startActivity(intent)

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
    /*____________Code attribution_____________
  *The following youtube tutorial was used to help program the camera capturing functionality:
  * youtube video link: https://www.youtube.com/watch?v=HjXJh_vHXFs
  * chatGpt was also used to help troubleshoot: https://chat.openai.com/ */
    private fun getOutputDirectory(): File{
        val mediaDir = externalMediaDirs.firstOrNull()?.let { mFile ->
            File(mFile, resources.getString(R.string.app_name)).apply{
                mkdir()
            }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
    /*_________code attribution____________
     *the following link was used to derive the bitmapToFileFunction
     *Website link: https://stackoverflow.com/questions/39630324/how-to-convert-a-bitmap-into-a-file-android
     * chatGpt was also used to help troubleshoot: https://chat.openai.com/ */
    fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File? {  // converts the captured bitmap into file in order to send to the API
        val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) //specifies where the file is stored in the subdirectory of app
        val imageFile = File(imagesDir, fileName)

        try {
            val outputStream: OutputStream = FileOutputStream(imageFile) //allows writing to the file
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) //writes bitmap to output stream
            outputStream.flush()
            outputStream.close()
            return imageFile //returns image file
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
                    filepath = photoFile.absolutePath //sends the file path to the next activity as the bitmap cannot be sent through intent

                   val capturedBitmap = BitmapFactory.decodeFile(photoFile.absolutePath) //decodes image file specified by photofile and converts to bitmap
                    //imageview.setImageBitmap(bitmap)
                    bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                   // imageview.setImageBitmap(bitmap)
                    //calls the function converting bitmap to file and gives the actual captured bitmap photo
                    val imageFile = bitmapToFile(applicationContext, capturedBitmap,fileName.toString() )
                    if(imageFile != null)
                    {
                        sendImage(imageFile)
                    }

                    Toast.makeText(this@ScannerActivity,
                        "Photo saved in gallery",
                        Toast.LENGTH_SHORT).show()

                   // Toast.makeText(this@ScannerActivity, "Generating barcode..", Toast.LENGTH_LONG).show()
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

    override fun onRequestPermissionsResult(          //when user responds to permissions
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings-> {
                val intent = Intent(this,SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //______________end__________________
}