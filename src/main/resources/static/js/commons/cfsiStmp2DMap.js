'use strict';

// mapbox gl js 2D
var jiMap = function jiMap(options) {
    if (!(this instanceof jiMap)) {
        throw new Error('new 로 생성해야 함.');
    }

    if (stmp.valid.checkValue(stmp.mapExtents)) {
        options.bounds = [stmp.mapExtents.west, stmp.mapExtents.south,
            stmp.mapExtents.east, stmp.mapExtents.north];
        stmp.mapExtents = undefined;
    } else {
        options.center = stmp.valid.defaultValue(options.center, [128, 37]);
    }

    stmp.MINI_MAP = stmp.valid.defaultValue(options.miniMap, false);

    this.map = this.init(options);

    if (!stmp.MINI_MAP) {
        // 지도 로드 완료 시
        this.map.on('load', function(evt) {
            if (stmp.PRESENT_MAP_KIND == stmp.MAP_KIND.MAP_2D) {
                // 맵박스
                stmp.mapObject.setZoomRate(10);             // wheel rate 설정
                stmp.mapObject.setWheelZoomRate(1);         // wheel rate 설정
                stmp.mapObject._bindEvents();
                console.log('2DMap 생성 완료.');
            }
            //
            milSymbolLoader.init({map : evt.target},function(){
                /**
                 * callback 함수가 있을 경우 호출
                 */
                if (stmp.valid.checkValue(options.initFn)) {
                    options.initFn()
                }
            });
        });
        /* GRID area */
        // 그리드 라벨 redraw
        this.map.on('moveend', stmp.graticulesLabelGenerator)
        /* GRID area */
    }
};

/**
 * 기본 스타일 정의
 */
jiMap.prototype.setBaseStyle = function setBaseStyle() {
    return {
        'version' : 8,
        'sources' : {
            'world' : {
                'type' : 'raster',
                'tiles' : [
                    stmp.URL +  "/mapproxy/service?bbox={bbox-epsg-3857}&format=image/png&service=WMS&version=1.1.1&request=GetMap&srs=EPSG:3857&crs=EPSG:3857&styles&transparent=true&width=256&height=256&layers=" + stmp.BASE_MAP_SOURCE,
                    //stmp.PROTOCOL + "//211.172.246.71:11130/mapserver/cgi-bin/mapserv?map=/data/jiapp/data_dir/style/basemaps/osm-google.map&bbox={bbox-epsg-3857}&format=image/png&service=WMS&version=1.1.1&request=GetMap&srs=EPSG:3857&crs=EPSG:3857&styles&transparent=true&width=256&height=256&layers=default"
                    //stmp.PROTOCOL + "//211.172.246.71:11190/osm_tiles/{z}/{x}/{y}.png"
                ],
                'tileSize' : 256
            }
        },
        'sprite' : stmp.URL + '/GSymbol/GSSSymbol',
        'glyphs' : stmp.URL + '/fonts/{fontstack}/{range}.pbf',
        'layers' : [{
            'id' : stmp.BASE_MAP_LAYER_ID,
            'type' : 'raster',
            'source' : 'world',
            'minzoom' : 0,
            'maxzoom' : 22
        }]
    };
};

/**
 * 맵 객체를 초기화 한다.
 */
jiMap.prototype.init = function init(options) {
    //mapboxgl.accessToken = 'pk.eyJ1IjoibmV1dHRpIiwiYSI6ImNqeG8xZTI0ejAyN2MzZ3IycmprMGozMTgifQ.COExEgQEp4_q926PX_Qu2w';
    return new mapboxgl.Map({
        container : options.container,
        //style : this.setBaseStyle(),
        style : CONTEXT + '/style/kr_style.json',
        center : options.center || [0, 0],
        bounds : options.bounds || undefined,
        zoom : options.zoom || 10,
        maxZoom : options.maxZoom || 20,
        minZoom : options.minZoom || 0,
        preserveDrawingBuffer : true,
        dragRotate : options.dragRotate === undefined ? true : options.dragRotate,
        dragPan : options.dragPan === undefined ? true : options.dragPan,
        doubleClickZoom : options.doubleClickZoom === undefined ? true : options.doubleClickZoom,
        boxZoom : options.boxZoom === undefined ? true : options.boxZoom,
        keyboard : options.keyboard === undefined ? true : options.keyboard
    });
};

