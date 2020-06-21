'use strict';

// cesium 3D
var ji3DMap = function ji3DMap(options) {
    if (!(this instanceof ji3DMap)) {
        throw new Error('new 로 생성해야 함.');
    }

    this.map = this.init(options);

    this.map.scene.globe = new Cesium.Globe(Cesium.Ellipsoid.WGS84);

    this.map.scene.globe.showGroundAtmosphere = false;
    this.map.scene.globe.showWaterEffect = true;

    this.minHeight = -74777.0;
    this.maxHeight = 8777.0;
    this.shadingUniforms = {};

    this.setBaseTerrain();

    this._updateMaterial();

    this.map.scene.logarithmicDepthBuffer = false; // Black Spot 방지

    this.setBaseImagery();

    $('.cesium-widget-credits').hide();     // widget-credits div 제거

    this.map.scene.fog.enabled = false;

    if (stmp.mapExtents !== undefined) {
        this.moveBounds(stmp.mapExtents);
        stmp.mapExtents = undefined;
    } else {
        this.flyTo(126.7640322, 38.539249, 2000000);
    }

    this.moveHandler = new Cesium.ScreenSpaceEventHandler(this.map.scene.canvas);
    this.leftClickHandler = new Cesium.ScreenSpaceEventHandler(this.map.scene.canvas);
    this.rightClickHandler= new Cesium.ScreenSpaceEventHandler(this.map.scene.canvas);

    this._bindEvents();

    milSymbolLoader.init({map : this.map}, null)
};

ji3DMap.prototype.init = function init(options) {
    //Cesium.Ion.defaultAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiIwNjI4Zjc5My1iOGY4LTRiYTUtOGE1NC1mM2VkNzgyODkxOTkiLCJpZCI6MTU4MDAsInNjb3BlcyI6WyJhc3IiLCJnYyJdLCJpYXQiOjE1Njg4NTQ5ODB9.nNExhlkH4Ss0txvJrrUvR4UmIHr4IM0rPQohubzklgU";
    return new Cesium.Viewer(options.container, {
        sceneMode : Cesium.SceneMode.SCENE3D,
        timeline : false,
        animation : false,              // 시계
        baseLayerPicker : false,
        imageryProvider : false,
        selectionIndicator : false,
        geocoder : false,
        scene3DOnly : true,
        terrainShadows : Cesium.ShadowMode.ENABLED,
        //shadows : true,
        shouldAnimate : false,
        //requestRenderMode : true,   // 필요한 경우에만 화면 갱신
        skyBox : false,
        fullscreenButton : false,
        navigationHelpButton : false,   // 도움말 버튼
        homeButton : false,             // 홈 버튼
        sceneModePicker : false,
        infoBox : false,
        contextOptions : {
            webgl : {
                preserveDrawingBuffer : true
            }
        },
        terrainExaggeration : 1.0,
        navigationInstructionsInitiallyVisible : false,
        mapProjection : new Cesium.WebMercatorProjection()
    });

    if(stmp.mapSource!==''&& stmp.mapSource!=='world:truemarble')
    {
        stmp.changeBaseMap(stmp.mapSource);
    }
};

/**
 * TODO 추후 변경 필요
 * 고도 종류가 여러가지 일 경우 url 받아 오게 수정 필요
 */
ji3DMap.prototype.setBaseTerrain = function setBaseTerrain() {
    var terrainProvider = new Cesium.CesiumTerrainProvider({
        url : stmp.URL + '/tilesets/dted'
    });

    this.map.terrainProvider = terrainProvider;
    this.map.scene.terrainProvider = terrainProvider;
};

ji3DMap.prototype._updateMaterial = function _updateMaterial() {
    /*var material = Cesium.Material.fromType('ElevationRamp');
    this.shadingUniforms = material.uniforms;
    this.shadingUniforms.minimumHeight = this.minHeight;
    this.shadingUniforms.maximumHeight = this.maxHeight;
    this.shadingUniforms.image = this._getColorRamp('eightColor');    // eightColor, sixteenColor

    this.map.scene.globe.material = material;*/
};

