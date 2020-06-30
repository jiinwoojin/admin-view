'use strict';

// cesium 3D Map
var JimapCesium = function JimapCesium(options) {
    if (!(this instanceof JimapCesium)) {
        throw new Error('');
    }

    this._map = undefined;
    this._minHeight = -74777.0;
    this._maxHeight = 8777.0;
    this._shadingUniforms = {};

    this._init(options);
}

JimapCesium.prototype.constructor = JimapCesium;

JimapCesium.prototype = {
    /**
     * cesium 초기화
     * @param options
     */
    _init : function _init(options) {
        this._map = new Cesium.Viewer(options.container, {
            sceneMode : Cesium.SceneMode.SCENE3D,
            timeline : false,
            animation : false,
            baseLayerPicker : false,
            imageryProvider : false,
            selectionIndicator : false,
            geocoder : false,
            scene3DOnly : true,
            terrainShadows : Cesium.ShadowMode.ENABLED,
            shouldAnimate : false,
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
            //requestRenderMode : true,   // 필요한 경우에만 화면 갱신
        });

        this._map.scene.globe = new Cesium.Globe(Cesium.Ellipsoid.WGS84);

        this._map.scene.globe.showGroundAtmosphere = false;
        this._map.scene.globe.showWaterEffect = true;

        this._setBaseTerrain();

        this._setBaseImagery();

        this._map.scene.logarithmicDepthBuffer = false; // Black Spot 방지
        $('.cesium-widget-credits').hide();     // widget-credits div 제거
        this._map.scene.fog.enabled = false;

        if (jiCommon.mapExtents !== undefined) {
            this.moveBounds(jiCommon.getMapExtents())
            jiCommon.mapExtents = undefined;
        } else {
            this.flyTo(126.7640322, 38.539249, 2000000);
        }

        milSymbolLoader.init({
            map : this._map
        }, null);
    },
    /**
     *
     * @private
     */
    _setBaseTerrain : function _setBaseTerrain() {
        var terrainProvider = new Cesium.CesiumTerrainProvider({
            url : jiCommon.MAP_SERVER_URL + '/tilesets/dted'
        });

        this._map.terrainProvider = terrainProvider;
        this._map.scene.terrainProvider = terrainProvider;
    },
    /**
     *
     * @private
     */
    _setBaseImagery : function _setBaseImagery() {
        var imageryLayers = this._map.imageryLayers;
        imageryLayers.removeAll();
        imageryLayers.addImageryProvider(new Cesium.WebMapServiceImageryProvider({
            url : jiCommon.MAP_SERVER_URL + '/mapproxy/service',
            layers : jiCommon.getBaseMapLayer(),
            parameters : {
                transparent : 'true',
                format : 'image/png'
            }
        }));
    },
    _updateMaterial : function _updateMaterial() {

    },
    /**
     *
     * @param extents
     */
    moveBounds : function moveBounds(extents) {
        this._map.camera.flyTo({
            destination : Cesium.Rectangle.fromDegrees(extents.west, extents.south, extents.east, extents.north),
            duration : 0
        })
    },
    /**
     *
     * @param lon
     * @param lat
     * @param height
     */
    flyTo : function flyTo(lon, lat, height) {
        if (height === undefined) {
            height = this._map.scene.globe.ellipsoid.cartesianToCartographic(this._map.camera.position).height;
        }

        this._map.camera.flyTo({
            destination : Cesium.Cartesian3.fromDegrees(lon, lat, height)
        });
    },
    /**
     *
     * @returns {{east: *, south: *, north: *, west: *}}
     */
    getExtents : function getExtents() {
        var extents = new Cesium.Rectangle();

        this._map.camera.computeViewRectangle(this._map.scene.globe.ellipsoid, extents);

        return {
            'east' : Cesium.Math.toDegrees(extents.east),
            'north' : Cesium.Math.toDegrees(extents.north),
            'south' : Cesium.Math.toDegrees(extents.south),
            'west' : Cesium.Math.toDegrees(extents.west)
        }
    },
    /**
     *
     * @param mode
     * @param constraint
     */
    drawControlMode : function drawControlMode(mode, constraint) {
        this._drawControlMode = mode;
        this._drawControlConstraint = constraint;
        this._drawControlPoints = [];
        this._map.screenSpaceEventHandler.removeInputAction(this._leftDoubleClickEvent, Cesium.ScreenSpaceEventType.LEFT_DOUBLE_CLICK);
        if (mode === 'draw_line_string') {
            this._map.screenSpaceEventHandler.setInputAction(this._leftDoubleClickEvent, Cesium.ScreenSpaceEventType.LEFT_DOUBLE_CLICK);
        }
    }
}