jiMap.prototype.changeBaseMap = function changeBaseMap() {
    if(stmp.mapObject.map.getLayer('selectMap')) {
        stmp.mapObject.map.removeLayer('selectMap');
        stmp.mapObject.map.removeSource('selectMap');
    }

    stmp.mapObject.map.addLayer(
        {
            'id' : 'selectMap',
            'type' : 'raster',
            'source' : {
                'type' : 'raster',
                'tiles' : [
                    stmp.PROTOCOL + '//' + stmp.SERVER_DOMAIN + ':' + stmp.SERVER_MAP_PORT+
                    '/geoserver/gwc/service/wms?bbox={bbox-epsg-3857}&format=image/png&service=WMS&version=1.1.1&request=GetMap&srs=EPSG:3857&transparent=true&width=256&height=256&layers=' + stmp.getMapSource()],
                'tileSize' : 256
            }
        }
    );

};

jiMap.prototype.removeMap = function removeMap() {

    if(stmp.mapObject.map.getLayer('selectMap')) {
        stmp.mapObject.map.removeLayer('selectMap');
        stmp.mapObject.map.removeSource('selectMap');
    }
};

/**
 * Wheel Zoom 수준을 설정
 * @param value
 */
jiMap.prototype.setWheelZoomRate = function setWheelZoomRate(value) {
    this.map.scrollZoom.setWheelZoomRate(value);
};

/**
 * Zoom 수준을 설정
 * @param value
 */
jiMap.prototype.setZoomRate = function setZoomRate(value) {
    this.map.scrollZoom.setZoomRate(value);
};

/**
 * 중심 좌표 이동
 */
jiMap.prototype.setCenter = function setCenter(coord) {
    this.map.setCenter(coord);
};

/**
 * 중심 좌표 반환
 * @returns {*|LngLat|{x, y}}
 */
jiMap.prototype.getCenter = function getCenter() {
    var center = this.map.getCenter();

    return new geoTrans.LatLon(center.lng, center.lat);
};

/**
 * ZOOM 이동
 */
jiMap.prototype.setZoom = function setZoom(zoom) {
    this.map.setZoom(parseInt(zoom));
};

jiMap.prototype.getZoom = function getZoom() {
    return this.map.getZoom();
};

/**
 * 중심 및 ZOOM 이동
 */
jiMap.prototype.move = function move(params) {
    this.setCenter(params.coord);
    this.setZoom(params.zoom);
};

jiMap.prototype.setFilter = function setFilter() {

};

jiMap.prototype.getFilter = function getFilter() {

};

jiMap.prototype.setPaintProperty = function setPaintProperty() {

};

jiMap.prototype.getPaintProperty = function getPaintProperty() {

};

jiMap.prototype.setLayoutProperty = function setLayoutProperty(layerId, name, prop) {
    this.map.setLayoutProperty(layerId, name, prop);
};

jiMap.prototype.getLayoutProperty = function getLayoutProperty() {

};

/**
 * 영역 이동
 */
jiMap.prototype.moveBounds = function moveBounds(extents) {
    if (extents.length !== 4) {
        var bounds = [extents.west, extents.south, extents.east, extents.north];
        this.map.fitBounds(bounds);
    } else {
        this.map.fitBounds(extents);
    }
};

jiMap.prototype.setStyle = function setStyle(style) {
    return this.map.setStyle(style);
};

jiMap.prototype.getStyle = function getStyle() {
    return this.map.getStyle();
};

/**
 * Source 를 등록 한다
 */
jiMap.prototype.addSource = function addSource(id, object) {
    this.map.addSource(id, object);
};

/**
 * Source 를 삭제 한다
 */
jiMap.prototype.removeSource = function removeSource(id) {
    this.map.removeSource(id);
};

/**
 * Layer 를 등록 한다
 */
jiMap.prototype.addLayer = function addLayer(feature) {
    if (!stmp.getLayerList2d().containsKey(feature.getLayerId())) {
        stmp.setLayerList2d(feature);
        this.map.addLayer(this._setLayer(feature));
    }
};

/**
 * Layer 를 삭제 한다
 */
jiMap.prototype.removeLayer = function removeLayer(id) {
    if (stmp.getLayerList2d().containsKey(id)) {
        stmp.removeLayerList2d(id);
        this.map.removeLayer(id);
    }
};

/**
 * 현재 지도 bounds 정보를 stmp 의 mapExtents 에 입력한다.
 */
jiMap.prototype.getExtents = function getExtents() {
    var bounds = this.map.getBounds();
    stmp.setMapExtents(bounds.getEast(), bounds.getNorth(), bounds.getSouth(), bounds.getWest());
};

/**
 * 필요 시 구현
 * @param params
 */
jiMap.prototype.drawBaseSymbol = function drawBaseSymbol(params) {

};

