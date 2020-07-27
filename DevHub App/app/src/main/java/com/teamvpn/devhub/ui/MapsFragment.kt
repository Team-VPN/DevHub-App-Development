package com.teamvpn.devhub.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.teamvpn.devhub.MainActivity
import com.teamvpn.devhub.ModelClass.PostClass
import com.teamvpn.devhub.R
import es.dmoral.toasty.Toasty
import java.io.IOException


class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener {
    private lateinit var locationCallback : LocationCallback
    private lateinit var locationRequest : LocationRequest
    private var locationUpdateState = false
    lateinit var user: FirebaseUser
     var flagging = false
    //Creating member variables
    var refUsers: DatabaseReference? = null
    //var refUsersMain: DatabaseReference? = null
    var firebaseUser: FirebaseUser?= null
    var userId:String?=null
    lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        checkPermission()
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        flagging = true
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f // 170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.isNotEmpty()) {
                    // get latest location
                    val location =
                        locationResult.lastLocation
                    MarkerOptions().position(LatLng(location.latitude,location.longitude)).title("My Location")
                    firebaseUser = FirebaseAuth.getInstance().currentUser
                    val myLoc = mMap.addMarker(MarkerOptions().position(LatLng(location.latitude,location.longitude)).title("My Location").snippet(MainActivity.username))
                    BitMapToMap(myLoc,MainActivity.url_for_image_link)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude), 12.0f));
                    SEND_LOCATION_DATA_TO_FIREBASE().execute(location)
                    //mMap.addMarker(MarkerOptions().position(LatLng(13.116881,77.634622)).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).title("demo"))
                    // use your location object
                    // get latitude , longitude and other info from this

                    refUsers = FirebaseDatabase.getInstance().reference.child("users")
                    if(flagging){
                        refUsers!!.addValueEventListener(object: ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {

                                for (snapshot in p0.children) {
                                    val username = snapshot.child("username").value.toString()
                                    val my_uid = snapshot.child("uid").value.toString()
                                    val my_url = snapshot.child("image_url").value.toString()

                                    if(snapshot.hasChild("lat") and snapshot.hasChild("lon")){
                                        //val latitude = snapshot.child("lan").value
                                        //val longitude = snapshot.child("lon").value
                                        val data:HashMap<String,Double> = snapshot.value as HashMap<String, Double>
                                        val latitude = data["lat"]
                                        val longitude = data["lon"]

                                            //context?.let { Toasty.error(it,xxx.toString(),Toast.LENGTH_LONG).show() }
                                        if(latitude != null && longitude != null){
                                            context?.let { Toasty.success(it,username,Toast.LENGTH_LONG).show() }
                                            val latlon = LatLng(latitude as Double,
                                                longitude as Double
                                            )
                                            val myLocation = mMap.addMarker(MarkerOptions().position(latlon!!).title(username))
                                            myLocation.tag = my_uid
                                            BitMapToMap(myLocation,my_url)
                                        }


                                    }


                                }
                            }
                            override fun onCancelled(p0: DatabaseError) {



                            }
                        })
                    }


                }
                }


            }

        }



private fun BitMapToMap(markeer: Marker,URL:String){
    //val markeer = mMap.addMarker(MarkerOptions().position(LatLng(13.116881,77.634622)).title("demo"))
    val marker = PicassoMarker(markeer)
    //val URL = "https://avatars3.githubusercontent.com/u/65026677?s=400&u=6e5cce51fb0cbd296afb631a79531c18540acc51&v=4"
    Picasso.get().load(URL).into(marker)
}





    private fun placeMarker(latlan: LatLng) {

        val markerOptions = MarkerOptions().position(latlan)

        val title = getAddress(latlan)

        markerOptions.title("This is our app user location")

        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.mipmap.ic_launcher
                )
            )
        )

        mMap.addMarker(markerOptions)

    }

    private fun getAddress(latlng: LatLng): String {


        val geoCoder = Geocoder(context)
        val addresses: List<Address>?
        val address: Address
        var addressText = ""


        try {
            addresses = geoCoder.getFromLocation(latlng.latitude, latlng.longitude, 1)

            if (addresses != null && !addresses.isEmpty()) {

                address = addresses[0]

                for (i in 0..address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(
                        i
                    )
                }


            }

        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

            locationCallback = object : LocationCallback(){
                override fun onLocationResult(p0: LocationResult?) {
                    super.onLocationResult(p0)
                    val lastLocation = p0!!.lastLocation

                   // placeMarker(LatLng(lastLocation.latitude, lastLocation.longitude))
                }
            }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            createLocationRequest()

        return view
    }
    fun ADD_MARKERS(){
        refUsers = FirebaseDatabase.getInstance().reference.child("users")
        if(flagging){
            refUsers!!.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {

                    for (snapshot in p0.children) {
                        val username = snapshot.child("username").value.toString()
                        val my_uid = snapshot.child("uid").value.toString()
                        val my_url = snapshot.child("image_url").value.toString()
                        if(snapshot.hasChild("lat") and snapshot.hasChild("lon")){
                            val myLocation = mMap.addMarker(MarkerOptions().position(LatLng(snapshot.child("lat").value.toString().toDouble(),snapshot.child("lot").value.toString().toDouble())).title(username))
                            myLocation.tag = my_uid
                            BitMapToMap(myLocation,my_url)
                        }


                    }
                }
                override fun onCancelled(p0: DatabaseError) {



                }
            })
        }


    }

    private fun startLocationUpdates(){
        checkPermission()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun createLocationRequest(){

        locationRequest = LocationRequest()

        locationRequest.interval = 10000

        locationRequest.fastestInterval = 5000

        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = context?.let { LocationServices.getSettingsClient(it) }

        val task = client?.checkLocationSettings(builder.build())

        task?.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }

        task?.addOnFailureListener { e ->

            if (e is ResolvableApiException){
                try {
                    e.startResolutionForResult(context as Activity?, REQUEST_CHECK_SETTINGS)
                }catch (e : IntentSender.SendIntentException){
                    Log.e("MAPSACTIVITY",e.toString())
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((context?.let {
                            ContextCompat.checkSelfPermission(
                                it,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        } ==
                                PackageManager.PERMISSION_GRANTED)) {
                        context?.let {
                            Toasty.success(
                                it,
                                "Permission Granted to access your location",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    context?.let {
                        Toasty.error(
                            it,
                            "Permission Denied to access your location, It is your privacy to show up your location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                return
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }



    private fun checkPermission() {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        val uid = p0?.id.toString()

        return false
    }



    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (!locationUpdateState){
            startLocationUpdates()
        }
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val PLACE_PICKER_REQUEST = 3

    }


    private class SEND_LOCATION_DATA_TO_FIREBASE :
        AsyncTask<Location,Void,Void>() {

        override fun doInBackground(vararg params: Location?): Void? {
            val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
            val database = FirebaseDatabase.getInstance().getReference("users/$firebaseUserID")
            val childUpdates=HashMap<String,Double?>()
            // here param[0] means some instance of a class
            childUpdates["lat"]= params[0]?.latitude?.toDouble()
            childUpdates["lon"] = params[0]?.longitude
            database.updateChildren(childUpdates as Map<String, Any?>)
            return null
        }


    }

    //https://avatars3.githubusercontent.com/u/65026677?s=400&u=6e5cce51fb0cbd296afb631a79531c18540acc51&v=4
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, 100, 100)
            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

}


