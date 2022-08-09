package kr.ac.tukorea.waiter

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.annotation.UiThread
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.util.FusedLocationSource
import kr.ac.tukorea.waiter.databinding.ActivityInformationRegistrationPageBinding

class Information_Registration_Page : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    lateinit var fusedLocationProvideClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    private lateinit var locationSource: FusedLocationSource
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 2f8e49e7fefd85e3d4c11dc88ca0a8fd"
        const val LOCATION_PERMISSION_REQUEST_CODE  = 1000
    }

    var db: FirebaseFirestore = Firebase.firestore
    private var mbinding : ActivityInformationRegistrationPageBinding? = null
    private val binding get() = mbinding!!

    // 입력 값 저장할 변수들 얘들을 디비로?
    private var AddressString : String? = null
    private var CorpNumString : String? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  //메뉴
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu1,menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        mbinding = ActivityInformationRegistrationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()

        binding.searchAddress.setOnClickListener {  // 주소를 등록하기 위한 검색 API 사용 버튼

            val restMap = hashMapOf(
                // 식당 이름, 주소, x, y, 연락처 DB에 넣기
                "storeName" to binding.storeAddressEdit.text.toString(),
//                "roadNameAddress" to binding.text.toString(),
//                "parcelAddress" to binding.text.toString(),
//                "storeCallNum" to binding.text.toString(),
//                "latitude_x" to binding.text.toString(),
//                "longitude_y" to binding.text.toString()
            )

            db.collection("restInfo").add(restMap)
                .addOnSuccessListener {

                }

        }

        binding.registrationBtn.setOnClickListener {
            if(binding.storeAddressEdit.text.toString().equals("")
                || binding.storeCorpNumEdit.text.toString().equals("") ){
                Toast.makeText(this, "식당 등록에 필요한 정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                //activName.receiveData()
                AddressString = binding.storeAddressEdit.toString()
                CorpNumString = binding.storeCorpNumEdit.toString()



                Toast.makeText(this, "입력 완료", Toast.LENGTH_LONG).show()
                val intent = Intent(this,Waiting_List_Page::class.java)
                //intent.putExtra("storeName",binding.storeNameEdit.text.toString())
                startActivity(intent)
            }
        }
    }

    fun setLastLocation(location: Location) {
        //내 현 위치 찍어주기
        val myLocation = LatLng(location.latitude, location.longitude)

//        val marker = Marker()
//        marker.map = naverMap
//        marker.setOnClickListener(this)

        //맵위에 overlay
        val locationOverlay = naverMap.locationOverlay
        naverMap.locationOverlay.run {
            isVisible = true
            position = LatLng(location!!.latitude, location!!.longitude)
        }
        locationOverlay.isVisible = true
        locationOverlay.iconWidth = LocationOverlay.SIZE_AUTO
        locationOverlay.iconHeight = LocationOverlay.SIZE_AUTO
        val cameraUpdate = CameraUpdate.scrollTo(myLocation)//카메라 내위치에 표시
        naverMap.moveCamera(cameraUpdate)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        //맵구현
        this.naverMap = naverMap
        naverMap.uiSettings.isLocationButtonEnabled = true
        naverMap.locationSource = locationSource
        val cameraPosition = CameraPosition(
            LatLng(37.343991285297, 126.74729588817),
            16.0
        )
        naverMap.cameraPosition = cameraPosition
        fusedLocationProvideClient =
            LocationServices.getFusedLocationProviderClient(this)
        setUpdateLocationListener()
    }

    //만약에 권한을 받지 못했으면 메세지
    @SuppressLint("MissingPermission")
    fun setUpdateLocationListener() {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
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

    override fun onDestroy() {
        mbinding = null
        super.onDestroy()
    }
}