/**
 * 필요 시 구현
 * @param params
 */
jiMap.prototype.drawPoint = function drawPoint(params) {

};

/**
 * 필요 시 구현
 * @param params
 */
jiMap.prototype.drawLine = function drawLine(params) {

};

/**
 * 필요 시 구현
 * @param params
 */
jiMap.prototype.drawPolygon = function drawPolygon(params) {

};

/**
 * 화면좌표 -> 경위도
 * @param point
 */
jiMap.prototype.unproject = function unproject(point) {
    console.log("point>>"+point);
    var _point = [point.x, point.y];

    return this.map.unproject(_point);
};

/**
 * 경위도 -> 화면좌표
 * @param point
 */
jiMap.prototype.project = function project(point) {
    var _point = [point.lon, point.lat];
    return this.map.project(_point);
};

/**
 * 북쪽이 위로 향하도록 지도를 회전
 */
jiMap.prototype.resetNorth = function resetNorth() {
    this.map.resetNorth();
};

/**
 * 북쪽이 위로 향하도록 지도를 회전
 * pitch 을 0 으로 조절
 */
jiMap.prototype.resetNorthPitch = function resetNorthPitch() {
    this.map.resetNorthPitch();
};

jiMap.prototype.addImage = function addImage(id, image) {
    this.map.addImage(id, image);
};

jiMap.prototype.updateImage = function updateImage(id, image) {
    this.map.updateImage(id, image);
};

jiMap.prototype.hasImage = function hasImage(id) {
    return this.map.hasImage(id);
};

jiMap.prototype.addEvent = function addEvent(type, fn) {
    this.map.on(type, fn);
};

jiMap.prototype.removeEvent = function removeEvent(type, fn) {
    this.map.off(type, fn);
};

jiMap.prototype.getSource = function getSource(id) {
    return this.map.getSource(id);
};

jiMap.prototype.addFeature = function addFeature(feature) {

    if (!(feature instanceof jiFeature)) {
        throw new Error('다른 객체임.');
    }

    var _id = feature.getId();
    var _stmpLayerId = feature.getStmpLayerId();
    if (!this.getSource(_stmpLayerId)) {
        this._createGeoJsonSource(_stmpLayerId);
    }

    if (!this._queryGeoJsonFeature(_stmpLayerId, _id)) {
        if (!this._searchGeoJsonFeature(_stmpLayerId, _id)) {
            this._addGeoJsonData(feature);
        } else {
            this._modifyGeoJsonData(feature);
        }
    } else {
        this._modifyGeoJsonData(feature);
    }

    if (feature.getImage()) {
        this._addImage(feature);
    } else {
        this.addLayer(feature);
    }
};


jiMap.prototype.getFeature = function getFeature(feature) {
    // source 체크
    // id 로 객체 체크
    // 해당 객체를 geojson 형태로 반환
};

jiMap.prototype.reset = function reset() {

};

/**
 * 객체 삭제
 * @param feature
 */
jiMap.prototype.removeFeature = function removeFeature(feature) {
    // stmpLayerId 로 해당 source 를 찾는다
    // id 로 해당 객체를 찾는다 _queryGeoJsonFeature, _getGeoJsonFeature 사용
    // features 배열에서 해당 id 의 값을 제거 한다
    // 이미지가 등록 되어 있을 경우 이미지 제거
    // 결과를 리턴 한다

    var featureIndex = this._getFeatureIndex(feature.getStmpLayerId(), feature.getId());

    console.log(featureIndex);

    // source 에서 제거
    if (featureIndex > -1) {
        this._removeFeature(feature.getStmpLayerId(), featureIndex);
        if (this.map.hasImage(feature.getImageId())) {
            this._removeImage(feature.getImageId());
        }
    }
};

jiMap.prototype.removeFeatures = function removeFeatures(features) {
    // for 돌면서 removeFeature 호출
};

jiMap.prototype._getFeatureIndex = function _getFeatureIndex(stmpLayerId, id) {
    var sourceList = this._getSourceDataList(stmpLayerId);

    return sourceList.findIndex(function(item) {
        return item.id === id
    });
};

jiMap.prototype._removeFeature = function _removeFeature(stmpLayerId, index) {
    var sourceJson = this.getSource(stmpLayerId).serialize().data;

    sourceJson.features.splice(index, 1);

    this._setGeoJsonData(stmpLayerId, sourceJson);
};

jiMap.prototype._removeImage = function _removeImage(id) {
    this.map.removeImage(id);
};

jiMap.prototype._getSourceDataList = function _getSourceDataList(stmpLayerId) {
    return this.getSource(stmpLayerId).serialize().data.features;
};

