package com.CargoTrack.cargotrack

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.CargoTrack.cargotrack.Model.ForwardingAgentAddresses
import com.CargoTrack.cargotrack.Model.Locations
import com.cargotrack.cargotrack.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException

class ForwardingAgent : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap:GoogleMap
    private lateinit var mapView: MapView
    lateinit var database: DatabaseReference
    val locations = Locations()
    val AgentAddresses = ForwardingAgentAddresses()
    private lateinit var searchView: SearchView
    val specificAddress1 = AgentAddresses.JoburgAgentAdress
    val specificAddress2 = AgentAddresses.DurbanAgentAddress
    val specificAddress3 = AgentAddresses.EastLondonAgentAddress
    val specificAddress4 = AgentAddresses.CapeTownAgentAddress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_fragment)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        searchView = findViewById<SearchView>(R.id.idSearchView)

        val database = FirebaseDatabase.getInstance().getReference("locations")
        database.setValue(locations)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                val location = searchView.query.toString()
                var addressList : List<Address>? = null

                if(location !=  null || location == "") {

                    val geocoder = Geocoder(this@ForwardingAgent)
                    try{
                        addressList = geocoder.getFromLocationName(location,1)
                        if (addressList != null) {
                            if(addressList.isNotEmpty()){

                                val specificAddressList =  geocoder.getFromLocationName("$specificAddress1, $specificAddress2,$specificAddress3,$specificAddress4, $location", 1)
                                if (specificAddressList != null) {
                                    if (specificAddressList.isNotEmpty()) {
                                        val specificLatLng =
                                            LatLng(specificAddressList[0].latitude, specificAddressList[0].longitude)

                                        // Add a marker for the specific address
                                        mMap.addMarker(
                                            MarkerOptions().position(specificLatLng).title(specificAddress1)
                                        )
                                        mMap.addMarker(
                                            MarkerOptions().position(specificLatLng).title(specificAddress2)
                                        )
                                        mMap.addMarker(
                                            MarkerOptions().position(specificLatLng).title(specificAddress3)
                                        )
                                        mMap.addMarker(
                                            MarkerOptions().position(specificLatLng).title(specificAddress4)
                                        )

                                        // Animate camera to the specific address
                                        mMap.animateCamera(
                                            CameraUpdateFactory.newLatLngZoom(specificLatLng, 15f)
                                        )
                                    }
                                }
                                    geocoder.getFromLocationName("$specificAddress1,$specificAddress2,$specificAddress3,$specificAddress4, $location", 1)
                            }

                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                    val address: Address = addressList?.get(0)!!
                    val latLng = LatLng(address.latitude, address.longitude)

                    // on below line we are adding marker to that position.
                    mMap.addMarker(MarkerOptions().position(latLng).title(location))

                    // below line is to animate camera to that position.
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    @SuppressLint("SetTextI18n")
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
      // mMap.setInfoWindowAdapter(this)
        val markerView = (getSystemService(LAYOUT_INFLATER_SERVICE)as LayoutInflater).inflate(R.layout.marker_layout, null)

        val CityName= markerView.findViewById<TextView>(R.id.CityName)
        val CardView = markerView.findViewById<CardView>(R.id.cardView)

        CityName.text = AgentAddresses.JoburgAgentAdress
        val bitmap=Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(bitmap)
        mMap.addMarker(MarkerOptions().position(locations.Johannesburg))

        mMap.addMarker(MarkerOptions().position(locations.Johannesburg).icon(smallMarkerIcon))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.Johannesburg, 18.0f))

        CityName.text = AgentAddresses.DurbanAgentAddress
        val bitmap1=Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon2 = BitmapDescriptorFactory.fromBitmap(bitmap1)
        mMap.addMarker(MarkerOptions().position(locations.Durban))

           mMap.addMarker(MarkerOptions().position(locations.Durban).icon(smallMarkerIcon2))
           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.Durban, 18.0f))


        CityName.text =AgentAddresses.EastLondonAgentAddress
        val bitmap2=Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon3 = BitmapDescriptorFactory.fromBitmap(bitmap2)
        mMap.addMarker(MarkerOptions().position(locations.EastLondon))
        mMap.addMarker(MarkerOptions().position(locations.EastLondon).icon(smallMarkerIcon3))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.EastLondon, 18.0f))

        CityName.text = AgentAddresses.CapeTownAgentAddress
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