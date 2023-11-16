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
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import com.CargoTrack.cargotrack.Model.DepotAddresses
import com.CargoTrack.cargotrack.Model.ForwardingAgentAddresses
import com.CargoTrack.cargotrack.Model.Locations
import com.cargotrack.cargotrack.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException

class DepoActivty : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    lateinit var database: DatabaseReference
    val locations = Locations()
    val depoAddresses = DepotAddresses()
    val AgentAddresses = ForwardingAgentAddresses()
    private lateinit var searchView: SearchView
    val specificAddress1 = depoAddresses.DurbanDepotAddress
    val specificAddress2 = depoAddresses.JoburgDepoAddress
    val specificAddress3 =  depoAddresses.PretoriaDepotAddress
    val specificAddress4 = depoAddresses.CapeTownDepotAddress
    val specificAddress5 = depoAddresses.PortElizabethDepotAddress


    /*Code attribution
   * The following youtube link was referred to when adding locations to the map
   * youtube link: https://www.youtube.com/watch?v=acUB18U-IM8 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forwarding_agent)

        mapView = findViewById(R.id.mapView2)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
        searchView = findViewById<SearchView>(R.id.idSearchView)
        val database = FirebaseDatabase.getInstance().getReference("locations")
        database.setValue(locations)

        val database2 = FirebaseDatabase.getInstance().reference
        database2.child("Addresses").child("AddressDepo").setValue(depoAddresses)
        database2.child("Addresses").child("AddressForwardingAgent").setValue(AgentAddresses)

        /*Code attribution
                      * The Following Search function was programmed referring to the following website link:
                      * Weblink: https://www.geeksforgeeks.org/how-to-add-searchview-in-google-maps-in-android/ */
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                val location = searchView.query.toString()
                var addressList : List<Address>? = null

                if(location !=  null || location == "") {

                    val geocoder = Geocoder(this@DepoActivty)
                    try{
                        addressList = geocoder.getFromLocationName(location,1)
                        if (addressList != null) {
                            if(addressList.isNotEmpty()){

                                val specificAddressList =  geocoder.getFromLocationName("$specificAddress1, $specificAddress2,$specificAddress3,$specificAddress4,$specificAddress5, $location", 1)
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
                                        mMap.addMarker(
                                            MarkerOptions().position(specificLatLng).title(specificAddress5)
                                        )
                                        // Animate camera to the specific address
                                        mMap.animateCamera(
                                            CameraUpdateFactory.newLatLngZoom(specificLatLng, 15f)
                                        )
                                    }
                                }
                                geocoder.getFromLocationName("$specificAddress1,$specificAddress2,$specificAddress3,$specificAddress4,$specificAddress5, $location", 1)
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

        CityName.text = depoAddresses.DurbanDepotAddress
        val bitmap= Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(bitmap)
        mMap.addMarker(MarkerOptions().position(locations.DurbanDepo))
        mMap.addMarker(MarkerOptions().position(locations.DurbanDepo).icon(smallMarkerIcon))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.DurbanDepo, 18.0f))

        CityName.text = depoAddresses.JoburgDepoAddress
        val bitmap3= Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon4 = BitmapDescriptorFactory.fromBitmap(bitmap3)
        mMap.addMarker(MarkerOptions().position(locations.JoburgDepo))
        mMap.addMarker(MarkerOptions().position(locations.JoburgDepo).icon(smallMarkerIcon4))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.JoburgDepo, 18.0f))


        CityName.text = depoAddresses.PretoriaDepotAddress
        val bitmap4= Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon5 = BitmapDescriptorFactory.fromBitmap(bitmap4)
        mMap.addMarker(MarkerOptions().position(locations.PretoriaDepo))
        mMap.addMarker(MarkerOptions().position(locations.PretoriaDepo).icon(smallMarkerIcon5))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.PretoriaDepo, 18.0f))

        CityName.text = depoAddresses.CapeTownDepotAddress
        val bitmap5= Bitmap.createScaledBitmap(viewToBitmap(CardView)!!, CardView.width, CardView.height, false)
        val smallMarkerIcon6 = BitmapDescriptorFactory.fromBitmap(bitmap5)
        mMap.addMarker(MarkerOptions().position(locations.CapeTownDepo))
        mMap.addMarker(MarkerOptions().position(locations.CapeTownDepo).icon(smallMarkerIcon6))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.CapeTownDepo, 18.0f))

        CityName.text = depoAddresses.PortElizabethDepotAddress
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