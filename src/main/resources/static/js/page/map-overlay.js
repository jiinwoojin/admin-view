function modal_draggable(id){
    $(id).draggable({
        handle: ".modal-header"
    });
}

$(document).ready(function() {
    // 2차원 지도 도시
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
        target: 'map2D'
    });

    // 지도 타일 레이어 추가.
    map.addLayer(osmLayer);

    // 보여지게 될 설정을 추가.
    map.setView(view);

    // 3차원 지도 도시
    var viewer = new Cesium.Viewer('map3D', {
        sceneMode: Cesium.SceneMode.SCENE3D,
        timeline: false,
        animation: false,
        infoBox: false,
        scene3DOnly : true,
        baseLayerPicker : false,
        requestRenderMode : true,
        geocoder : false,
        contextOptions : {
            webgl : {
                preserveDrawingBuffer : true
            }
        },
        navigationHelpButton : false,
        selectionIndicator : false,
        navigationInstructionsInitiallyVisible : false,
        fullscreenButton : false,
        homeButton : false
    });

    viewer.scene.globe = new Cesium.Globe(Cesium.Ellipsoid.WGS84);

    viewer.scene.logarithmicDepthBuffer = false;

    viewer.scene.globe.enableLighting = true;

    $('.cesium-widget-credits').hide();

    /*var osmImageryProvider = Cesium.createOpenStreetMapImageryProvider();
    viewer.imageryLayers.addImageryProvider(osmImageryProvider);*/

    var osm = new Cesium.OpenStreetMapImageryProvider({
        url : 'http://c.tile.stamen.com/watercolor/'
    });

    viewer.imageryLayers.addImageryProvider(osm);

    viewer.camera.flyTo({
        destination : Cesium.Cartesian3.fromDegrees(127.0, 27.0, 1000000),
        orientation : {
            heading : Cesium.Math.toRadians(0.0),
            pitch : Cesium.Math.toRadians(-40.0),
            roll : 0.0
        }
    });

    modal_draggable('#layer_modal');
    modal_draggable('#shape_modal');
    modal_draggable('#military_modal');
});
