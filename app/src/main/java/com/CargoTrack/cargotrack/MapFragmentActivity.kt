package com.CargoTrack.cargotrack

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.cargotrack.cargotrack.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.w3c.dom.Text

class MapFragmentActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap:GoogleMap
    private lateinit var mapView: MapView
    val Johannesburg = LatLng(-26.16926924102308, 28.22331865263266)
    val Durban = LatLng(-29.736683887218078, 31.064520981645735)

    private  var locationArraryList:ArrayList<LatLng>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_fragment)


        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync(this)


       // val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
      //  map.getMapAsync(this)

        locationArraryList = ArrayList()
        locationArraryList!!.add(Johannesburg)
        locationArraryList!!.add(Durban)

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
        mMap.addMarker(MarkerOptions().position(Johannesburg))

        mMap.addMarker(MarkerOptions().position(Johannesburg).icon(smallMarkerIcon))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Johannesburg, 18.0f))

        CityName.text = ("Durban\n" +
                "Musgrave \n")
        val bitmap1=Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon2 = BitmapDescriptorFactory.fromBitmap(bitmap1)
        mMap.addMarker(MarkerOptions().position(Durban))

           mMap.addMarker(MarkerOptions().position(Durban).icon(smallMarkerIcon2))
           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Durban, 18.0f))

        /* mMap.addMarker(MarkerOptions().position(Johannesburg))
         mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f))
         mMap.moveCamera(CameraUpdateFactory.newLatLng(Johannesburg))*/




        /*for(i in locationArraryList!!.indices)
        {
            mMap.addMarker(MarkerOptions().position(locationArraryList!![i])
                .title("Johannesburg Office ")
                .snippet("ADDRESS\n" +
                        "South Lake Office Park\n" +
                        "5 Estee Ackerman Street\n" +
                        "Jet Park Ext 62\n" +
                        "Johannesburg 1462"))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18.0f))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationArraryList!!.get(i)))

        }*/
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