ji3DMap.prototype.changeTerrain = function changeTerrain() {

};

ji3DMap.prototype.removeMap = function removeMap() {
    var mapLayer = this.map.imageryLayers.get(1);
    this.map.imageryLayers.remove(mapLayer);
};

ji3DMap.prototype.setBaseImagery = function setBaseImagery() {
    var imageryLayers = this.map.imageryLayers;
    imageryLayers.removeAll();
    imageryLayers.addImageryProvider(new Cesium.WebMapServiceImageryProvider({
        url : stmp.URL + '/mapproxy/service',
        layers : stmp.getBaseMapSource(),
        parameters : {
            transparent : 'true',
            format : 'image/jpeg'
        }
    }));
};

/**
 *
 */
ji3DMap.prototype.changeBaseMap = function changeBaseMap() {
    var mapLayer = this.map.imageryLayers.get(1);
    this.map.imageryLayers.remove(mapLayer);
    this.map.imageryLayers.addImageryProvider(new Cesium.WebMapServiceImageryProvider({
        url : stmp.URL + '/geoserver/wms',
        layers : stmp.getMapSource(),
        rectangle : Cesium.Rectangle.fromDegrees(105.522, 17.034, 140.487, 51.449),
        style : 'default',
        parameters : {
            service : 'WMS',
            transparent : 'TRUE',
            format : 'image/png',
            tiled : true,
            version : '1.1.1'
        }
    }));
};

/**
 * 영역 이동
 */
ji3DMap.prototype.moveBounds = function moveBounds(extents) {
    this.map.camera.flyTo({
        destination : Cesium.Rectangle.fromDegrees(extents.west, extents.south, extents.east, extents.north),
        duration : 0
    });
};

/**
 * 지도 이동
 */
ji3DMap.prototype.flyTo = function flyTo(lon, lat, height) {
    if (height === undefined) {     // 높이 값이 없을 경우 현재 높이로 이동
        height = this.map.scene.globe.ellipsoid.cartesianToCartographic(this.map.camera.position).height;
    }
    this.map.camera.flyTo({
        destination : Cesium.Cartesian3.fromDegrees(lon, lat, height)
    });
};

/**
 * 지도 영역 세팅
 */
ji3DMap.prototype.getExtents = function getExtents() {
    var rectangle = new Cesium.Rectangle();

    this.map.camera.computeViewRectangle(this.map.scene.globe.ellipsoid, rectangle);

    stmp.setMapExtents(Cesium.Math.toDegrees(rectangle.east),
        Cesium.Math.toDegrees(rectangle.north),
        Cesium.Math.toDegrees(rectangle.south),
        Cesium.Math.toDegrees(rectangle.west));
};

/**
 * 경위도 -> 화면좌표
 * @param point
 */
ji3DMap.prototype.unproject = function unproject(point) {
    var _point = Cesium.Cartesian3.fromDegrees(point.lon, point.lat);
    return Cesium.SceneTransforms.wgs84ToWindowCoordinates(this.map.scene, _point);
};

/**
 * 화면좌표 -> 경위도
 * @param point
 */
ji3DMap.prototype.project = function project(point) {
    var cartesian = this.map.camera.pickEllipsoid(point, this.map.scene.globe.ellipsoid);

    var _point = {};
    if (cartesian) {
        var cartographic = this.map.scene.globe.ellipsoid.cartesianToCartographic(cartesian);
        _point.lon = Number(Cesium.Math.toDegrees(cartographic.longitude).toFixed(6));
        _point.lat = Number(Cesium.Math.toDegrees(cartographic.latitude).toFixed(6));
    } else {
        _point.lon = 0;
        _point.lat = 0;
    }

    return _point;
};

ji3DMap.prototype.reset = function reset() {

};

/**
 * 이벤트 등록
 * @param type
 * @param fn
 */
ji3DMap.prototype.addEvent = function addEvent(type, fn) {

};

/**
 * 이벤트 제거
 * @param type
 * @param fn
 */
ji3DMap.prototype.removeEvent = function removeEvent(type, fn) {

};

