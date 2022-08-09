package kr.ac.tukorea.waiter


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_map_page.*
import kr.ac.tukorea.waiter.databinding.ActivityMapPageBinding

//,Overlay.OnClickListener
class MapPage : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    companion object {
        val permissionrequest = 99
        val infoWindow = InfoWindow()
        lateinit var fusedLocationProvideClient: FusedLocationProviderClient
        lateinit var locationCallback: LocationCallback
        private lateinit var locationSource: FusedLocationSource
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 2f8e49e7fefd85e3d4c11dc88ca0a8fd"
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private lateinit var binding: ActivityMapPageBinding
        val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
        val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터
        private var pageNumber = 1      // 검색 페이지 번호
        private var keyword = ""        // 검색 키워드
        var permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {  //메뉴
        super.onCreateOptionsMenu(menu)
        var mInflater = menuInflater
        mInflater.inflate(R.menu.menu1, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_map_page)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        binding = ActivityMapPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 리사이클러 뷰
//        binding.rvList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        binding.rvList.adapter = listAdapter

//        listAdapter.setItemClickListener(object : ListAdapter.OnItemClickListener {
//            override fun onClick(v: View, position: Int) {
////                val marker = Marker()
////                marker.position = LatLng(listItems[position].y, listItems[position].x)
////                marker.map =naverMap
//                val cameraUpdate =
//                    CameraUpdate.scrollTo(LatLng(listItems[position].y, listItems[position].x))
//                naverMap.moveCamera((cameraUpdate))
//            }
//        })
//        binding.btnSearch.setOnClickListener {
//            keyword = binding.searchText.text.toString()
//            pageNumber = 1
//            searchKeyword(keyword)
//        }
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
        findPlace()
    }
    fun findPlace(){
        var posX = ""
        var posY = ""
        if (intent.hasExtra("examKey")) {
            var exam = intent.getParcelableExtra<Exam>("examKey")
            restName.text = exam?.name
            restAddres.text = exam?.address
            restRoad.text = exam?.road
            restX.text = exam?.x
            restY.text = exam?.y
            posX = exam?.x.toString()
            posY = exam?.y.toString()
        }
        else{
            Toast.makeText(this, "검색 exam 키가 없습니다", Toast.LENGTH_SHORT).show()
        }
        val marker = Marker()//마커 생성
        marker.position =
            LatLng(posY.toDouble(),posX.toDouble())//검색결과나오는거 마커로 찍기
        marker.map = naverMap// 리스트 초기화
        val cameraUpdate =
            CameraUpdate.scrollTo(LatLng(posY.toDouble(), posX.toDouble()))
        naverMap.moveCamera((cameraUpdate))

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
//    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
//        if (!searchResult?.documents.isNullOrEmpty()) {
//            // 검색 결과 있음
//            var posx = ""
//            var posy = ""
//           posx = document.x
//                posy = document.y
//                val x = item.x
//                val y = item.y
//                val address = item.address
//                val rd = item.road
//                     infoWindow.adapter = object: InfoWindow.DefaultTextAdapter(application){
//                         override fun get(infoWindow: InfoWindow): CharSequence{
//                             return ""
//                         }
//                     }
//
//            }
//            val marker = Marker()//마커 생성
//            marker.position =
//                LatLng(posy.toDouble(),posx.toDouble())//검색결과나오는거 마커로 찍기
//            marker.map = naverMap// 리스트 초기화
//            val infoWindow = InfoWindow()
//            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(getApplication()) {
//                override fun getText(infoWindow: InfoWindow): CharSequence {
//                    return "정보 창 내용"
//                }
//            }
//            // infoWindow.open(marker)
//            infoWindow.position = LatLng(posy.toDouble(), posx.toDouble())
//            infoWindow.open(naverMap)
//            val listener = Overlay.OnClickListener { overlay: Overlay ->
//                if (marker.infoWindow == null) {
//                    infoWindow.open(marker)
//                } else {
//                    infoWindow.close()
//                }
//                true
//            }
//        }
//        else
//        {
//            // 검색 결과가 없을 때 toast 메세지
//            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
//        }
//    }

}
