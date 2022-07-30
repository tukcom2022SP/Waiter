package kr.ac.tukorea.waiter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.Manifest;
import android.app.Instrumentation
import android.util.Log
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.naver.maps.map.*
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

    var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_page)
//        var permission =
//            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//        var permission1 =
//            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//        if (permission == PackageManager.PERMISSION_DENIED || permission1 == PackageManager.PERMISSION_DENIED)


        fun startProcess(){
            val fm  = supportFragmentManager
            val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
                ?: MapFragment.newInstance().also{
                    fm.beginTransaction().add(R.id.map, it).commit()
                }
            mapFragment.getMapAsync(onMapReady)
        }
        fun isPermitted(): Boolean {
            for (perm in permissions){
                if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED){
                    return false
                }
            }
            return true
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

//    override fun onMapReady(naverMap: NaverMap) {
//        val cameraPosition = CameraPosition(
//        )
//    }
}
//    @Override
//    public void onRequestPermissionsResult(var requestCode,String permissions[],int[] grandResults){
//        if(grantResults.  )
//     }

