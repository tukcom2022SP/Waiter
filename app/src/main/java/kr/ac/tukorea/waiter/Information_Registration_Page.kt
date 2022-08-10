package kr.ac.tukorea.waiter

import ResultSearchKeyword
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_information_registration_page.*
import kr.ac.tukorea.waiter.databinding.ActivityInformationRegistrationPageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Information_Registration_Page : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    lateinit var fusedLocationProvideClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    private lateinit var locationSource: FusedLocationSource
    val permissionrequest = 99

    var store_registration = hashMapOf<String, Any>()
    val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 2f8e49e7fefd85e3d4c11dc88ca0a8fd"
        const val LOCATION_PERMISSION_REQUEST_CODE  = 1000
    }
    var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
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

        //val intent = Intent(this, SearchPage::class.java)
        mbinding = ActivityInformationRegistrationPageBinding.inflate(layoutInflater)

        if(intent.hasExtra("storeName")) {
            var storeName = intent.getStringExtra("storeName")
            var roadNameAddress = intent.getStringExtra("roadNameAddress")
            var parcelAddress = intent.getStringExtra("parcelAddress")
            var storeCallNum = intent.getStringExtra("storeCallNum")
            var latitude_y = intent.getDoubleExtra("latitude_y", 0.0)
            var longitude_x = intent.getDoubleExtra("longitude_x", 0.0)


            var restMap = hashMapOf(
                // 식당 이름, 주소, x, y, 연락처 DB에 넣기
                "storeName" to storeName,
                "roadNameAddress" to roadNameAddress,
                "parcelAddress" to parcelAddress,
                "storeCallNum" to storeCallNum,
                "latitude_y" to latitude_y,
                "longitude_x" to longitude_x,
                "counter" to 0
            )
            store_registration = restMap.clone() as HashMap<String, Any>

            Log.d("restMap", "${restMap}")
            binding.storeAddress.text = intent.getStringExtra("roadNameAddress")
//            addItemsAndMarkers(longitude_x, latitude_y)
        }
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()

        fun isPermitted(): Boolean {
            for (perm in permissions) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        perm
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
            return true
        }
        fun startProcess() {
            //권한 확인
            val fm = supportFragmentManager
            Log.d("지도확인", "API 지도확인")
            val mapFragment = fm.findFragmentById(R.id.map2) as MapFragment?
                ?: MapFragment.newInstance().also {
                    fm.beginTransaction().add(R.id.map2, it).commit()
                }
            mapFragment.getMapAsync(this)
        }
        if (isPermitted()) {
            startProcess()
        } else {
            ActivityCompat.requestPermissions(this, permissions, permissionrequest)
        }
        title = "map"
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient("8eo4a3qdn1")



        binding.searchAddress.setOnClickListener {  // 주소를 등록하기 위한 검색 API 사용 버튼
            startActivity(
                Intent(this, SearchPage::class.java)
            )
        }

        binding.registrationBtn.setOnClickListener {
            if(binding.storeAddress.text.toString().equals("")
                || binding.storeCorpNumEdit.text.toString().equals("") ){
                Toast.makeText(this, "식당 등록에 필요한 정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                //activName.receiveData()
                AddressString = binding.storeAddress.toString()
                CorpNumString = binding.storeCorpNumEdit.toString()

                db.collection("rest_Info").document("${store_registration.get("longitude_x")}_${store_registration.get("latitude_y")}")
                .set(store_registration)

                val arr = "${store_registration.get("longitude_x")}_${store_registration.get("latitude_y")}"

                val data = hashMapOf("x_y" to arr)

                db.collection("user").document(Firebase.auth.currentUser?.uid.toString())
                    .update("x_y",FieldValue.arrayUnion(arr))

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

    //검색 기능
//    private fun searchKeyword(place_name: String) {
//        //API설정
//        val retrofit = Retrofit.Builder()
//            .baseUrl(MapPage.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val api = retrofit.create(KakaoAPI::class.java)
//        val call = api.getSearchKeyword(MapPage.API_KEY, place_name)
//
//        call.enqueue(object : Callback<ResultSearchKeyword> {
//            //만약에 API와 통신성공시
//            override fun onResponse(
//                call: Call<ResultSearchKeyword>,
//                response: Response<ResultSearchKeyword>
//            ) {
//                Log.d("Test", "성공: ${response.raw()}")//로그찍기
//                Log.d("Test", "Body: ${response.body()}")//로그찍기
//                val x = response.body()?.documents?.get(0)?.x//x 확인 값
//                val y = response.body()?.documents?.get(0)?.y//y 확인 값
//                addItemsAndMarkers(response.body())//result 넘겨주기
//            }
//            //만약에 API와 통신실패시
//            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
//                Log.w("MainActivity", "실패 ${t.message}")
//            }
//        })
//    }

    //recyleview에 리스트랑 마커 추가
//    private fun addItemsAndMarkers(x : Double, y : Double) {
//                val marker = Marker()//마커 생성
//                marker.position = LatLng(y,x)//검색결과나오는거 마커로 찍기
//                marker.map = naverMap// 리스트 초기화
//
//                val infoWindow = InfoWindow()
//                infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(getApplication()) {
//                    override fun getText(infoWindow: InfoWindow): CharSequence {
//                        return "정보 창 내용"
//                    }
//                }
//                // infoWindow.open(marker)
//                infoWindow.position = LatLng(y,x)
//                infoWindow.open(naverMap)
//                val listener = Overlay.OnClickListener { overlay : Overlay ->
//                    if (marker.infoWindow == null){
//                        infoWindow.open(marker)
//                    }else {
//                        infoWindow.close()
//                    }
//                    true
//                }
//            }


    override fun onDestroy() {
        mbinding = null
        super.onDestroy()
    }
}