ji3DMap.prototype.addFeature = function addFeature(feature) {
    var id = feature.getFeatureId();

    if (this.hasEntity(id)) {
        this._removeEntity(id);
    }

    var _entity;

    switch (feature.getTypeCd()) {
        case stmp.DRAW_TYPE_KIND.BASE_MILSYMBOL.CD :
            _entity = this._addBillboard(feature);
            break;
        case stmp.DRAW_TYPE_KIND.POINT.CD :
            _entity = this._addPoint(feature);
            break;
        case stmp.DRAW_TYPE_KIND.LINE.CD :
            _entity = this._addLine(feature);
    }
    this.map.entities.add(_entity);
};

ji3DMap.prototype.getFeature = function getFeature(id) {
    return this._getEntityById(id);
};

/**
 * entity 유무 판단
 * @private
 */
ji3DMap.prototype.hasEntity = function _hasEntity(id) {
    return this._getEntityById(id) !== undefined;
};

/**
 * entities 에서 해당 feature 정보를 가져 온다
 * @param id
 * @returns {*}
 */
ji3DMap.prototype._getEntityById = function getEntityById(id) {
    return this.map.entities.getById(id);
};

/**
 * 등록되어 있는 feature 를 제거 한다
 * @param feature
 */
ji3DMap.prototype.removeFeature = function removeFeature(feature) {
    if (this.hasEntity(feature.getFeatureId())) {
        this._removeEntity(feature.getFeatureId());
    }
};

/**
 * globalFeatures 에 있는 도시요소를 map 에 draw
 * @param featureMap
 */
ji3DMap.prototype.changeMapDrawFeatures = function changeMapDrawFeatures(featureMap) {
    for (var feature of featureMap.values()) {
        this.addFeature(feature);
    }
};

/**
 * id 값으로 entity 삭제
 * @private
 */
ji3DMap.prototype._removeEntity = function _removeEntity(id) {
    this.map.entities.removeById(id);
};

/**
 * billboard 타입 정의
 * @private
 */
ji3DMap.prototype._addBillboard = function _addBillboard(feature) {
    // billboard 정의 작업중
    // TODO 1. 고도값으로 구분 필요
    var _position;
    var _billboard = {};
    _billboard.image = feature.getImage();

    if (feature.getTypeCd() === stmp.DRAW_TYPE_KIND.BASE_MILSYMBOL.CD) {    // 기본군대부호 일 경우만 간략부호 적용
        _billboard.scale = 0.5;
        if (!stmp.getSignCnvsnLink()) {
            _billboard.scale = feature.getProperties().size;
        }
        if (stmp.getWarsblYn()) {
            _billboard.scale = stmp.getWarsblValue();
        }
    } else {
        _billboard.scale = feature.getProperties().size;
    }

    _billboard.horizontalOrigin = Cesium.HorizontalOrigin.CENTER;
    _billboard.verticalOrigin = Cesium.VerticalOrigin.BOTTOM;
    _billboard.heightReference = Cesium.HeightReference.CLAMP_TO_GROUND;
    // billboard 정의

    return new Cesium.Entity({
        position : Cesium.Cartesian3.fromDegrees(feature.getCoordinates()[0], feature.getCoordinates()[1]),
        billboard : _billboard,
        properties : feature.getProperties(),
        id : feature.getFeatureId()
    });
};

/**
 * point 타입 정의
 * @param feature
 * @private
 */
ji3DMap.prototype._addPoint = function _addPoint(feature) {

};
/**
 * Line 타입 정의
 * @param feature
 * @private
 */
