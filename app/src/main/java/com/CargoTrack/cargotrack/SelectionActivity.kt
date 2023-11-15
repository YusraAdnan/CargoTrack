package com.CargoTrack.cargotrack

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.cargotrack.cargotrack.R
import com.cargotrack.cargotrack.databinding.ActivityMainBinding

class SelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var takePicture: Button
    private lateinit var sharePdf: Button
    private lateinit var FindForwardingAgent: Button
    private lateinit var FindDepot: Button
    var PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.action_selection)

        takePicture = findViewById(R.id.takeAPhoto)
        FindForwardingAgent = findViewById(R.id.ForwardingButton)
        FindDepot = findViewById(R.id.DepotButton)
        sharePdf = findViewById(R.id.sharePdf)
        sharePdf.setOnClickListener {
           sendEmail()
        }
        takePicture.setOnClickListener {
            val  intent = Intent(this, ScannerActivity::class.java)
            startActivity(intent)
        }
        FindForwardingAgent.setOnClickListener {
            val intent = Intent(this, ForwardingAgent::class.java)
            startActivity(intent)
        }
        FindDepot.setOnClickListener {
            val intent = Intent(this, DepoActivty::class.java)
            startActivity(intent)
        }
        mySettings()
    }

    private fun mySettings(){

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
    private fun sendEmail() {
        try {

            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "plain/text"
            emailIntent.putExtra(Intent.EXTRA_STREAM, CameraActivity.uri)
            this.startActivity(Intent.createChooser(emailIntent, "Sending email..."))
            Toast.makeText(this, "Email sent", Toast.LENGTH_LONG).show()

        }
        catch (t: Throwable) {
            Toast.makeText(this, "Request failed try again: $t", Toast.LENGTH_LONG).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CameraActivity.PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                CameraActivity.uri = data.data!!
            }

        }
    }
}