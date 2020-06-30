'use strict';

// mapbox 2D Map
var JimapMapbox = function JimapMapbox(options) {
    if (!(this instanceof JimapMapbox)) {
        throw new Error('new 로 객체를 생성해야 합니다.');
    }

    this._initComplete = false;

    this._map = undefined;
    this._init(options);

    if (!jiCommon.MINI_MAP_WHETHER) {
        this._map.on('load', function(evt) {
            jiCommon.map.setZoomRate(10);
            jiCommon.map.setWheelZoomRate(1);

            milSymbolLoader.init({
                map : evt.target,
                function() {

                }
            })
        });
    }
}

JimapMapbox.prototype.constructor = JimapMapbox;

JimapMapbox.prototype = {
    /**
     * Mapbox 초기화
     * @param options
     */
    _init : function _init(options) {
        if (this._initComplete) {
            throw new Error('Init을 완료한 상태 입니다.');
        }
        mapboxgl.maxParallelImageRequests = 32;

        this._map = new mapboxgl.Map({
            container : options.container,
            style : this.setBaseStyle(),
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

        this._initComplete = true;
    },
    setBaseStyle : function setBaseStyle() {
        var sources = {
            'world' : {
                'type' : jiConstant.MAPBOX_SOURCE_TYPE.RASTER,
                'tiles' : [
                    jiCommon.MAP_SERVER_URL + '/mapproxy/service?bbox={bbox-epsg-3857}&format=image/png&service=WMS&version=1.1.1' +
                    '&request=GetMap&srs=EPSG:3857&crs=EPSG:3857&styles&transparent=true&width=256&height=256&layers=' + jiCommon.BASE_MAP_LAYER
                ],
                'tileSize' : 256
            }
        };
        var layers = [
            {
                'id' : jiConstant.BASE_MAP_ID,
                'type' : jiConstant.MAPBOX_LAYER_TYPE.RASTER,
                'source' : 'world',
                'minzoom' : 0,
                'maxzoom' : 22
            }
        ];
        return {
            'version' : 8,
            'sources' : sources,
            'sprite' : jiCommon.MAP_SERVER_URL + '/GSymbol/GSSSymbol',
            'glyphs' : jiCommon.MAP_SERVER_URL + '/fonts/{fontstack}/{range}.pbf',
            'layers' : layers
        };
    },
    /**
     * Wheel Zoom 수준 정의
     * @param value
     */
    setWheelZoomRate : function setWheelZoomRate(value) {
        this._map.scrollZoom.setWheelZoomRate(value);
    },
    /**
     * Zoom 수준 정의
     * @param value
     */
    setZoomRate : function setZoomRate(value) {
        this._map.scrollZoom.setZoomRate(value);
    },
    /**
     * 중심 좌표 이동
     * @param center
     */
    setCenter : function setCenter(center) {
        this._map.setCenter(center)
    }
}

var jimapMapboxPrototypeAccessors = {
    minZoom : {configurable : true},
    maxZoom : {configurable : true},
    event : {configurable : true},
    layer : {configurable : true}
}

jimapMapboxPrototypeAccessors.minZoom.get = function() {
    return this._map.getMinZoom();
};

jimapMapboxPrototypeAccessors.minZoom.set = function(zoom) {
    this._map.setMinZoom(zoom);
};

jimapMapboxPrototypeAccessors.maxZoom.get = function() {
    return this._map.getMaxZoom();
};

jimapMapboxPrototypeAccessors.maxZoom.set = function(zoom) {
    this._map.setMaxZoom(zoom);
};

/**
 * 이벤트 추가
 * @param type
 * @param event
 */
jimapMapboxPrototypeAccessors.event.add = function(type, event) {

};

/**
 * 이벤트 제거
 * @param type
 * @param event
 */
jimapMapboxPrototypeAccessors.event.remove = function(type, event) {

};

/**
 *
 * @param id
 * @returns {string}
 */
jimapMapboxPrototypeAccessors.layer.get = function(id) {
    return this._map.getLayer(id);
};

/**
 * Layer 등록
 */
jimapMapboxPrototypeAccessors.layer.add = function() {

};

/**
 * Layer 삭제
 */
jimapMapboxPrototypeAccessors.layer.remove = function() {

};

Object.defineProperties(JimapMapbox.prototype, jimapMapboxPrototypeAccessors);