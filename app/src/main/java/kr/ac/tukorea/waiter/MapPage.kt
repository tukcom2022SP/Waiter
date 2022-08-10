package kr.ac.tukorea.waiter


import ResultSearchKeyword
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import kr.ac.tukorea.waiter.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_map_page.*
import kr.ac.tukorea.waiter.databinding.ActivityMapPageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapPage : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private var auth : FirebaseAuth? = null
    private lateinit var binding: ActivityMapPageBinding
    var db: FirebaseFirestore = Firebase.firestore
    var counter = 0
    val infoWindow = InfoWindow()
    lateinit var fusedLocationProvideClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    private lateinit var locationSource: FusedLocationSource
    var phoneNum = "" // 유저 핸드폰 번호
    var userName = "" // 유저 이름

    companion object {
        val permissionrequest = 99
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 2f8e49e7fefd85e3d4c11dc88ca0a8fd"
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        var permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  //메뉴
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu1, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        binding = ActivityMapPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnSearch.setOnClickListener {
            val SearchIntent = Intent(this, Waiter_Search::class.java)
            startActivity(SearchIntent)
        }
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        listAdapter.setItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val marker = Marker()
                marker.position = LatLng(listItems[position].y, listItems[position].x)
                marker.map =naverMap
                val cameraUpdate =
                    CameraUpdate.scrollTo(LatLng(listItems[position].y, listItems[position].x))
                naverMap.moveCamera((cameraUpdate))
            }
        })
        binding.reservationBtn.setOnClickListener {

            var reservationMap = hashMapOf(
                "userName" to userName,
                "phoneNum" to phoneNum,
                "customerNum" to 100
            )


            db.collection("rest_Info").document("126.484480056159_33.5124867330564")
                .get().addOnSuccessListener {
                    if (it.exists()){
                        counter = it.get("counter").toString().toInt()
                        counter+=1
                    }
                }

            db.collection("rest_info").document("126.484480056159_33.5124867330564")
                .update("counter", counter.toString().toInt())

            db.collection("rest_Info").document("126.484480056159_33.5124867330564")
                .collection("reservation").document(counter.toString())
                .set(reservationMap)
        }


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
            val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    fm.beginTransaction().add(R.id.map, it).commit()
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
        return
    }
    //검색 기능
    private fun searchKeyword(place_name: String) {
        //API설정
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)
        val call = api.getSearchKeyword(API_KEY, place_name)

        call.enqueue(object : Callback<ResultSearchKeyword> {
            //만약에 API와 통신성공시
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                Log.d("Test", "성공: ${response.raw()}")//로그찍기
                Log.d("Test", "Body: ${response.body()}")//로그찍기
                val x = response.body()?.documents?.get(0)?.x//x 확인 값
                val y = response.body()?.documents?.get(0)?.y//y 확인 값
                addItemsAndMarkers(response.body())//result 넘겨주기
            }
            //만약에 API와 통신실패시
            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                Log.w("MainActivity", "실패 ${t.message}")
            }
        })
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
        if (intent.hasExtra("name")) {
            phoneNum = intent.getStringExtra("phone").toString()
            userName = intent.getStringExtra("name").toString()
            var exam = intent.getParcelableExtra<Exam>("examKey")
            Log.d("로그확인", "phoneNum")
            if (exam != null) {
                findPlace(exam)
                val marker = Marker()//마커 생성
                marker.position =
                    LatLng(exam.y.toString().toDouble(),exam.x.toString().toDouble())//검색결과나오는거 마커로 찍기
                marker.map = naverMap// 리스트 초기화
                val cameraUpdate =
                    CameraUpdate.scrollTo(LatLng(exam.y.toString().toDouble(), exam.x.toString().toDouble()))
                naverMap.moveCamera((cameraUpdate))

            }
        }
        else{
            Log.d("로그확인실패","phoneNum")
            Toast.makeText(this, "검색 exam 키가 없습니다", Toast.LENGTH_SHORT).show()
        }
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

    fun setLastLocation(location: Location) {
        //내 현 위치 찍어주기
        val myLocation = LatLng(location.latitude, location.longitude)

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



    fun findPlace(exam: Exam){
        var posX = ""
        var posY = ""

        Log.d("로그확인exam","${exam}")
            restName.text = exam?.name
            restAddres.text = exam?.address
            restRoad.text = exam?.road
            restX.text = exam?.x
            restY.text = exam?.y
            posX = exam?.x.toString()
            posY = exam?.y.toString()
    }


    //    override fun onClick(overlay: Overlay): Boolean {
//        //마커 클릭하면 mainactivity 넘어가기
//        if (overlay is Marker) {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//        return true
//    }
    //recyleview에 리스트랑 마커 추가
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()
            Log.d("로그","${searchResult}")//로그 찍기
            for (document in searchResult!!.documents)
            // 해당 결과들이 documents 에 있으면
            {
                // 결과를 리사이클러 뷰에 추가
                val item = ListLayout(
                    document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.phone,
                    document.x.toDouble(),
                    document.y.toDouble()
                )
                listItems.add(item)//item에 있는내용 list로 넘기기
                listAdapter.notifyDataSetChanged()//listadapter에 변경사항 알리기
                val marker = Marker()//마커 생성
                marker.position = LatLng(document.y.toDouble(),document.x.toDouble())//검색결과나오는거 마커로 찍기
                marker.map = naverMap// 리스트 초기화
                Log.d("로그1","${item}")//로그찍어보기
//                     infoWindow.adapter = object: InfoWindow.DefaultTextAdapter(application){
//                         override fun get(infoWindow: InfoWindow): CharSequence{
//                             return ""
//                         }
//                     }
//
                val infoWindow = InfoWindow()
                infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(getApplication()) {
                    override fun getText(infoWindow: InfoWindow): CharSequence {
                        return "정보 창 내용"
                    }
                }
                // infoWindow.open(marker)
                infoWindow.position = LatLng(document.y.toDouble(),document.x.toDouble())
                infoWindow.open(naverMap)
                val listener = Overlay.OnClickListener { overlay :Overlay->
                    if (marker.infoWindow == null){
                        infoWindow.open(marker)
                    }else {
                        infoWindow.close()
                    }
                    true
                }
            }
        }
        else
        {
            // 검색 결과가 없을 때 toast 메세지
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
}
