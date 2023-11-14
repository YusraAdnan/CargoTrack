package com.CargoTrack.cargotrack

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.CargoTrack.cargotrack.Model.Locations
import com.cargotrack.cargotrack.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ForwardingAgent : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap:GoogleMap
    private lateinit var mapView: MapView
    lateinit var database: DatabaseReference
    val locations = Locations()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_fragment)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        val locations = Locations()
        val database = FirebaseDatabase.getInstance().getReference("locations")
        database.setValue(locations)
    }

    @SuppressLint("SetTextI18n")
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
      // mMap.setInfoWindowAdapter(this)
        val markerView = (getSystemService(LAYOUT_INFLATER_SERVICE)as LayoutInflater).inflate(R.layout.marker_layout, null)

        val CityName= markerView.findViewById<TextView>(R.id.CityName)
        val CardView = markerView.findViewById<CardView>(R.id.cardView)

        CityName.text = ("Johannesburg\n" +
                "5 Estee Ackerman Street\n" +
                "South Lake Office Park")
        val bitmap=Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(bitmap)
        mMap.addMarker(MarkerOptions().position(locations.Johannesburg))

        mMap.addMarker(MarkerOptions().position(locations.Johannesburg).icon(smallMarkerIcon))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.Johannesburg, 18.0f))

        CityName.text = ("Durban\n" +
                "16 Cranbrook Park,\n" +
                "Cranbrook Crescent\n" +
                "La Lucia Ridge")
        val bitmap1=Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon2 = BitmapDescriptorFactory.fromBitmap(bitmap1)
        mMap.addMarker(MarkerOptions().position(locations.Durban))

           mMap.addMarker(MarkerOptions().position(locations.Durban).icon(smallMarkerIcon2))
           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.Durban, 18.0f))


        CityName.text = ("East London\n" +
                "164 Main Road,\n" +
                "Amalinda, East London")
        val bitmap2=Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon3 = BitmapDescriptorFactory.fromBitmap(bitmap2)
        mMap.addMarker(MarkerOptions().position(locations.EastLondon))
        mMap.addMarker(MarkerOptions().position(locations.EastLondon).icon(smallMarkerIcon3))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.EastLondon, 18.0f))

        CityName.text = ("Cape Town\n" +
                "Unit 25, Prestige Business Park\n" +
                "Democracy Way\n" +
                "Marconi Beam\n" +
                "Montague Gardens, 7407")
        val bitmap3=Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon4 = BitmapDescriptorFactory.fromBitmap(bitmap3)
        mMap.addMarker(MarkerOptions().position(locations.CapeTown))
        mMap.addMarker(MarkerOptions().position(locations.CapeTown).icon(smallMarkerIcon4))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.CapeTown, 18.0f))

    }

    private fun viewToBitmap(view:View):Bitmap?{
        view.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas=Canvas(bitmap)
        view.layout(0,0,view.measuredWidth,view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }

}