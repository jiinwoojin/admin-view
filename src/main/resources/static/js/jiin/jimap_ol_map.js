'use strict';

// openlayers 2D Map
var JimapOl = function JimapOl(options) {
    if (!(this instanceof JimapOl)) {
        throw new Error('new 로 객체를 생성해야 합니다.');
    }

    this._initComplete = false;

    this._map = undefined;
    this._baseLayer = undefined;
    this._init(options);
}

JimapOl.prototype.constructor = JimapOl;

JimapOl.prototype = {
    /**
     * ol 초기화
     * @param options
     * @private
     */
    _init : function _init(options) {
        debugger;
        this._map = new ol.Map({
            layers : [this.setBaseMap()],
            target : options.container,
            view : new ol.View({
                center : options.center,
                projection : 'EPSG:4326',
                zoom : options.zoom
            })
        });

        this._initComplete = true;
    },
    setBaseMap : function setBaseMap() {
        if (this._baseLayer === undefined) {
            this._baseLayer = new ol.layer.Tile({
                source : new ol.source.TileWMS({
                    url : jiCommon.MAP_SERVER_URL + '/mapproxy/service?',
                    params : {
                        'LAYERS' : jiCommon.getBaseMapLayer(),
                        'TILED' : true,
                        'TRANSPARENT' : true,
                        'FORMAT' : 'image/png'
                    }
                })
            });
        }

        return this._baseLayer;
    }
}