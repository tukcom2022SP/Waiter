package kr.ac.tukorea.waiter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.Manifest;
import android.annotation.SuppressLint
import android.app.Instrumentation
import android.location.Location
import android.os.Looper
import android.util.Log
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import java.util.jar.Pack200

class MapPage : AppCompatActivity(), OnMapReadyCallback{

//    private val requestPermissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            if (isGranted) {
//                Log.i ("Permission: ", "Granted")
//            }
//            else {
//                Log.i("Permission:  ","Denied")
//            }
//        }
//    private fun requestPermission() {
//        when {
//            ContextCompat.checkSelfPermission(
//                this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED-> {}
//            ActivityCompat.shouldShowRequestPermissionRationale(
//                this, Manifest.permission.ACCESS_FINE_LOCATION)-> {
//                Toast.makeText(this, "위치를 사용할려면 권한이 필요합니다.",Toast.LENGTH_SHORT).show()
//                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//            }
//            else -> {}
//        }
//    }
    val permission_request = 99
    private lateinit var naverMap: NaverMap
    lateinit var  fusedLocationProvideClient : FusedLocationProviderClient
    lateinit var  locationCallback: LocationCallback
    var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_page)
//        var permission =
//            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//        var permission1 =
//            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//        if (permission == PackageManager.PERMISSION_DENIED || permission1 == PackageManager.PERMISSION_DENIED)



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
            ActivityCompat.requestPermissions(this, permissions, permission_request)
        }
            title = "map"
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient("8eo4a3qdn1")
            return;
    }
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        val cameraPosition = CameraPosition(
            LatLng(37.21312,126.321312),
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
           interval = 1000
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
        val cameraUpdate = CameraUpdate.scrollTo(myLocation)
        naverMap.moveCamera(cameraUpdate)
   }
}


//    @Override
//    public void onRequestPermissionsResult(var requestCode,String permissions[],int[] grandResults){
//        if(grantResults.  )
//     }

