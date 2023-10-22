package com.CargoTrack.cargotrack

import android.app.Activity
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.cargotrack.cargotrack.R

class EmailActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etSubject: EditText
    lateinit var etMessage: EditText
    lateinit var sendEmail: Button
    lateinit var attachment: Button
    lateinit var tvAttachment: TextView
    lateinit var email: String
    lateinit var subject: String
    lateinit var message: String
    lateinit var uri: Uri
    private val pickFromGallery:Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

    /*____________Code attribution_____________
    *The following website was used to help program the email sending and pdf attatchment:
    * Website link: https://www.tutorialspoint.com/how-to-send-an-email-with-a-file-attachment-in-android-using-kotlin */

        etEmail = findViewById(R.id.etTo)
        etSubject = findViewById(R.id.etSubject)
        etMessage = findViewById(R.id.etMessage)
        attachment = findViewById(R.id.btAttachment)
        tvAttachment = findViewById(R.id.tvAttachment)
        sendEmail = findViewById(R.id.btSend)
        sendEmail.setOnClickListener { sendEmail() }
        attachment.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, PDF_REQUEST_CODE)
            openFolder()
        }
    }
     /*____________Code attribution_____________
    *The following youtube tutorial was used to help program the pdf attatchment:
    * Youtube video link: https://www.youtube.com/watch?v=vaKFSUmZ31A
    * */
    private fun openFolder() {
        val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
        startActivity(intent)
    }

    /*____________Code attribution_____________
    *The following website was used to help program the email sending and pdf attatchment:
    * Website link: https://www.tutorialspoint.com/how-to-send-an-email-with-a-file-attachment-in-android-using-kotlin */

    private fun sendEmail() {
        try {
            email = etEmail.text.toString()
            subject = etSubject.text.toString()
            message = etMessage.text.toString()
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "plain/text"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
            emailIntent.putExtra(Intent.EXTRA_TEXT, message)
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
            emailIntent.putExtra(Intent.EXTRA_TEXT, message)

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
            tvAttachment.text = uri.lastPathSegment
            tvAttachment.visibility = View.VISIBLE
        }
    }
    companion object {
        private const val PDF_REQUEST_CODE = 123 // An arbitrary request code
    }
}