/**
 * Event 바인딩
 */
jiMap.prototype._bindEvents = function _bindEvents() {
    // 기본 마우스 이동 이벤트 정의
    this.map.on('mousemove', function(e) {
        stmp.displaySupportInfo(e.lngLat.lng, e.lngLat.lat);    // 보조자료 전시
    });

    this.map.on('zoomend', function(e) {
        console.log(e);
        console.log(stmp.mapObject.map.getZoom());
        // zoom 레벨에 따라 지도 변환
        // 육도일 경우 육도로만 전환
        // 14LV : 2.5만
        // 13LV : 5만
        // 12LV : 10만
        // 11LV : 25만
        // 10LV : 50만
        // 9LV  : 100만
        // 8LV  : 200만
    });
};

/**
 * geojson source 생성
 * @param id
 * @private
 */
jiMap.prototype._createGeoJsonSource = function _createGeoJsonSource(id) {
    this.addSource(id, {
        'type' : jiConstant.MAPBOX_SOURCE_TYPE.GEOJSON,
        'data' : {
            'type' : 'FeatureCollection',
            'features' : []
        },
        'buffer' : 0
    });
};

/**
 * map 에서 사용할 image 등록
 *
 * @private
 * @param feature
 */
jiMap.prototype._addImage = function _addImage(feature) {
    var image = new Image();
    image.crossOrigin = 'anonymous';

    image.addEventListener('load', function(e) {
        if (this.width === 0 && this.height === 0) {
            if (feature.isSymbol()) {
                this.width = feature.getSymbol().width;
                this.height = feature.getSymbol().height;
            }
        }

        if (!stmp.mapObject.hasImage(feature.getImageId())) {
            stmp.mapObject.addImage(feature.getImageId(), this);
        } else {
            stmp.mapObject.updateImage(feature.getImageId(), this);
        }

        if (stmp.mapObject.getSource(feature.getStmpLayerId())) {
            stmp.mapObject.addLayer(feature);
        }
    });

    image.src = feature.getImage();
};

/**
 * 해당 source 에 해당 id 의 feature 가 있는지 여부
 * true : 객체 있음
 * false : 객체 없음
 * @private
 */
jiMap.prototype._queryGeoJsonFeature = function _queryGeoJsonFeature(stmpLayerId, id) {
    return this._getGeoJsonFeature(stmpLayerId, id).length !== 0;
};

/**
 * [] 로 반환
 * @private
 */
jiMap.prototype._getGeoJsonFeature = function _getGeoJsonFeature(stmpLayerId, id) {
    return this.map.querySourceFeatures(stmpLayerId, {
        filter : ['==', 'id', id]
    });
};

/**
 * true : 객체 있음
 * false : 객체 없음
 * @private
 */
jiMap.prototype._searchGeoJsonFeature = function _searchGeoJsonFeature(stmpLayerId, id) {
    var _geojson = this.getSource(stmpLayerId).serialize().data;

    if (_geojson.features.length === 0) {
        return false;
    } else {
        return _geojson.features.some(function(e) {
            return e.id === id;
        });
    }
};

/**
 * 해당 source 에 geojson 객체를 업로드 한다
 * @param stmpLayerId
 * @param json
 * @private
 */
jiMap.prototype._setGeoJsonData = function _setGeoJsonData(stmpLayerId, json) {
    this.getSource(stmpLayerId).setData(json);
};

/**
 * 해당 geojson 에 feature 를 추가 한다
 * @param feature
 * @private
 */
jiMap.prototype._addGeoJsonData = function _addGeoJsonData(feature) {
    var _geojson = this.getSource(feature.getStmpLayerId()).serialize().data;
    _geojson.features.push(feature.getGeoJson());

    this._setGeoJsonData(feature.getStmpLayerId(), _geojson);
};

/**
 * 해당 geojson 에 feature 룰 추가 한다
 * @param stmpLayerId
 * @param features
 * @private
 */
jiMap.prototype._addGeoJsonDatas = function _addGeoJsonDatas(stmpLayerId, features) {
    var _geojson = this.getSource(stmpLayerId).serialize().data;
    _geojson.features.push(features);
    for (var i = 0; i < features.length; i++) {
        _geojson.features.push(features[i]);
    }

    this._setGeoJsonData(stmpLayerId, _geojson);
};

/**
 * 해당 geojson 에 해당 feature 를 찾아 수정 한다
 * @param feature
 * @private
 */