ji3DMap.prototype._addLine = function _addLine(feature) {
   var _lineId = feature.getFeatureId();
   var startCoord = stmp.convert.mgrsToLonLat(feature.coordInfo.coords[0]);
   var endCoord = stmp.convert.mgrsToLonLat(feature.coordInfo.coords[1]);
   var line = new Cesium.Entity({
       polyline : {
           positions : Cesium.Cartesian3.fromDegreesArray([startCoord.lon,startCoord.lat,endCoord.lon,endCoord.lat]),
               width : 10.0,
               material : new Cesium.PolylineGlowMaterialProperty({
                   color : Cesium.Color.RED,
                   glowPower : 0.25
           }),
           clampToGround : true,
               classificationType : Cesium.ClassificationType.TERRAIN

       },
       id : _lineId,
   });
  /*  var line = new Cesium.Entity({
    polyline : {
        positions :Cesium.Cartesian3.fromDegrees([127,38,129,38]),
            width : 5,
            material : new Cesium.PolylineGlowMaterialProperty({
                color : Cesium.Color.RED,
                glowPower : 0.25
        }),
        clampToGround : true,
        classificationType : Cesium.ClassificationType.TERRAIN

    },
    id : _lineId
});*/
    return line;
};

/**
 * polyline 타입 정의
 * @param feature
 * @private
 */
ji3DMap.prototype._addPolyline = function _addPolyline(feature) {

};

/**
 * polygon 타입 정의
 * @param feature
 * @private
 */
ji3DMap.prototype._addPolygon = function _addPolygon(feature) {

};

/**
 * 3D 객체 타입 정의
 * @private
 */
ji3DMap.prototype._add3dTileset = function _add3dTileset() {

};

ji3DMap.prototype._getColorRamp = function _getColorRamp(selectedColorMode) {
    var ramp = document.createElement('canvas');
    ramp.width = 100;
    ramp.height = 1;
    var ctx = ramp.getContext('2d');
    var values;
    var grd = ctx.createLinearGradient(0, 0, 100, 0);

    if (selectedColorMode === 'sixteenColor') {
        values = [0.0, 0.012, 0.12, 0.18, 0.25, 0.31, 0.37, 0.43, 0.5, 0.56, 0.62, 0.68, 0.75, 0.80, 0.83, 1];

        grd.addColorStop(values[0], '#000075'); //Navy
        grd.addColorStop(values[1], '#f032e6'); //Magenta
        grd.addColorStop(values[2], '#911eb4'); //Purple
        grd.addColorStop(values[3], '#4363d8'); //Blue
        grd.addColorStop(values[4], '#42d4f4'); //Cyan
        grd.addColorStop(values[5], '#3cb44b'); //Green
        grd.addColorStop(values[6], '#bfef45'); //Lime
        grd.addColorStop(values[7], '#a9a9a9'); //Grey
        grd.addColorStop(values[8], '#ffe119'); //Yellow
        grd.addColorStop(values[9], '#f58231'); //Orange
        grd.addColorStop(values[10], '#e6194B'); //Red
        grd.addColorStop(values[11], '#800000'); //Maroon
        grd.addColorStop(values[12], '#9A6324'); //Brown
        grd.addColorStop(values[13], '#808000'); //Olive
        grd.addColorStop(values[14], '#27AA00'); //Dark Green
        grd.addColorStop(values[15], '#a9a9a900'); //Transparent
    } else if (selectedColorMode === 'eightColor') {
        values = [0.0, 0.045, 0.1, 0.3, 0.5, 0.7, 0.86, 1.0];

        grd.addColorStop(values[0], '#9400D3AA'); //Violet
        grd.addColorStop(values[1], '#0000FFAA'); //Blue
        grd.addColorStop(values[2], '#00FF00AA'); //Green
        grd.addColorStop(values[3], '#FFFF00AA'); //Yellow
        grd.addColorStop(values[4], '#FF7F00AA'); //orange
        grd.addColorStop(values[5], '#FF0000AA'); //Red
        grd.addColorStop(values[6], '#27AA00AA'); //Dark Green
        grd.addColorStop(values[7], '#a9a9a900'); //Transparent
    }

    ctx.fillStyle = grd;
    ctx.fillRect(0, 0, 100, 1);

    return ramp;
};

/**
 * 지도 init 시 필요한 event 바인딩 정의
 * @private
 */
