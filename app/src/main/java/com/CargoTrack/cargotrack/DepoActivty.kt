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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DepoActivty : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    lateinit var database: DatabaseReference
    val locations = Locations()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forwarding_agent)

        mapView = findViewById(R.id.mapView2)
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

        CityName.text = ("Durban Depot\n" +
                "26 Bayhead Rd, Bayhead")
        val bitmap= Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(bitmap)
        mMap.addMarker(MarkerOptions().position(locations.DurbanDepo))
        mMap.addMarker(MarkerOptions().position(locations.DurbanDepo).icon(smallMarkerIcon))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.DurbanDepo, 18.0f))

        CityName.text = ("Joburg Depot\n" +
                "13 Chilvers St, Denver, Johannesburg, 2011\n" +
                "GRINDROD LOGISTICS")
        val bitmap3= Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon4 = BitmapDescriptorFactory.fromBitmap(bitmap3)
        mMap.addMarker(MarkerOptions().position(locations.JoburgDepo))
        mMap.addMarker(MarkerOptions().position(locations.JoburgDepo).icon(smallMarkerIcon4))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.JoburgDepo, 18.0f))


        CityName.text = ("Pretoria Depot\n" +
                "Paul Kruger St, Capital Park, Pretoria, 2001\n" +
                "Pretcon Container Terminal")
        val bitmap4= Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon5 = BitmapDescriptorFactory.fromBitmap(bitmap4)
        mMap.addMarker(MarkerOptions().position(locations.PretoriaDepo))
        mMap.addMarker(MarkerOptions().position(locations.PretoriaDepo).icon(smallMarkerIcon5))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.PretoriaDepo, 18.0f))

        CityName.text = ("CapeTown Depot\n" +
                "5 SATI RD, KILLARNEY GARDENS\n" +
                "APM TERMINALS (SATI)")
        val bitmap5= Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon6 = BitmapDescriptorFactory.fromBitmap(bitmap5)
        mMap.addMarker(MarkerOptions().position(locations.CapeTownDepo))
        mMap.addMarker(MarkerOptions().position(locations.CapeTownDepo).icon(smallMarkerIcon6))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.CapeTownDepo, 18.0f))

        CityName.text = ("PortElizabeth Depot\n" +
                "151 – 157 BURMAN ROAD  DEAL PARTY\n" +
                "GRINDROD INTERMODAL")
        val bitmap6= Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon7 = BitmapDescriptorFactory.fromBitmap(bitmap6)
        mMap.addMarker(MarkerOptions().position(locations.PortElizabethDepo))
        mMap.addMarker(MarkerOptions().position(locations.PortElizabethDepo).icon(smallMarkerIcon7))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.PortElizabethDepo, 18.0f))

    }
    private fun viewToBitmap(view: View):Bitmap?{
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas= Canvas(bitmap)
        view.layout(0,0,view.measuredWidth,view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }
}