package com.CargoTrack.cargotrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import com.cargotrack.cargotrack.R

class SelectionActivity : AppCompatActivity() {
    private lateinit var takePicture: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.action_selection)

        takePicture = findViewById(R.id.takeAPhoto)
        takePicture.setOnClickListener {
            val  intent = Intent(this, ScannerActivity::class.java)
            startActivity(intent)
        }
    }
}