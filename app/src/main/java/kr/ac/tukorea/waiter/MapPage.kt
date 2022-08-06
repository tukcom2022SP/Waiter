package kr.ac.tukorea.waiter

//import android.app.Instrumentation
//import android.webkit.PermissionRequest
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContract
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.NonNull

import ResultSearchKeyword
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource



//import java.util.jar.Pack200

class MapPage : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener{
    private val GEOCODE_URL : String ="http://dapi.kakao.com/v2/local/search/address.json?query="
    private val GEOCODE_USER_INFO : String ="e2ff78b2e20ee43f72827e3e379c2191"
    val permissionrequest = 99
    private lateinit var naverMap: NaverMap
    lateinit var  fusedLocationProvideClient : FusedLocationProviderClient
    lateinit var  locationCallback: LocationCallback
    private lateinit var  locationSource: FusedLocationSource
    lateinit var keyword : EditText

    var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE  = 1000
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  //메뉴
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu1,menu)
        return true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_page)
        var keyword = findViewById<EditText>(R.id.keyword).getText().toString()
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        fun isPermitted(): Boolean {
            for (perm in permissions){
                if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED){
                    return false
                }
            }
            return true
        }



        fun startProcess(){
            val fm  = supportFragmentManager
            val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
                ?: MapFragment.newInstance().also{
                    fm.beginTransaction().add(R.id.map, it).commit()
                }
            mapFragment.getMapAsync(this)
        }
        if (isPermitted()){
            startProcess()
        }else{
            ActivityCompat.requestPermissions(this, permissions, permissionrequest)
        }
            title = "map"
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient("8eo4a3qdn1")
            return
    }
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
//        val locationButtonView: LocationButtonView = findViewById(R.id.locationbtn)
//       locationButtonView.setMap(naverMap)
        naverMap.uiSettings.isLocationButtonEnabled = true
        naverMap.locationSource = locationSource
        val cameraPosition = CameraPosition(
            LatLng(37.343991285297,126.74729588817),
            16.0
        )
        naverMap.cameraPosition= cameraPosition
        fusedLocationProvideClient =
            LocationServices.getFusedLocationProviderClient(this)
        setUpdateLocationListener()
   }
   @SuppressLint("MissingPermission")
   fun setUpdateLocationListener() {
       val locationRequest = LocationRequest.create()
       locationRequest.run {
           priority = LocationRequest.PRIORITY_HIGH_ACCURACY
       }
       locationCallback = object  : LocationCallback(){
           override fun onLocationResult(locationResult: LocationResult?) {
           locationResult ?: return
           for ((i, location) in locationResult.locations.withIndex()) {
               Log.d("location: ", "${location.latitude},${location.longitude}")
               setLastLocation(location)
           }
       }
       }
       fusedLocationProvideClient.requestLocationUpdates(
           locationRequest,
           locationCallback,
           Looper.myLooper()
       )
   }
    fun setLastLocation(location: Location) {
        val myLocation =LatLng(location.latitude,location.longitude)
        val marker = Marker()
        marker.position = myLocation
        marker.map = naverMap
        marker.setOnClickListener(this)

        val locationOverlay = naverMap.locationOverlay
        naverMap.locationOverlay.run {
            isVisible = true
            position = LatLng(location!!.latitude, location!!.longitude)
        }
        locationOverlay.isVisible = true
        locationOverlay.iconWidth = LocationOverlay.SIZE_AUTO
        locationOverlay.iconHeight = LocationOverlay.SIZE_AUTO
        //정보창 여는 예시 infoWindow.open(marker)
        val cameraUpdate = CameraUpdate.scrollTo(myLocation)
        naverMap.moveCamera(cameraUpdate)
   }
    override fun onClick(overlay: Overlay): Boolean {
        if (overlay is Marker) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        return true
    }
}