ji3DMap.prototype._bindEvents = function _bindEvents() {
    this.moveHandler.setInputAction(this._mouseMoveEvent, Cesium.ScreenSpaceEventType.MOUSE_MOVE);

    this.leftClickHandler.setInputAction(this._leftClickEvent, Cesium.ScreenSpaceEventType.LEFT_CLICK);
    this.leftClickHandler.setInputAction(this._rightClickEvent, Cesium.ScreenSpaceEventType.RIGHT_CLICK);
   };

/**
 * 마우스 왼쪽 클릭 이벤트
 * @param movement
 * @private
 */
var _pickedFeature = null;
ji3DMap.prototype._leftClickEvent = function _leftClickEvent(movement) {
    var obj = stmp.mapObject
    var mode = obj._drawControlMode
    var points = obj._drawControlPoints
    if(mode == "draw_point"){
        points.push(stmp.mapObject.map.camera.pickEllipsoid(movement.position))
        obj._drawMilSymbol()
    }else if(mode == "draw_line_string"){
        var entities = stmp.mapObject.map.entities._entities._array
        var removeTargets = []
        jQuery.each(entities,function(idx, entity){
            if(entity.id && entity.id.indexOf("draw-entities-") > -1){
                removeTargets.push(entity)
            }
        })
        jQuery.each(removeTargets,function(idx, target){
            stmp.mapObject.map.entities.remove(target)
        })
        points.push(stmp.mapObject.map.camera.pickEllipsoid(movement.position))
        jQuery.each(points,function(idx, point){
            obj.map.entities.add({
                id : 'draw-entities-' + idx,
                position : point,
                point : {
                    color : Cesium.Color.RED,
                    pixelSize: 10
                }
            });
        })
        obj.map.entities.add({
            id : 'draw-entities-line',
            polyline : {
                positions : points,
                width: 5,
                material : Cesium.Color.BLUE
            }
        });
        // drawMilSymbol / _leftDoubleClickEvent
    }else{
        var threeObjYn = false; //3차원 객체여부
        var pickedFeature =  stmp.mapObject.map.scene.pick(movement.position);
        if(pickedFeature == null){
            //20200131 jmk 추가
            if(window._moveYn){    //적부대위치이동
                var cartesian = stmp.mapObject.map.camera.pickEllipsoid(movement.position, stmp.mapObject.map.scene.globe.ellipsoid);
                _featureObj.id._position._value.x = cartesian.x;
                _featureObj.id._position._value.y = cartesian.y;
                _featureObj.id._position._value.z = cartesian.z;
                window._moveYn = false;
                var movePos = stmp.mapObject.project(movement.position);
                _scwin3DMap.updateDitemPos(movePos.lon,movePos.lat);
            } else {
                //flt_rightClickLayer.hide();
            }
            return;
        }
        try{
            var propId = pickedFeature.getProperty('name');
            threeObjYn = true;
        } catch(e){
            threeObjYn = false;
        }
        if(threeObjYn){ //3차원 객체
            _pickedFeature = pickedFeature;
            stmp.openSMTAlert("/ui/CF/SI/SMT/CFSISMDitem3dObjtPopup.xml", "804","3d객체속성", "3dObjtPopup");
        } else {
            //
        }
    }
};
ji3DMap.prototype._leftDoubleClickEvent = function _leftDoubleClickEvent(movement) {
    var obj = stmp.mapObject
    obj._drawControlPoints.push(stmp.mapObject.map.camera.pickEllipsoid(movement.position))
    obj._drawMilSymbol()
}
ji3DMap.prototype._drawMilSymbol = function _drawMilSymbol() {
    var obj = stmp.mapObject
    var mode = obj._drawControlMode
    var constraint = obj._drawControlConstraint
    var points = obj._drawControlPoints
    obj.map.screenSpaceEventHandler.removeInputAction(this._leftDoubleClickEvent, Cesium.ScreenSpaceEventType.LEFT_DOUBLE_CLICK)
    if(mode == "draw_point") {
        if(constraint === "milSym"){
            obj.map.entities.add({
                position: points[0],
                billboard: {
                    image: stmp.mapObject.map._drawing_milsymbol.asCanvas().toDataURL()
                }
            })
        }else{
            var _cs = document.getElementById('SIDCCODINGSCHEME').value;
            drawMsymbol(-1, 'SVG', null, window.symStd, _cs, window.function_sidc);
            if(jQuery("#svg-draw").length == 0){
                jQuery("body").css("overflow",'hidden')
                jQuery("body").append("<div id='svg-draw'></div>")
            }
            jQuery("#svg-draw").empty()
            jQuery("#svg-draw").append(stmp.mapObject.map._drawing_milsymbol._svg_symbol.getSVG())
            html2canvas(jQuery("#svg-draw svg")[0],{backgroundColor: "rgba(0,0,0,0)"}).then(function(canvas){
                obj.map.entities.add({
                    position: points[0],
                    billboard: {
                        image: canvas.toDataURL()
                    }
                })
            })
        }
    }else if(mode == "draw_line_string") {
        var coordinates = stmp.mapObject.map._drawing_milsymbol_coordinates
        jQuery.each(points, function(idx, point){
            var wgs = Cesium.Ellipsoid.WGS84.cartesianToCartographic(point)
            var lon = Cesium.Math.toDegrees(wgs.longitude)
            var lat = Cesium.Math.toDegrees(wgs.latitude)
            coordinates.push({x:lon,y:lat})
        })
        var cs = document.getElementById('SIDCCODINGSCHEME').value;
        drawMsymbol(-1, 'geoJSON', null, symStd, cs, function_sidc)
        var geojson = stmp.mapObject.map._drawing_milsymbol._geojson
        var data = JSON.parse(geojson)
        //console.log(data)
        var dataSource = Cesium.GeoJsonDataSource.load(data)
        obj.map.dataSources.add(dataSource)
        //obj.map.zoomTo(dataSource)
    }
    obj._drawControlMode = null
    obj._drawControlPoints = []
    if(mode == "draw_line_string") {
        var entities = stmp.mapObject.map.entities._entities._array
        var removeTargets = []
        jQuery.each(entities, function (idx, entity) {
            if (entity.id && entity.id.indexOf("draw-entities-") > -1) {
                removeTargets.push(entity)
            }
        })
        console.log(removeTargets)
        jQuery.each(removeTargets, function (idx, target) {
            stmp.mapObject.map.entities.remove(target)
        })
    }
}
/**_drawControlMode
 * 마우스 오른쪽 클릭 이벤트
 * @param movement
 * @private
 */
