function modal_draggable(id){
    $(id).draggable({
        handle: ".modal-header"
    });
}

$(document).ready(function() {
    modal_draggable('#layer_modal');
    modal_draggable('#shape_modal');
    modal_draggable('#military_modal');

    $('.btn-overlay').tooltip()

    // 3차원 지도 도시
    var viewer = new Cesium.Viewer('map3d', {
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

    $('.btn-overlay').tooltip()
});