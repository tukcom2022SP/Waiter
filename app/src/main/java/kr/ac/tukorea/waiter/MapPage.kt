package kr.ac.tukorea.waiter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import com.naver.maps.map.NaverMapSdk
import android.Manifest;
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat

class MapPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_map_page)
        var permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        var permission1 =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permission == PackageManager.PERMISSION_DENIED || permission1 == PackageManager.PERMISSION_DENIED)

            title = "map"
        setContentView(R.layout.activity_map_page)
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient("av6rm647yo")
            return;
    }
}
//    @Override
//    public void onRequestPermissionsResult(var requestCode,String permissions[],int[] grandResults){
//        if(grantResults.  )
//     }