ji3DMap.prototype._rightClickEvent = function _rightClickEvent(movement) {
   var pickedFeature =  stmp.mapObject.map.scene.pick(movement.position);
    stmp.addREvnet(pickedFeature,movement);

};

/**
 * 마우스 이동 이벤트
 * 1. 보조자료 전시
 * @private
 */
ji3DMap.prototype._mouseMoveEvent = function _mouseMoveEvent(movement) {
    var cartesian = stmp.mapObject.map.camera.pickEllipsoid(movement.endPosition,
        stmp.mapObject.map.scene.globe.ellipsoid);
    if (cartesian) {
        var cartographic = stmp.mapObject.map.scene.globe
            .ellipsoid.cartesianToCartographic(cartesian);
        cartographic.height = stmp.mapObject.map.scene.globe
            .getHeight(cartographic);
        var longitude = Cesium.Math.toDegrees(cartographic.longitude);
        var latitude = Cesium.Math.toDegrees(cartographic.latitude);

        var height = '고도값 : ' + Math.round(cartographic.height) + 'm';

        stmp.displaySupportInfo(longitude, latitude, height);
    }
};
ji3DMap.prototype.drawControlMode = function drawControlMode(mode,constraint) {
    this._drawControlMode = mode
    this._drawControlConstraint = constraint
    this._drawControlPoints = []
    this.map.screenSpaceEventHandler.removeInputAction(this._leftDoubleClickEvent, Cesium.ScreenSpaceEventType.LEFT_DOUBLE_CLICK)
    if(mode == "draw_line_string") {
        this.map.screenSpaceEventHandler.setInputAction(this._leftDoubleClickEvent, Cesium.ScreenSpaceEventType.LEFT_DOUBLE_CLICK)
    }
};
