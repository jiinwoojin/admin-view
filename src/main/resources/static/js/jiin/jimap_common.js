'use strict';

// [공통] CONTEXT Path 를 얻기 위한 문장
var ContextPath = '#ContextPath';
var CONTEXT = $(ContextPath).attr('data-contextPath') ? $(ContextPath).attr('data-contextPath') : '';

/**
 * JIMAP Script
 * [공통] : 자바스크립트 공통 로직
 */
var jiCommon = {
    /**
     *
     */
    MAP_SERVER_DOMAIN : '192.168.1.180',    // 115.144.19.100
    /**
     *
     */
    MAP_SERVER_PORT : (window.location.protocol === 'https:') ? 0 : 0,
    /**
     *
     */
    MAP_SERVER_URL : '',
    /**
     *
     */
    BASE_MAP_LAYER : 'world_layer', // 기본 지도 데이터
    /**
     *
     */
    MINI_MAP_WHETHER : false,
    /**
     *
     */
    map : undefined,
    /**
     *
     */
    mapExtents : undefined,
    /**
     *
     */
    overlay : undefined,
    /**
     *
     */
    PRESENT_MAP_KIND : undefined,
    /**
     * true : 3D, false : 2D
     * @returns {boolean}
     */
    checkMapKind : function checkMapKind() {
        return this.PRESENT_MAP_KIND === jiConstant.MAP_KIND.MAP_3D;
    },
    setPresentMapKind : function setPresentMapKind(mapKind) {
        this.PRESENT_MAP_KIND = mapKind;
    },
    /**
     *
     * @returns {string}
     */
    getBaseMapLayer : function getBaseMapLayer() {
        return this.BASE_MAP_LAYER;
    },
    /**
     *
     * @param layerName
     */
    setBaseMapLayer : function setBaseMapLayer(layerName) {
        this.BASE_MAP_LAYER = layerName;
    },
    setMapExtents : function setMapExtents() {
        var extents = this.map.getExtents();

        if (this.mapExtents === undefined) {
            this.mapExtents = {};
        }

        this.mapExtents['east'] = extents.east;
        this.mapExtents['north'] = extents.north;
        this.mapExtents['south'] = extents.south;
        this.mapExtents['west'] = extents.west;
    },
    getMapExtents : function getMapExtents() {
        return this.mapExtents;
    },
    /**
     *
     */
    showLoading : function showLoading() {
        $('#loader-wrapper').show();
    },
    /**
     *
     */
    hideLoading : function hideLoading() {
        $('#loader-wrapper').hide();
    },
    // [공통] sleep 함수
    sleep : function sleep(t) {
        return new Promise(resolve => setTimeout(resolve, t));
    },
    isDigit : function isDigit(code) {
        return this.isBetween(code, 47, 58);
    },
    isNumber : function isNumber(num) {
        return !isNaN(num) && num !== null && !Array.isArray(num);
    },
    /**
     * [공통] first 와 last 값 사이에 있는지 체크
     * @param v
     * @param first
     * @param last
     */
    isBetween : function isBetween(v, first, last) {

    },
    isObject : function isObject(object) {
        return (!!object) && (object.constructor === Object);
    },
    /**
     * [공통] 깊은 복사
     * @param object
     * @returns {*}
     */
    cloneDeep : function cloneDeep(object) {
        return _.cloneDeep(object);
    },
    /**
     * [공통] 얕은 복사
     * @param object
     * @returns {*}
     */
    cloneShallow : function cloneShallow(object) {
        return _.clone(object);
    },
    // [공통] 유효성 체크 함수
    valid : {
        // [공통] MGRS 유효성 검사
        isValidMgrs : function mgrs(mgrs) {

        },
        // [공통] UTM 유효성 감사
        isValidUtm : function utm(utm) {

        },
        // [공통] 경위도 유효성 검사
        isValidLonLat : function lonLat(point) {
            return this.isValidLongitude(point.lon) && this.isValidLatitude(point.lat);
        },
        isValidLongitude : function isValidLongitude(lon) {
            return (this.isNumber(lon) && this.isBetween())
        },
        isValidLatitude : function isValidLatitude(lat) {

        },
        // [공통] polygon 좌표 유효성 검사
        isValidPolygon : function polygon(coordinates) {
            var _firstCoord = coordinates[0];
            var _lastCoord = coordinates[coordinates.length - 1];

            return _firstCoord[0] === _lastCoord[0] && _firstCoord[1] === _lastCoord[1];
        },
        /**
         * [공통]
         * @param value
         * @returns {boolean}
         */
        checkValue : function checkValue(value) {
            if (value === null || value === undefined || value === "" || value === '') {
                return false;
            } else if (value !== null && typeof value === 'object' && Object.keys(value).length < 1) {
                return false;
            } else {
                return !(Array.isArray(value) && value.length < 1);
            }
        },
        /**
         * [공통]
         * @param a
         * @param b
         * @returns {*}
         */
        defaultValue : function defaultValue(a, b) {
            if (a !== null && a !== undefined) {
                return a;
            }

            return b;
        },
        // [공통] 중복 체크 함수
        duplicateCheck : function duplicateCheck(type, targetId, messageId) {
            var name = $(targetId).val();
            if ($.isEmptyObject(name)) {
                $(targetId).focus();
                return
            }

            jiCommon.showLoading();

            $.post({
                url: CONTEXT + '/server/api/check/duplicate',
                data: {
                    type: type,
                    name: name
                }
            }).done(function (result) {
                if (!result) {
                    $(messageId).html('사용 가능한 이름입니다.');  // Message 로 통합
                    $(targetId).data('check-duplicate', true);
                } else {
                    toastr.error('사용 불가능한 이름입니다.');         // Message 로 이동
                    $(targetId).data('check-duplicate', false);
                }
            }).fail(function () {
                toastr.error('중복체크에 실패 하엿습니다.');
                $(targetId).data('check-duplicate', false);
            }).always(function () {
                jiCommon.hideLoading();
            });
        }
    },
    /**
     * [공통] 좌표변환
     */
    convert : {
        /**
         * 경위도 -> 화면좌표
         * @param point point.lon, point.lat
         * @returns {*}
         * var point = {};
         * point.lon = 127;
         * point.lat = 37;
         * var result = jiCommon.convert.lonLatToPixel(point);
         */
        lonLatToPixel : function lonLatToPixel(point) {
            return jiCommon.map.project(point);
        },
        /**
         * EPSG:4326 -> EPSG:3857
         * @param lon
         * @param lat
         * @returns {[number, number]}
         */
        degreesToMeters : function degreesToMeters(lon, lat) {
            var x = lon * 20037508.34 / 180;
            var y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
            y = y * 20037508.34 / 180;

            return [x, y]
        },
        /**
         * Gars -> 경위도
         * @param gars - '608LS47'
         * var lonLat = jiCommon.convert.garsToLonLat('608LS47');
         */
        garsToLonLat : function garsToLonLat(gars) {
            return geoTrans.Gars.garsToLonLat(gars);
        },
        /**
         * 화면좌표 -> 경위도
         * @param point point.x, point.y
         * var point = {};
         * point.x = 250;
         * point.y = 300;
         * var result = jiCommon.convert.pixelToLonLat(point);
         */
        pixelToLonLat : function pixelToLonLat(point) {
            return jiCommon.map.unproject(point);
        },
        /**
         * EPSG:3857 -> EPSG:4326
         * @param x
         * @param y
         * @returns {{lon: number, lat: number}}
         */
        metersToDegrees : function metersToDegrees(x, y) {
            var lon = x * 180 / 20037508.34;
            var lat = Math.atan(Math.exp(y * Math.PI / 20037508.34)) * 360 / Math.PI - 90;

            return {
                'lon' : lon,
                'lat' : lat
            }
        },
        /**
         * Gars -> 경위도 polygon
         * @param gars - '608LS47'
         */
        garsToPolygon : function garsToPolygon(gars) {
            var coords = geoTrans.Gars.garsToLonLatArray(gars);

            var leftBottom = coords.leftBottom;
            var rightTop = coords.rightTop;

            var leftTop = new geoTrans.LatLon(leftBottom.lon, rightTop.lat);
            var rightBottom = new geoTrans.LatLon(rightTop.lon, leftBottom.lat);

            var coordinates = [];
            coordinates.push([leftTop.lon, leftTop.lat]);
            coordinates.push([rightTop.lon, rightTop.lat]);
            coordinates.push([rightBottom.lon, rightBottom.lat]);
            coordinates.push([leftBottom.lon, leftBottom.lat]);
            coordinates.push([leftTop.lon, leftTop.lat]);

            return new Geometry.Polygon(coordinates).getCoordinates();
        },
        degreesToRadians : function degreesToRadians(degrees) {
            return turf.degreesToRadians(degrees);
        },
        radiansToDegrees : function radiansToDegrees(radians) {
            return turf.radiansToDegrees(radians);
        },
        lengthToRadians : function lengthToRadians(length, units) {
            if (units === undefined) {
                units = jiConstant.UNITS.KILOMETERS;
            }

            return turf.lengthToRadians(length, {units : units});
        },
        lengthToDegrees : function lengthToDegrees(length, units) {
            if (units === undefined) {
                units = jiConstant.UNITS.KILOMETERS;
            }

            return turf.lengthToDegrees(length, {units : units});
        },
        /**
         * EPSG:4326 -> EPSG:3857
         * @param lon
         * @param lat
         * @returns {*}
         */
        wgs84ToMercator : function wgs84ToMercator(lon, lat) {
            return turf.toMercator(turf.point([lon, lat])).geometry.coordinates;
        },
        /**
         * EPSG:3857 -> EPSG:4326
         * @param x
         * @param y
         * @returns {*}
         */
        mercatorToWgs84 : function mercatorToWgs84(x, y) {
            return turf.toWgs84(turf.point([x, y])).geometry.coordinates;
        }
    },
    calculation : {
        distance : function distance(p1, p2, units) {
            if (units === undefined) {
                units = jiConstant.UNITS.KILOMETERS;
            }

            var from = turf.point([p1.x, p1.y]);
            var to = turf.point([p2.x, p2.y]);

            return turf.distance(from, to, {units : units});
        },
        pointToLineDistance : function pointToLineDistance(point, line, units) {
            if (units === undefined) {
                units = jiConstant.UNITS.KILOMETERS;
            }

            if (Array.isArray(line)) {
                throw new Error('Array 형태의 값이어야 합니다.');
            }

            return turf.pointToLineDistance(turf.point([point.x, point.y]), turf.lineString(line), {units : units});
        }
    },
    booleans : {
        crosses : function crosses() {

        }
    },
    addMapEvent : function addMapEvent(type, fn) {
        this.map.addEvent(type, fn);
    },
    removeMapEvent : function removeMapEvent(type, fn) {
        this.map.removeEvent(type, fn);
    },
    disableDragPan : function disableDragPan() {
        this.map.disableDragPan();
    },
    enableDragPan : function enableDragPan() {
        this.map.enableDragPan();
    }
};

// Local IP 가져오기
/*$.ajax({
    url: `${CONTEXT}/server/api/service/local-info`,
    type: 'get',
    contentType: "application/json",
    async: false,
    success: function(res){
        if(res && res.ip){
            jiCommon.MAP_SERVER_DOMAIN = res.ip
        }
    },
    error: function(e){
        console.log(e);
    }
});*/

if (jiCommon.MAP_SERVER_URL === '') {
    jiCommon.MAP_SERVER_URL = window.location.protocol + '//'
        + jiCommon.MAP_SERVER_DOMAIN + (jiCommon.MAP_SERVER_PORT !== 0 ? ':' + jiCommon.MAP_SERVER_PORT : '');
}