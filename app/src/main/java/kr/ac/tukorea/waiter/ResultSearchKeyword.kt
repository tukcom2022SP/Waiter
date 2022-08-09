data class ResultSearchKeyword(
    var meta: PlaceMeta,
    var documents: List<Place>
)
data class PlaceMeta(
    var total_count: Int,
    var page_count: Int,
    var is_end:Boolean,
    var same_name: RegionInfo
)
data class RegionInfo(
    var region: List<String>,
    var keyword: String,
    var selected_region: String
)
data class Place(
    var id: String,
    var place_name: String,
    var phone: String,                  // 전화번호
    var address_name: String,           // 전체 지번 주소
    var road_address_name: String,      // 전체 도로명 주소
    var x: String,                      // X 좌표값 혹은 longitude
    var y: String,                      // Y 좌표값 혹은 latitude
    var place_url: String,              // 장소 상세페이지 URL
    var distanc: String
)