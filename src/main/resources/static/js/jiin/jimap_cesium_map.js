'use strict';

// cesium 3D Map
var JimapCesium = function JimapCesium(options) {
    if (!(this instanceof JimapCesium)) {
        throw new Error('');
    }

    this._map = undefined;
    this._minHeight = -414.0;
    this._maxHeight = 8777.0;
    this._shadingUniforms = {};
    this._contourUniforms = {};

    this._init(options);

    this._updateMaterial();

    this.leftDownHandler = new Cesium.ScreenSpaceEventHandler(this._map.scene.canvas);
    this.mouseMoveHandler = new Cesium.ScreenSpaceEventHandler(this._map.scene.canvas);
    this.leftUpHandler = new Cesium.ScreenSpaceEventHandler(this._map.scene.canvas);
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
            //timeline : false,
            //animation : false,
            baseLayerPicker : false,
            imageryProvider : false,
            selectionIndicator : false,
            geocoder : false,
            scene3DOnly : true,
            terrainShadows : Cesium.ShadowMode.ENABLED,
            shouldAnimate : true,
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

        this._minPitch = -Cesium.Math.PI_OVER_TWO;
        this._maxPitch = 0;
        this._minHeight = 50;

        var that = this;

        this._map.camera.changed.addEventListener(function() {
            if (that._map.camera._suspendTerrainAdjustment && that._map.scene.mode === Cesium.SceneMode.SCENE3D) {
                that._map.camera._suspendTerrainAdjustment = false;
                that._map.camera._adjustHeightForTerrain();
            }

            var pitch = that._map.camera.pitch;

            if (pitch > that._maxPitch || pitch < that._minPitch) {
                that._map.scene.screenSpaceCameraController.enableTilt = false;

                if (pitch > that._maxPitch) {
                    pitch = that._maxPitch;
                } else if (pitch < that._minPitch) {
                    pitch = that._minPitch;
                }

                let destination = Cesium.Cartesian3.fromRadians(
                    that._map.camera.positionCartographic.longitude,
                    that._map.camera.positionCartographic.latitude,
                    Math.max(that._map.camera.positionCartographic.height, that._minHeight));

                that._map.camera.setView({
                    destination : destination,
                    orientation : {pitch : pitch}
                });

                that._map.scene.screenSpaceCameraController.enableTilt = true;
            }
        });

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
            url : jiCommon.MAP_SERVER_URL + '/tilesets/lv2',
            //requestWaterMask : true,
            requestVertexNormals : true
        });

        this._map.terrainProvider = terrainProvider;
        this._map.scene.terrainProvider = terrainProvider;

        this._map.scene.globe.enableLighting = true;
    },
    /**
     *
     * @private
     */
    _setBaseImagery : function _setBaseImagery() {
        var imageryLayers = this._map.imageryLayers;
        imageryLayers.removeAll();
        imageryLayers.addImageryProvider(new Cesium.WebMapServiceImageryProvider({
            //url : jiCommon.MAP_SERVER_URL + `${window.location.protocol === 'https:' ? '/mapproxy' : ''}/service`,
            url : jiCommon.MAP_SERVER_URL + '/mapproxy/service',
            layers : jiCommon.getBaseMapLayer(),
            parameters : {
                transparent : 'true',
                format : 'image/png',
                tiled : true
            }
        }));
    },
    _updateMaterial : function _updateMaterial() {
        var material = Cesium.Material.fromType('ElevationRamp');
        this._shadingUniforms = material.uniforms;
        this._shadingUniforms.minimumHeight = -7477.0;
        this._shadingUniforms.maximumHeight = 0.0;
        this._shadingUniforms.image = this._getColorRamp();

        this._map.scene.globe.material = material;
    },
    _getColorRamp : function _getColorRamp() {
        var ramp = document.createElement('canvas');
        ramp.width = 100;
        ramp.height = 1;
        var ctx = ramp.getContext('2d');

        /*var values = [0.0, 0.012, 0.12, 0.18, 0.25, 0.31, 0.37, 0.43, 0.5, 0.56, 0.62, 0.68, 0.75, 0.80, 0.83, 1];
        var grd = ctx.createLinearGradient(0, 0, 100, 0);
        grd.addColorStop(values[0], 'rgb(91, 140, 196)'); //Navy
        grd.addColorStop(values[1], 'rgb(94, 141, 196)'); //Magenta
        grd.addColorStop(values[2], 'rgb(98, 144, 196)'); //Purple
        grd.addColorStop(values[3], 'rgb(103, 147, 197)'); //Blue
        grd.addColorStop(values[4], 'rgb(107, 149, 197)'); //Cyan
        grd.addColorStop(values[5], 'rgb(111, 151, 198)'); //Green
        grd.addColorStop(values[6], 'rgb(115, 153, 198)'); //Lime
        grd.addColorStop(values[7], 'rgb(119, 156, 199)'); //Grey
        grd.addColorStop(values[8], 'rgb(120, 157, 199)'); //Yellow
        grd.addColorStop(values[9], 'rgb(128, 161, 200)'); //Orange
        grd.addColorStop(values[10], 'rgb(131, 164, 201)'); //Red
        grd.addColorStop(values[11], 'rgb(134, 164, 201)'); //Maroon
        grd.addColorStop(values[12], 'rgb(136, 166, 202)'); //Brown
        grd.addColorStop(values[13], 'rgb(139, 168, 202)'); //OliveF
        grd.addColorStop(values[14], 'rgb(142, 169, 202)'); //Dark Green
        grd.addColorStop(values[15], '#a9a9a900'); //Transparent*/

        var values = [0.0, 0.04, 0.08, 0.12, 0.16, 0.2, 0.24, 0.28, 0.32, 0.36, 0.4, 0.44, 0.48, 0.52, 0.56, 0.6, 0.64, 0.68, 0.72, 0.76, 0.8, 0.84, 0.88, 0.92, 0.96, 1];
        var grd = ctx.createLinearGradient(0, 0, 100, 0);
        grd.addColorStop(values[0], 'rgb(69, 127, 193)');   // 수심 깊음
        grd.addColorStop(values[1], 'rgb(70, 128, 193)');
        grd.addColorStop(values[2], 'rgb(73, 130, 193)');
        grd.addColorStop(values[3], 'rgb(75, 131, 194)');
        grd.addColorStop(values[4], 'rgb(78, 133, 194)');
        grd.addColorStop(values[5], 'rgb(81, 134, 194)');
        grd.addColorStop(values[6], 'rgb(84, 136, 194)');
        grd.addColorStop(values[7], 'rgb(87, 138, 196)');
        grd.addColorStop(values[8], 'rgb(91, 140, 196)');
        grd.addColorStop(values[9], 'rgb(94, 141, 196)');
        grd.addColorStop(values[10], 'rgb(98, 144, 197)');
        grd.addColorStop(values[11], 'rgb(103, 147, 197)');
        grd.addColorStop(values[12], 'rgb(107, 149, 198)');
        grd.addColorStop(values[13], 'rgb(111, 151, 198)');
        grd.addColorStop(values[14], 'rgb(115, 153, 199)');
        grd.addColorStop(values[15], 'rgb(119, 156, 199)');
        grd.addColorStop(values[16], 'rgb(120, 157, 199)');
        grd.addColorStop(values[17], 'rgb(128, 161, 200)');
        grd.addColorStop(values[18], 'rgb(131, 164, 201)');
        grd.addColorStop(values[19], 'rgb(134, 164, 201)');
        grd.addColorStop(values[20], 'rgb(136, 166, 202)');
        grd.addColorStop(values[21], 'rgb(139, 168, 202)');
        grd.addColorStop(values[22], 'rgb(142, 169, 202)');
        grd.addColorStop(values[23], 'rgb(142, 170, 202)');
        grd.addColorStop(values[24], 'rgb(142, 170, 202)');
        grd.addColorStop(values[25], '#a9a9a900'); //Transparent

        ctx.fillStyle = grd;
        ctx.fillRect(0, 0, 100, 1);

        return ramp;
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
    },
    /**
     * LAYER 데이터 설정
     * @param layer
     */
    setLayer : function setLayer(layer) {
        var imageryLayers = this._map.imageryLayers;
        imageryLayers.removeAll();
        imageryLayers.addImageryProvider(new Cesium.WebMapServiceImageryProvider({
            url : jiCommon.MAP_SERVER_URL + (window.location.protocol === 'https:' ? '/mapproxy/service' : '/service'),
            layers : layer,
            parameters : {
                transparent : 'true',
                format : 'image/png',
                tiled : true
            }
        }));
    },
    addEvent : function addEvent(type, fn) {
        switch (type) {
            case jiConstant.EVENTS.MOUSEDOWN :
                jiCommon.map.leftDownHandler.setInputAction(fn, Cesium.ScreenSpaceEventType.LEFT_DOWN);
                break;
            case jiConstant.EVENTS.MOUSEMOVE :
                jiCommon.map.mouseMoveHandler.setInputAction(fn, Cesium.ScreenSpaceEventType.MOUSE_MOVE);
                break;
            case jiConstant.EVENTS.MOUSEUP :
                jiCommon.map.leftUpHandler.setInputAction(fn, Cesium.ScreenSpaceEventType.LEFT_UP);
                break;
        }
    },
    removeEvent : function removeEvent(type, fn) {
        switch (type) {
            case jiConstant.EVENTS.MOUSEDOWN :
                jiCommon.map.leftDownHandler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_DOWN);
                break;
            case jiConstant.EVENTS.MOUSEMOVE :
                jiCommon.map.mouseMoveHandler.removeInputAction(Cesium.ScreenSpaceEventType.MOUSE_MOVE);
                break;
            case jiConstant.EVENTS.MOUSEUP :
                jiCommon.map.leftUpHandler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_UP);
                break;
        }
    },
    /**
     * 경위도 -> 화면좌표
     * @param point
     */
    project : function project(point) {
        return Cesium.SceneTransforms.wgs84ToWindowCoordinates(this._map.scene, Cesium.Cartesian3.fromDegrees(point.lon, point.lat));
    },
    /**
     * 화면좌표 -> 경위도
     * @param point
     */
    unproject : function unproject(point) {
        var cartesian = this._map.camera.pickEllipsoid(point, this._map.scene.globe.ellipsoid);

        var _point = {};
        if (cartesian) {
            var cartographic = this._map.scene.globe.ellipsoid.cartesianToCartographic(cartesian);
            _point.lon = Number(Cesium.Math.toDegrees(cartographic.longitude).toFixed(6));
            _point.lat = Number(Cesium.Math.toDegrees(cartographic.latitude).toFixed(6));
        } else {
            _point.lon = 0;
            _point.lat = 0;
        }

        return _point;
    },
    disableDragPan : function disableDragPan() {
        this._map.scene.screenSpaceCameraController.enableRotate = false;
        this._map.scene.screenSpaceCameraController.enableTranslate = false;
        this._map.scene.screenSpaceCameraController.enableZoom = false;
        this._map.scene.screenSpaceCameraController.enableTilt = false;
        this._map.scene.screenSpaceCameraController.enableLook = false;
    },
    enableDragPan : function enableDragPan() {
        this._map.scene.screenSpaceCameraController.enableRotate = true;
        this._map.scene.screenSpaceCameraController.enableTranslate = true;
        this._map.scene.screenSpaceCameraController.enableZoom = true;
        this._map.scene.screenSpaceCameraController.enableTilt = true;
        this._map.scene.screenSpaceCameraController.enableLook = true;
    }
}