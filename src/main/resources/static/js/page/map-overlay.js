function modal_draggable(id){
    $(id).draggable({
        handle: ".modal-header"
    });
}

$(document).ready(function() {
    // 지도 타일 레이어
    var osmLayer = new ol.layer.Tile({
        source: new ol.source.OSM()
    });

    // [경도(북위이면 양수), 위도(동경이면 양수)] EPSG 는 WGS84 체계로 사용하는 경도와 위도의 상수.
    var seoul = ol.proj.transform([127.0016985, 37.5642135], 'EPSG:4326', 'EPSG:3857');

    // view 는 보여지게 될 도시와 확대 여부를 설정함.
    var view = new ol.View({
        center: seoul,
        zoom: 5
    });

    // 화면에 보여지는 Map 개체 생성.
    var map = new ol.Map({
        target: 'map'
    });

    // 지도 타일 레이어 추가.
    map.addLayer(osmLayer);

    // 보여지게 될 설정을 추가.
    map.setView(view);

    modal_draggable('#layer_modal');
    modal_draggable('#shape_modal');
    modal_draggable('#military_modal');
});