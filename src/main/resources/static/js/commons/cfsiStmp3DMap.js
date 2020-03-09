var ji3DMap = function ji3DMap(options) {
    this.map = this.init(options);

    this.map.scene.globe = new Cesium.Globe(Cesium.Ellipsoid.WGS84);

    // this.setBaseTerrain();

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
};

ji3DMap.prototype.init = function init(options) {
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
        }
    });

    if(stmp.mapSource!==''&& stmp.mapSource!=='world:truemarble')
    {
        stmp.changeBaseMap(stmp.mapSource);
    }
};

/**
 * TODO 추후 변경 필요
 */
ji3DMap.prototype.setBaseTerrain = function setBaseTerrain() {
    this.map.scene.globe.terrainProvider = new Cesium.GeoserverTerrainProvider({
        service : 'WMS',
        url : 'http://' + stmp.SERVER_DOMAIN + ':' + stmp.SERVER_MAP_PORT + '/geoserver/wms',
        layerName : 'world:dted2048',
        styleName : 'mySLD',
        waterMask : false,
        heightMapWidth : 64,
        heightMapHeight : 64,
        offset : 0,
        highest : 12000,
        lowest : -5000,
        hasStyledImage : true,
        maxLevel : 13,
        formatImage : {
            format : "image/png",
            extension : "png"
        },
        formatArray : {
            format : "image/bil",
            postProcessArray : function(bufferIn, size, highest,
                                        lowest, offset) {
                var resultat;
                var viewerIn = new DataView(bufferIn);
                var littleEndianBuffer = new ArrayBuffer(size.height
                    * size.width * 2);
                var viewerOut = new DataView(littleEndianBuffer);
                if (littleEndianBuffer.byteLength === bufferIn.byteLength) {
                    // time to switch bytes!!
                    var temp, goodCell = 0, somme = 0;
                    for (var i = 0; i < littleEndianBuffer.byteLength; i += 2) {
                        temp = viewerIn.getInt16(i, false) - offset;
                        if (temp > lowest && temp < highest) {
                            viewerOut.setInt16(i, temp, true);
                            somme += temp;
                            goodCell++;
                        } else {
                            var val = (goodCell === 0 ? 1 : somme
                                / goodCell);
                            viewerOut.setInt16(i, val, true);
                        }
                    }
                    resultat = new Int16Array(littleEndianBuffer);
                }
                return resultat;
            }
        }
    });
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
        url : 'http://' + stmp.SERVER_DOMAIN + ':' + stmp.SERVER_MAP_PORT + '/service',
        layers : stmp.getBaseMapSource(),
        parameters : {
            transparent : 'true',
            format : 'image/jpeg',
            version : '1.1.1'
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
        url : 'http://' + stmp.SERVER_DOMAIN + ':' + stmp.SERVER_MAP_PORT + '/geoserver/wms',
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
        destination : Cesium.Rectangle.fromDegrees(extents.west,
            extents.south, extents.east, extents.north)
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
    var threeObjYn = false; //3차원 객체여부
    var pickedFeature =  stmp.mapObject.map.scene.pick(movement.position);
    if(pickedFeature == null){
        //20200131 jmk 추가
        if(_moveYn){    //적부대위치이동
            var cartesian = stmp.mapObject.map.camera.pickEllipsoid(movement.position, stmp.mapObject.map.scene.globe.ellipsoid);
            _featureObj.id._position._value.x = cartesian.x;
            _featureObj.id._position._value.y = cartesian.y;
            _featureObj.id._position._value.z = cartesian.z;
            _moveYn = false;
            var movePos = stmp.mapObject.project(movement.position);
            _scwin3DMap.updateDitemPos(movePos.lon,movePos.lat);
        } else {
            flt_rightClickLayer.hide();
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
};
/**
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