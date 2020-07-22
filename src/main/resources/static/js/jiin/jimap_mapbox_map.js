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
            });
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
            minZoom : options.minZoom || 2,
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
                    jiCommon.MAIN_SERVER_URL + '/mapproxy/service?bbox={bbox-epsg-3857}&format=image/png&service=WMS&version=1.1.1' +
                    '&request=GetMap&srs=EPSG:3857&crs=EPSG:3857&styles&transparent=true&width=256&height=256&layers=' + jiCommon.MAIN_MAP_LAYER
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
            'sprite' : jiCommon.MAIN_SERVER_URL + '/GSymbol/GSSSymbol',
            'glyphs' : jiCommon.MAIN_SERVER_URL + '/fonts/{fontstack}/{range}.pbf',
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
    },
    getExtents : function getExtents() {
        var extents = this._map.getBounds();

        return {
            'east' : extents.getEast(),
            'north' : extents.getNorth(),
            'south' : extents.getSouth(),
            'west' : extents.getWest()
        }
    },
    /**
     * LAYER 데이터 설정
     * @param layer
     */
    setLayer : function setLayer(layer) {
        this._map.removeLayer(jiConstant.BASE_MAP_ID);
        this._map.removeSource('world');

        this._map.addSource('world', {
            'type': 'raster',
            'tiles': [
                jiCommon.MAP_SERVER_URL + (window.location.protocol === 'https:' ? '/mapproxy/service' : '/service') + `?REQUEST=getMap&LAYERS=${layer}&version=1.3.0&crs=EPSG:3857&srs=EPSG:3857&format=image/png&bbox={bbox-epsg-3857}&width=256&height=256&styles=`
            ],
            'tileSize': 256
        });
        this._map.addLayer(
            {
                'id': jiConstant.BASE_MAP_ID,
                'type': 'raster',
                'source': 'world',
                'paint': {}
            }
        );
    },
    getCanvasContainer : function getCanvasContainer() {
        return this._map.getCanvasContainer();
    },
    addEvent : function addEvent(type, fn) {
        this._map.on(type, fn);
    },
    removeEvent : function removeEvent(type, fn) {
        this._map.off(type, fn);
    },
    disableDragPan : function disableDragPan() {
        this._map.dragPan.disable();
    },
    enableDragPan : function enableDragPan() {
        this._map.dragPan.enable();
    },
    project : function project(point) {
        return this._map.project([point.lon, point.lat]);
    },
    unproject : function unproject(point) {
        return this._map.unproject([point.x, point.y]);
    }
}