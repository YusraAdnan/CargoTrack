package com.CargoTrack.cargotrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
}