jiMap.prototype._modifyGeoJsonData = function _modifyGeoJsonData(feature) {
    var _geojson = this.getSource(feature.getStmpLayerId()).serialize().data;

    var changeData = false;
    for (var i = 0; i < _geojson.features.length; i++) {
        if (feature.getId() === _geojson.features[i].id) {
            _geojson.features[i].properties = feature.getProperties();
            _geojson.features[i].geometry = feature.getGeometry();
            changeData = true;
            break;
        }
    }

    if (changeData) {
        this.getSource(feature.getStmpLayerId()).setData(_geojson);
    }
};
jiMap.prototype.getAllFeatures = function() {
    var sourceIds = Object.keys(stmp.mapObject.map.getStyle().sources)
    var features = {};
    for(var i=0; i < sourceIds.length; i++){
        var source = stmp.mapObject.map.getSource(sourceIds[i]);
        if(source._data){
            features[sourceIds[i]] = source._data.features
        }
    }
    /*
    var img = stmp.mapObject.map.style.getImage("TestLayer_BASE_MILSYMBOL_test3_SYMBOL")
    var data = img.data.data.buffer
    var blob = new self.Blob([new Uint8Array(data)], { type: 'image/png' });
    console.log(data, URL.createObjectURL(blob))
    self.createImageBitmap(blob).then(function (imgBitmap) {
        console.log(null, imgBitmap);
    }).catch(function (e) {
        callback(new Error('Could not load image because of ' + e.message + '. Please make sure to use a supported image type such as PNG or JPEG. Note that SVGs are not supported.'));
    });
    */
    return features;
};

/**
 * globalFeatures 에 있는 도시요소를 map 에 draw
 * @param featureMap
 */
jiMap.prototype.changeMapDrawFeatures = function changeMapDrawFeatures(featureMap) {
    for (var feature of featureMap.values()) {
        this.addFeature(feature);
    }
};

/**
 * 스타일에 사용할 layer 생성
 * @param feature
 * @private
 */
jiMap.prototype._setLayer = function _setLayer(feature) {
    var _layer = {
        'id' : feature.getLayerId(),
        'type' : feature.getMapBoxLayerType(),
        'source' : feature.getStmpLayerId(),
        'filter' : ['all', ['==', 'isVisible', true]]
    };

    _layer.filter.push(['==', 'type', feature.getTypeName()]);
    _layer.filter.push(['==', '$type', feature.getGeometryType()]);

    var _style = this._setLayerStyle(feature);

    if (_style.layout) {
        _layer.layout = _style.layout;
    }

    if (_style.paint) {
        _layer.paint = _style.paint;
    }

    return _layer;
};

jiMap.prototype._setLayerStyle = function _setLayerStyle(feature) {
    var _layerType = feature.getMapBoxLayerType();
    var _layout = null;
    var _paint = null;
    var _style = {};

    var symbolStyle = function() {
        _layout = {
            'icon-image' : '{img}',
            'icon-size' : ['get', 'size'],
            'icon-allow-overlap' : true
        };

        _style.layout = _layout;
    };

    var lineStyle = function() {
        _layout = {
            'line-cap' : ['get', 'cap'],
            'line-join' : ['get', 'join']
        };

        _paint = {
            'line-color' : ['get', 'line-color'],
            'line-width' : ['get', 'line-width'],
            'line-opacity' : ['get', 'line-opacity']
        };

        _style.layout = _layout;
        _style.paint = _paint;
    };

    var fillStyle = function() {
        _paint = {
            'fill-color' : ['get', 'color'],
            'fill-opacity' : ['get', 'opacity']
        };

        if (feature.styleInfo.style['outline-color']) {
            _paint['fill-outline-color'] = ['get', 'outline-color'];
        }

        _style.paint = _paint;
    };

    var circleStyle = function() {
        _paint = {
            'circle-radius' : ['get', 'radius'],
            'circle-color' : ['get', 'color'],
            'circle-opacity' : ['get', 'opacity']
        };

        _style.paint = _paint;
    };

    var rasterStyle = function() {

    };

    switch (_layerType) {
        case jiConstant.MAPBOX_LAYER_TYPE.CIRCLE :
            circleStyle();
            break;
        case jiConstant.MAPBOX_LAYER_TYPE.LINE :
            lineStyle();
            break;
        case jiConstant.MAPBOX_LAYER_TYPE.FILL :
            fillStyle();
            break;
        case jiConstant.MAPBOX_LAYER_TYPE.SYMBOL :
            symbolStyle();
            break;
        case jiConstant.MAPBOX_LAYER_TYPE.RASTER :
            rasterStyle();
            break;
    }

    return _style;
};
