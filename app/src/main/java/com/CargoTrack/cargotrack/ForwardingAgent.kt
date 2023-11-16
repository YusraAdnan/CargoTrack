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
import android.widget.Toast
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

    /*Code attribution
   * The following youtube link was referred to when adding locations to the map
   * youtube link: https://www.youtube.com/watch?v=acUB18U-IM8 */
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

                /*Code attribution
                * The Following Search function was programmed referring to the following website link:
                * Weblink: https://www.geeksforgeeks.org/how-to-add-searchview-in-google-maps-in-android/ */

                val location = searchView.query.toString() //location from searchView
                var addressList : List<Address>? = null // stores all the address lists

                if(location !=  null || location == "") {
                    /*The following link was used to code the specific area search view:
                    * website link: https://www.geeksforgeeks.org/how-to-add-searchview-in-google-maps-in-android/ */

                    val geocoder = Geocoder(this@ForwardingAgent)
                    try{
                        addressList = geocoder.getFromLocationName(location,1) //getting the searched location name and setting it to the list
                        if (addressList != null) {
                            if(addressList.isNotEmpty()){

                    /*The following link was used to code the specific area search view:
                    * website link: https://www.tabnine.com/code/java/methods/android.location.Geocoder/getFromLocationName
                    * website link: http://www.java2s.com/example/java-api/android/location/geocoder/getfromlocationname-6-0.html */
                                val ActualAddressList =  geocoder.getFromLocationName("$specificAddress1" +
                                        ", $specificAddress2,$specificAddress3,$specificAddress4, $location", 1) //adds the specified addresses to another list
                                if (ActualAddressList != null) {
                                    if (ActualAddressList.isNotEmpty()) {
                                        val specificLatLng =
                                            LatLng(ActualAddressList[0].latitude, ActualAddressList[0].longitude) //sets the long/lat of the specified address to variable

                                        // Add a marker for the specific address
                                        mMap.addMarker(
                                            MarkerOptions().position(specificLatLng).title(specificAddress1)//makes marker for the specified co-ordinates
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

                                    else{
                                        Toast.makeText(this@ForwardingAgent, "Address Specific address not found ", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }else{
                                Toast.makeText(this@ForwardingAgent, "Location not found for $location", Toast.LENGTH_SHORT).show()
                            }
                            geocoder.getFromLocationName("$specificAddress1," +
                                    "$specificAddress2,$specificAddress3,$specificAddress4, $location", 1)

                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                    val address: Address = addressList?.get(0)!!
                    val latLng = LatLng(address.latitude, address.longitude)

                    mMap.addMarker(MarkerOptions().position(latLng).title(location))
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