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
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import kr.ac.tukorea.waiter.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.FieldPosition

//import java.util.jar.Pack200

class MapPage : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener {
    val permissionrequest = 99
    private lateinit var naverMap: NaverMap
    lateinit var fusedLocationProvideClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    private lateinit var locationSource: FusedLocationSource

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK e2ff78b2e20ee43f72827e3e379c2191"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private lateinit var binding: ActivityMainBinding
    val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드

    var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  //메뉴
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu1, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_map_page)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        // 리사이클러 뷰
        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter
        // 리스트 아이템 클릭 시 해당 위치로 이동
//        listAdapter.setItemClickListener(object: ListAdapter.OnItemClickListener {
//            override fun onClick(v: View, position: Int) {
//                //val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
//                val mapPoint = naver.maps.LatLng(listItems[position].x,listItems[position].y)
//                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
//                naver.maps.Event.addListener(map,'click',function(e) {
//                marker.setPosition(e.latlng)
//                }
//                }
//            }
        listAdapter.setItemClickListener(object : ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val marker = Marker()
                marker.position = LatLng(listItems[position].y, listItems[position].x)
                val cameraUpdate =
                    CameraUpdate.scrollTo(LatLng(listItems[position].y, listItems[position].x))
                naverMap.moveCamera((cameraUpdate))
            }
        })
        searchKeyword("스타벅스")
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

    private fun searchKeyword(place_name: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)
        val call = api.getSearchKeyword(API_KEY, place_name)

        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                Log.d("Test", "성공: ${response.raw()}")
                Log.d("Test", "Body: ${response.body()}")
                val x = response.body()?.documents?.get(0)?.x
                val y = response.body()?.documents?.get(0)?.y
                println("x : ${x}")
                println("y : ${y}")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                Log.w("MainActivity", "실패 ${t.message}")
            }
        })
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
//        val locationButtonView: LocationButtonView = findViewById(R.id.locationbtn)
//       locationButtonView.setMap(naverMap)
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
        val myLocation = LatLng(location.latitude, location.longitude)
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

    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()
            val marker = Marker()
            marker.map = naverMap// 리스트 초기화
            marker.map = null// 지도의 마커 모두 제거
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = ListLayout(
                    document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble()
                )
                listItems.add(item)

                // 지도에 마커 추가
                //  val
                //  point.apply {
                //       itemName = document.place_name
                //       mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                //           document.x.toDouble())
                //        markerType = MapPOIItem.MarkerType.BluePin
                //          selectedMarkerType = MapPOIItem.MarkerType.RedPin
            }
            //        binding.mapView.addPOIItem(point)
            listAdapter.notifyDataSetChanged()

            //  binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            // binding.btnPrevPage.isEnabled = pageNumber != 1             // 1페이지가 아닐 경우 이전 버튼 활성화

        } else
        {
            // 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
}
