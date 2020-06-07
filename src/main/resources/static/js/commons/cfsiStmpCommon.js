'use strict';
/**
 * 상황도 공통
 */
var stmp = {
    PROTOCOL : window.location.protocol,
    // 상황도 서버 도메인 서버 IP
    SERVER_DOMAIN : '211.172.246.71',
    // 상황도 서버 지도 PORT
    SERVER_MAP_PORT : 11100,
    // 상황도 서버 이미지 PORT
    SERVER_IMG_PORT : 11200,
    STMP_UI_PATH : '/ui/CF/SI',
    BASE_MAP_SOURCE : 'world_k2',
    BASE_MAP_LAYER_ID : 'BASE_MAP',
    URL : undefined,
    setBaseMapSource : function setBaseMapSource(value) {
        this.BASE_MAP_SOURCE = value;
    },
    getBaseMapSource : function getBaseMapSource() {
        return this.BASE_MAP_SOURCE;
    },
    mapSource : '',
    setMapSource : function setMapSource(value) {
        this.mapSource = value;
    },
    getMapSource : function getMapSource() {
        return this.mapSource;
    },
    // 상황도 지도 종류 코드
    /**
     * R01 : G25K
     * R02 : G50K
     * R03 : G100K
     * R04 : G250K
     * R05 : G500K
     * R06 : G1M
     * R07 : 연안작전지도
     * R08 : A250K
     * R09 : A500K
     * R10 : A1M
     * R11 : A2M
     * R12 : 아리랑위성영상
     * R13 : CIB영상
     * V01 : G25K
     * V02 : G50K
     * V03 : G100K
     * V04 : G250K
     * V05 : G500K
     * V06 : G1M
     * V07 : KR1
     * V08 : KR2
     * V09 : KR3
     * V10 : KR4
     * V11 : KR5
     * V12 : A250K
     * V13 : A500K
     * V14 : A1M
     * V15 : A2M
     * V16 : 한반도주변국
     * V17 : 세계지도
     * V18 : COP지도
     */
    STMP_MAP_KND_CD : 'R12',
    /**
     * 도시요소 최신화 설정
     */
    DITEM_LTS_NEW : {
        YN : 'Y',
        CYCLE : 300000
    },
    /**
     * 도시요소 최신화 점멸 설정
     */
    DITEM_LTS_FLASH : {
        YN : 'Y',
        NUM : 5
    },
    /**
     * true : 사용, false : 미사용
     * @returns {boolean}
     */
    getDitemLtsNewYn : function getDitemLtsNewYn() {
        return this.DITEM_LTS_NEW.YN === 'Y';
    },
    setDitemLtsNewYn : function setDitemLtsNewYn(value) {
        this.DITEM_LTS_NEW.YN = value.toLocaleUpperCase();
    },
    getDitemLtsNewCycle : function getDitemLtsNewCycle() {
        return this.DITEM_LTS_NEW.CYCLE;
    },
    setDitemLtsNewCycle : function setDitemLtsNewCycle(value) {
        this.DITEM_LTS_NEW.CYCLE = Number(value);
    },
    /**
     * true : 사용, false : 미사용
     * @returns {boolean}
     */
    getDitemLtsFlashYn : function getDitemLtsFlashYn() {
        return this.DITEM_LTS_FLASH.YN === 'Y';
    },
    setDitemLtsFlashYn : function setDitemLtsFlashYn(value) {
        this.DITEM_LTS_FLASH.YN = value.toLocaleUpperCase();
    },
    getDitemLtsFlashNum : function getDitemLtsFlashNum() {
        return this.DITEM_LTS_FLASH.NUM;
    },
    setDitemLtsFlashNum : function setDitemLtsFlashNum(value) {
        this.DITEM_LTS_FLASH.NUM = Number(value);
    },
    /**
     * 간략부호 설정
     */
    SIGN_CNVSN_LINK : 'N',
    /**
     * true : 적용
     * false : 미적용
     * @returns {boolean}
     */
    getSignCnvsnLink : function getSignCnvsnLink() {
        return this.SIGN_CNVSN_LINK === 'Y';
    },
    setSignCnvsnLink : function setSignCnvsnLink(value) {
        this.SIGN_CNVSN_LINK = value;
    },
    BROWSER_KIND_CD : '',
    BROWSER_KIND : {
        IE : '0',
        CHROME : '1',
        FI : '2'
    },
    initBrowserKindCd : function initBrowserKindCd() {
        if (navigator.userAgent.indexOf('Chrome') > -1) {
            this.BROWSER_KIND_CD = this.BROWSER_KIND.CHROME;
        } else {
            this.BROWSER_KIND_CD = this.BROWSER_KIND.IE;
        }
    },
    getBrowserKindCd : function getBrowserKindCd() {
        return this.BROWSER_KIND_CD;
    },
    /**
     * 좌표계 정의
     */
    COORDINATE_SYSTEM : {
        WGS84 : '0',
        MGRS : '1',
        UTM : '2',
        GEOREF : '3',
        GARS : '4'
    },
    MESSAGE : {
        ERROR_FEATURE_ID : '객체 ID 가 없습니다.',
        ERROR_FEATURE_TYPE : '객체 타입 정보가 없습니다.',
        ERROR_LAYER_ID : '레이어 ID 가 없습니다.',
        ERROR_STYLE_INFO : '스타일 정보가 없습니다.'
    },
    /**
     * 도형 타입 정의
     */
    DRAW_TYPE_KIND : {
        BASE_MILSYMBOL : {
            CD : '0',
            LAYER_NAME : 'BASE_MILSYMBOL'
        },           // 기본부호
        POINT : {
            CD : '1',
            LAYER_NAME : 'POINT'
        },                    // 점
        LINE : {
            CD : '2',
            LAYER_NAME : 'LINE'
        },                     // 선
        POLYGON : {
            CD : '3',
            LAYER_NAME : 'FILL'
        },                  // 면
        CIRCLE : {
            CD : '4',
            LAYER_NAME : 'CIRCLE'
        },                   // 원
        IMAGE : {
            CD : '5',
            LAYER_NAME : 'SYMBOL'
        },                    // 이미지
        MILITARY_MILSYMBOL : {
            CD : '6',
            LAYER_NAME : 'MILITARY_MILSYMBOL'
        },       // 전술부호
        OVERLAY : {
            CD : '7',
            LAYER_NAME : 'OVERLAY'
        }                   // 투명도
    },
    DRAW_TYPE_LAYER_NAME : {
        BASE_MILSYMBOL : 'BASE_MILSYMBOL',
        POINT : 'CIRCLE',
        LINE : 'LINE',
        POLYGON : 'FILL',
        CIRCLE : 'CIRCLE',
        IMAGE : 'SYMBOL',
        MILITARY_MILSYMBOL : 'MILITARY_MILSYMBOL',
        OVERLAY : 'OVERLAY'
    },
    // 투명도 Draw 종류
    OVERLAY_TYPE : {
        LINE : 'line',
        POINT : 'point',
        RECT : 'rect',
        ROUNDRECT : 'roundrect',
        CIRCLE : 'circle',
        ARC : 'arc'
    },
    SOURCE_LIST_2D : [],
    /**
     * 2D 지도에 등록되어 있는 layer 목록
     */
    layerList2D : new jsMap(),
    setLayerList2d : function setLayerList2d(feature) {
        this.layerList2D.put(feature.getLayerId(), '');
    },
    getLayerList2d : function getLayerList2d() {
        return this.layerList2D;
    },
    removeLayerList2d : function removeLayerList2d(id) {
        this.layerList2D.remove(id);
    },
    /**
     * 3D 지도에 등록되어 있는 3D 객체(모델)
     */
    tileset3D : new jsMap(),
    /**
     * 지도에서 관리되고 있는 전체 도시요소 객체 정보
     */
    globalFeatures : new jsMap(),
    setGlobalFeatures : function setGlobalFeatures(feature) {
        this.globalFeatures.put(feature.getFeatureId(), feature);
    },
    getGlobalFeatures : function getGlobalFeatures() {
        return this.globalFeatures;
    },
    removeGlobalFeatures : function removeGlobalFeatures(feature) {
        this.globalFeatures.remove(feature.getFeatureId());
    },
    btlRnkId :{},
    /**
     * 도시요소 최신화 점멸 객체 정보
     */
    flashFeatures : new jsMap(),
    setFlashFeatures : function setFlashFeatures(feature, value) {
        this.flashFeatures.put(feature.getFeatureId(), value);
    },
    getFlashFeatures : function getFlashFeatures() {
        return this.flashFeatures;
    },
    STYLE_2D_LAYOUT : {
        VISIBILITY : {
            name : 'visible',
            'default' : 'visible'
        },
        LINE : {
            CAP : {
                name : 'line-cap',
                'default' : 'butt'
            },
            JOIN : {
                name : 'line-join',
                'default' : 'miter'
            }
        },
        SYMBOL : {

        }
    },
    STYLE_2D_PAINT : {
        LINE : {
            COLOR : {
                name : 'line-color',
                'default' : '#000000'
            },
            WIDTH : {
                name : 'line-width',
                'default' : 1
            },
            OPACITY : {
                name : 'line-opacity',
                'default' : 1
            },
            DASHARRAY : {
                name : 'line-dasharray'
            },
            PATTERN : {
                name : 'line-pattern'
            }
        },
        CIRCLE : {
            RADIUS : {
                name : 'circle-radius',
                'default' : 5
            },
            COLOR : {
                name : 'circle-color',
                'default' : '#000000'
            },
            BLUR : {
                name : 'circle-blur',
                'default' : 0
            },
            OPACITY : {
                name : 'circle-opacity',
                'default' : 1
            },
            TRANSLATE : {
                name : 'circle-translate',
                'default' : [0, 0]
            },
            STROKE_WIDTH : {
                name : 'circle-stroke-width',
                'default' : 0
            },
            STROKE_COLOR : {
                name : 'circle-stroke-color',
                'default' : '#000000'
            },
            STROKE_OPACITY : {
                name : 'circle-stroke-opacity',
                'default' : 1
            }
        },
        SYMBOL : {
            OPACITY : {
                name : 'icon-opacity',
                'default' : 1
            }
        },
        FILL : {
            COLOR : {
                name : 'fill-color',
                'default' : '#000000'
            },
            OPACITY : {
                name : 'fill-opacity',
                'default' : 1
            }
        }
    },
    /**
     * 3D 일 경우 '0'
     * 2D 일 경우 '1'
     */
    PRESENT_MAP_KIND : undefined,
    MAP_KIND : {
        MAP_3D : 0,
        MAP_2D : 1
    },
    MINI_MAP : undefined,
    /**
     * true : 3D, false : 2D
     */
    checkMapKind : function checkMapKind() {
        return this.PRESENT_MAP_KIND === this.MAP_KIND.MAP_3D;
    },
    /**
     * 상황도 서버 도메인 정보 세팅
     */
    setDomain : function setDomain(domain) {
        this.SERVER_DOMAIN = domain;
    },
    /**
     * 지도 서버 PORT 세팅
     */
    setMapPort : function setMapPort(port) {
        this.SERVER_MAP_PORT = port;
    },
    /**
     * 이미지 서버 PORT 세팅
     */
    setImgPort : function setImgPort(port) {
        this.SERVER_IMG_PORT = port;
    },
    // 상황도 직책 구분 코드
    byId : false,
    /**
     * 직책 구분 코드 세팅
     */
    setById : function setById(cd) {
        this.byId = cd === 'CM';
    },
    getById : function getById() {
        return this.byId;
    },
    /**
     * Map 객체
     */
    mapObject : undefined,
    /**
     * 군대부호 객체
     */
    ms : undefined,
    /**
     * 군대부호 객체 가져오기
     */
    getMilsym : function getMilsym() {
        return this.ms;
    },
    /**
     * 군대부호 객체 세팅
     */
    setMilSym : function setMilSym(ms) {
        this.ms = ms;
    },
    /**
     * 군대부호 객체 생성
     */
    setSymbolInfo : function setSymbolInfo(sidc, options) {
        var _options = {};

        if (!this.valid.checkValue(options)) {
            options = {};
        }

        _options.additionalInformation =
            this.valid.defaultValue(options.additionalInformation, '');    // 추가 사항
        _options.altitudeDepth =
            this.valid.defaultValue(options.altitudeDepth, '');            // 고도
        _options.civilianColor =
            this.valid.defaultValue(options.civilianColor, true);            // 민가
        _options.colorMode = this.valid.defaultValue(options.colorMode, 'Light');                // 명도
        _options.combatEffectiveness =
            this.valid.defaultValue(options.combatEffectiveness, '');      // 전투효과(전투력)
        _options.direction = this.valid.defaultValue(options.direction, '');                // 이동 방향
        _options.dtg = this.valid.defaultValue(options.dtg, '');                      // 활동시각(DTG)
        _options.evaluationRating =
            this.valid.defaultValue(options.evaluationRating, '');         // 평가 등급
        _options.fill = this.valid.defaultValue(options.fill, true);                     // 색 채움
        _options.fillOpacity = this.valid.defaultValue(options.fillOpacity, 1);              // 색 채움 투명도
        _options.frame = this.valid.defaultValue(options.frame, true);                    // 외형
        _options.higherFormation =
            this.valid.defaultValue(options.higherFormation, '');          // 상급 부대
        _options.hostile = this.valid.defaultValue(options.hostile, '');                  // 적군 표시
        _options.hqStafLength =
            this.valid.defaultValue(options.hqStafLength, 100);             //
        _options.icon = this.valid.defaultValue(options.icon, true);                     //
        _options.iffSif = this.valid.defaultValue(options.iffSif, '');                   // 피아식별 모드 및 코드
        _options.infoColor = this.valid.defaultValue(options.infoColor, '');                // 수식 정보 색
        _options.infoFields = this.valid.defaultValue(options.infoFields, true);               // 수식 정보
        _options.infoSize = this.valid.defaultValue(options.infoSize, '40');                 // 수식 정보 크기
        _options.location = this.valid.defaultValue(options.location, '');
        _options.monoColor = this.valid.defaultValue(options.monoColor, '');
        _options.outlineColor =
            this.valid.defaultValue(options.outlineColor, '#efefef');
        _options.outlineWidth = this.valid.defaultValue(options.outlineWidth, '1');
        _options.quantity = this.valid.defaultValue(options.quantity, '');
        _options.reinforcedReduced =
            this.valid.defaultValue(options.reinforcedReduced, '');
        _options.signatureEquipment =
            this.valid.defaultValue(options.signatureEquipment, '');
        _options.size = this.valid.defaultValue(options.size, 37);
        _options.specialHeadquarters =
            this.valid.defaultValue(options.specialHeadquarters, '');
        _options.speed = this.valid.defaultValue(options.speed, '');
        _options.staffComments = this.valid.defaultValue(options.staffComments, '');
        _options.strokeWidth = this.valid.defaultValue(options.strokeWidth, '3');
        _options.type = this.valid.defaultValue(options.type, '');
        _options.uniqueDesignation =
            this.valid.defaultValue(options.uniqueDesignation, '');

        _options.SIDC = sidc;

        return new this.ms.Symbol(sidc).setOptions(_options);
    },
    /**
     *
     */
    mapExtents : undefined,
    /**
     * 지도 영역 설정
     */
    setMapExtents : function setMapExtents(east, north, south, west) {
        this.mapExtents = {};
        this.mapExtents.east = east;        // 동
        this.mapExtents.north = north;      // 북
        this.mapExtents.south = south;      // 남
        this.mapExtents.west = west;        // 서
    },
    /**
     * 현재 Map 의 Extents
     */
    getMapExtents : function getMapExtents() {
        this.mapObject.getExtents();

        return this.mapExtents;
    },
    /**
     * 현재 Map 의 중심좌표
     * @returns {[number, number]}
     */
    getMapCenter : function getMapCenter() {
        this.mapObject.getExtents();
        var lon = (this.mapExtents.east + this.mapExtents.west) / 2;
        var lat = (this.mapExtents.north + this.mapExtents.south) / 2;

        return [lon, lat];
    },
    /**
     * 사각형의 중심좌표
     * @returns {[number, number]}
     */
    getCenter : function getCenter(mapExtents) {
        var lon = (mapExtents.east + mapExtents.west) / 2;
        var lat = (mapExtents.north + mapExtents.south) / 2;

        return [lon, lat];
    },
    /**
     * 좌표로 이동
     */
    setMapCenter : function setMapCenter(lon, lat, height) {
        if (!this.checkMapKind()) {
            this.mapObject.setCenter([lon, lat]);
        }else{
            this.mapObject.flyTo(lon, lat, height);
        }

    },
    Change3dObject : function Change3dObject(e) {
        this.mapObject.map.scene.primitives.show = e;
    },
    /**
     * 유효성 확인
     */
    valid : {
        /**
         * mgrs 유효성 확인 (자릿수 및 형태)
         * 52SBH6811675651 -> mgrs 객체
         */
        mgrs : function mgrs(mgrs) {
            return geoTrans.Mgrs.parse(mgrs) instanceof geoTrans.Mgrs;
        },
        /**
         * UTM 좌표 유효성 확인
         * 52N2874684261776 -> utm 객체
         */
        utm : function utm(coordinate) {
            return geoTrans.Utm.parse(coordinate) instanceof geoTrans.Utm;
        },
        /**
         *
         * @param point
         */
        lonLat : function lonLat(point) {
            return (point.lon > -180 && point.lon < 180) && (point.lat > -90 && point.lat < 90);
        },
        /**
         * polygon 좌표 유효성 확인
         * result : result (확인 결과, true | false), message (결과 이유)
         */
        polygon : function polygon(coordinates) {
            var _firstCoord = coordinates[0];
            var _lastCoord = coordinates[coordinates.length - 1];

            return _firstCoord[0] === _lastCoord[0] && _firstCoord[1] === _lastCoord[1];
        },
        /**
         * 값의 유무 판단
         * true : 값 있음
         * false : 값 없음
         */
        checkValue : function checkValue(a) {

            if(a === null || a === undefined || a === "" || a === ''){
                return false;
            }else if( a !== null && typeof a === "object" && Object.keys(a).length < 1 ){
                return false;
            }else return !(Array.isArray(a) && a.length < 1);

        },
        /**
         * 값이 없을 시 기본값으로 설정
         */
        defaultValue : function defaultValue(a, b) {
            if (a !== null && a !== undefined) {
                return a;
            }
            return b;
        },

        /**
         * target 문자열이 keywod로 시작하는지 체크
         */
        startWith : function startWith(target, keyword) {
            return target.indexOf(keyword)==0;
        }
    },
    /**
     * DeepCopy
     */
    cloneDeep : function cloneDeep(obj){
        return JSON.parse(JSON.stringify(obj));
    },
    /**
     * 좌표변환 공통 함수
     */
    convert : {
        /**
         * 경위도 -> 군사좌표
         * @param {string|number} lon   경도 - 126.87456
         * @param {string|number} lat   위도 - 38.5658
         * @param {number} [digits - 초기값 10] 6, 8, 10
         * @returns {string}    군사좌표 - '52S CH 14825 70736'
         * var mgrs = stmp.convert.lonLatToMgrs(126.87456, 38.5658, 10);
         */
        lonLatToMgrs : function lonLatToMgrs(lon, lat, digits) {
            if (digits === undefined) {
                digits = 10;
            }
            return new geoTrans.LatLon(lon, lat).toUtm().toMgrs().toString(digits);
        },
        /**
         * 경위도 -> UTM
         * @param {string|number} lon   경도 - 126.87456
         * @param {string|number} lat   위도 - 38.5658
         * @returns {string} UTM - '52 N 314825 4270736'
         * var utm = stmp.convert.lonLatToUtm(126.87456, 38.5658);
         */
        lonLatToUtm : function lonLatToUtm(lon, lat) {
            return new geoTrans.LatLon(lon, lat).toUtm().toString();
        },
        /**
         * 경위도 -> GeoRef
         * @param {string|number} lon   경도 - 126.87456
         * @param {string|number} lat   위도 - 38.5658
         * @returns {string} GeoRef - 'WJGJ52283357'
         * var geoRef = stmp.convert.lonLatToGeoRef(126.87456, 38.5658);
         */
        lonLatToGeoRef : function lonLatToGeoRef(lon, lat) {
            return geoTrans.GeoRef.lonLatToGeoRef(lon, lat);
        },
        /**
         * 경위도 -> Gars
         * @param {string|number} lon   경도 - 126.87456
         * @param {string|number} lat   위도 - 38.5658
         * @param {string} precision    '5' | '15' | '30'
         * var gars = stmp.convert.lonLatToGars(126.87456, 38.5658, '30');
         */
        lonLatToGars : function lonLatToGars(lon, lat, precision) {
            if (precision === undefined) {
                precision = '30';
            }
            return geoTrans.Gars.lonLatToGars(lon, lat, precision);
        },
        /**
         * 경위도 -> 화면좌표
         * @param point point.lon, point.lat
         * @returns {*}
         * var point = {};
         * point.lon = 127;
         * point.lat = 37;
         * var result = stmp.convert.lonLatToPixel(point);
         */
        lonLatToPixel : function lonLatToPixel(point) {
            return this.mapObject.unproject(point);
        },
        /**
         * 군사좌표 -> 경위도
         * @param mgrs  군사좌표 - '52SBG6656882015'
         * var lonLat = stmp.convert.mgrsToLonLat('52SBG6656882015');
         */
        mgrsToLonLat : function mgrsToLonLat(mgrs) {
            return geoTrans.Mgrs.parse(mgrs).toUtm().toLatLonE();
        },
        /**
         * 군사좌표 -> UTM
         * @param mgrs  군사좌표 - '52SBG6656882015'
         * var utm = stmp.convert.mgrsToUtm('52SBG6656882015');
         */
        mgrsToUtm : function mgrsToUtm(mgrs) {
            return geoTrans.Mgrs.parse(mgrs).toUtm();
        },
        /**
         * UTM -> 경위도
         * @param utm - '52N2874684261776'
         * var lonLat = stmp.convert.utmToLonLat('52N2874684261776');
         */
        utmToLonLat : function utmToLonLat(utm) {
            return geoTrans.Utm.parse(utm).toLatLonE();
        },
        /**
         * UTM -> 군사좌표
         * @param utm - '52N2874684261776'
         * @param {number} [digits - 초기값 10] 6, 8, 10
         * var mgrs = stmp.convert.utmToMgrs('52N2874684261776', 10);
         */
        utmToMgrs : function utmToMgrs(utm, digits) {
            if (digits === undefined) {
                digits = 10;
            }
            return geoTrans.Utm.parse(utm).toMgrs().toString(digits);
        },
        /**
         * GeoRef -> 경위도
         * @param geoRef - 'WJHG47183558'
         * var lonLat = stmp.convert.geoRefToLonLat('WJHG47183558');
         */
        geoRefToLonLat : function geoRefToLonLat(geoRef) {
            return geoTrans.GeoRef.geoRefToLonLat(geoRef);
        },
        /**
         * Gars -> 경위도
         * @param gars - '608LS47'
         * var lonLat = stmp.convert.garsToLonLat('608LS47');
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
         * var result = stmp.convert.pixelToLonLat(point);
         */
        pixelToLonLat : function pixelToLonLat(point) {
            return this.mapObject.project(point);
        },
        degreesToMeters : function degreesToMeters(lon, lat) {
            var x = lon * 20037508.34 / 180;
            var y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
            y = y * 20037508.34 / 180;

            return [x, y];
        },
        metersToDegrees : function metersToDegrees(x, y) {
            var lon = x * 180 / 20037508.34;
            var lat = Math.atan(Math.exp(y * Math.PI / 20037508.34)) * 360 / Math.PI - 90;

            return {
                'lon' : lon,
                'lat' : lat
            }
        }
    },
    /**
     * 보조자료 전시
     */
    displaySupportInfo : function displaySupportInfo(lon, lat, height) {
        lon = Number(lon).toFixed(6);
        lat = Number(lat).toFixed(6);

        var coordsText = '경위도 : ' + lon + ' , ' + lat;
        var heightText;

        if (height === undefined) {
            heightText = '고도값 : 0 m';
        } else {
            heightText = height;
        }

        var latLon = new geoTrans.LatLon(lon, lat);
        var utm = latLon.toUtm();
        var mgrs = utm.toMgrs();

        var mgrsText = '군사좌표 : ' + mgrs.toString();
        var utmText = 'UTM : ' + utm.toString();
        var geoRefText = 'GEOREF : ' + geoTrans.GeoRef.lonLatToGeoRef(lon, lat);

        if(typeof mf_spn_coordinate !== "undefined"){
            mf_spn_coordinate.setLabel(coordsText);
            mf_spn_coordinateMgrs.setLabel(mgrsText);
            mf_spn_coordinateUtm.setLabel(utmText);
            mf_spn_coordinateGeoRef.setLabel(geoRefText);
            mf_spn_height.setLabel(heightText);
        }

    },
    onClassCheck : function onClassCheck(className, that) {
        $('.' + className).each(function() {
            if ($(this).hasClass('on')) {
                $(this).removeClass('on');
            }
        });

        that.addClass('on');
    },
    /**
     * 숫자 체크
     * @param code
     * @returns {boolean}
     * stmp.isDigit(char.charCodeAt(index))
     */
    isDigit : function isDigit(code) {
        return code > 47 && code < 58;
    },
    /**
     * 숫자 체크
     * @param {*} num
     * @returns {boolean}
     */
    isNumber : function isNumber(num) {
        return !isNaN(num) && num !== null && !Array.isArray(num);
    },
    /**
     * Object 체크
     * @param {*} obj
     * @returns {boolean}
     */
    isObject : function isObject(obj) {
        return (!!obj) && (obj.constructor === Object);
    },
    /**
     * 작은 팝업
     */
    openSMTAlert : function(url, width, title, id, modal, top, left, dataValue, callbackFnNm) {
        var data = {
            "isReturnValue" : true,
            "messageType" : "popup",
            "data" : dataValue,
            "callbackFn" : callbackFnNm,
        };
        var options = {
            id : id,
            popupName : title,
            width : width,
            classType : "2",
            modal : modal,
            top : top,
            left : left
        };

/*        if( !(callbackFnNm === null || callbackFnNm === undefined)) {
            data['callbackFn'] = callbackFnNm;
        }*/

        com.openPopup(url, options, data);
    },
    /**
     * 큰 팝업
     */
    openSMTPopup : function(url, width, height, id, top, left) {
        var data = {
            "isReturnValue" : false,
            "messageType" : "popup"
        };
        var options = {
            id : id,
            width : width,
            height : height,
            classType : "1",
            top : top,
            left : left
        };
        com.openPopup(url, options, data);
    },
    /**
     *
     * @param radians   {number} angle in radians
     * @returns {number} 0 and 360
     */
    toDegrees : function toDegrees(radians) {
        var degrees = radians % (2 * Math.PI);
        return (degrees * 180) / Math.PI;
    },
    /**
     *
     * @param degrees   {number} 0 and 360
     * @returns {number}
     */
    toRadians : function toRadians(degrees) {
        var radians = degrees % 360;
        return (radians * Math.PI) / 180;
    },
    /**
     * p0 -> p1 의 각도
     * @param p0 {[]}   [x, y]
     * @param p1 {[]}   [x, y]
     * @returns {number}
     */
    angle : function angle(p0, p1) {
        var dx = p1[0] - p0[0];
        var dy = p1[1] - p0[1];
        return Math.atan2(dy, dx);
    },
    /**
     * 두점 사이의 거리 측정 (경위도)
     * @param p1    {[]} [127, 34]
     * @param p2    {[]} [128, 34]
     * @param units {string} 'kilometers', 'meters'
     * @returns {number}
     */
    distance : function distance(p1, p2, units) {
        if (units === undefined) {
            units = 'kilometers';
        }

        var line = turf.lineString([[p1[0], p1[1]], [p2[0], p2[1]]]);

        return turf.lineDistance(line, {units : units});
    },
    DRAWING_TYPE_KIND : {

    },
    DRAWING_MODE : undefined,       // 사용자가 선택 한 그리기 모드 (POINT, LINE, POLYGON)
    /**
     * draw 공통 함수
     * 현재 미사용
     * 추후 기본적인 draw 함수로 정의
     * 점, 선, 면, 원
     * @param params
     */
    draw : function draw(params) {
        if (!stmp.valid.checkValue(params.drawKind)) {
            throw new Error('drawKind 값이 없습니다.');
        }
        if (!stmp.valid.checkValue(params.position)) {
            throw new Error('좌표값이 없습니다.');
        }

        var _drawKind = params.drawKind;
        var _position = params.position;            // 좌표 정보
        var _params = {};

        switch (_drawKind) {
            case stmp.DRAW_TYPE_KIND.POINT.CD :            // 점
                this.mapObject.drawPoint(_params);
                break;
            case stmp.DRAW_TYPE_KIND.LINE.CD :             // 선
                this.mapObject.drawLine(_params);
                break;
            case stmp.DRAW_TYPE_KIND.POLYGON.CD :          // 폴리곤
                this.mapObject.drawPolygon(_params);
                break;
            case stmp.DRAW_TYPE_KIND.CIRCLE.CD :           // 원
                break;
            default:
                throw new Error('');
        }
    },
    addFeature : function addFeature(params) {
        // 2D 일 경우 addLayer
        // 3D 일 경우 entities
        // 2D, 3D 처음 호출 함수는 addFeature

        var _options = {};  // map 에 넘길 파라미터

        // 객체 id 체크
        if (!stmp.valid.checkValue(params.id)) {
            throw new Error(this.MESSAGE.ERROR_FEATURE_ID);
        }
        // 객체 source (레이어 ID) 체크
        if (!stmp.valid.checkValue(params.layerId)) {
            throw new Error(this.MESSAGE.ERROR_LAYER_ID);
        }
        // 객체 타입 정보 체크
        if (!stmp.valid.checkValue(params.type)) {
            throw new Error(this.MESSAGE.ERROR_FEATURE_TYPE);
        }
        // 객체 좌표 정보 체크
        if (!stmp.valid.checkValue(params.coordInfo)) {
            throw new Error('좌표 정보가 없습니다.');
        }

        // 객체 좌표 정보 타입 체크
        if (!stmp.valid.checkValue(params.coordInfo.type)) {
            throw new Error('좌표 타입 정보가 없습니다.');
        }

        // 객체 좌표 정보 좌표열 체크
        if (!stmp.valid.checkValue(params.coordInfo.coords)) {
            throw new Error('좌표열 정보가 없습니다.');
        }

        // 객체 스타일 정보 체크
        if (!this.valid.checkValue(params.styleInfo)) {
            throw new Error(this.MESSAGE.ERROR_STYLE_INFO);
        }

        if (!this.valid.checkValue(params.styleInfo.milsymbol) && !this.valid.checkValue(params.styleInfo.style)) {
            throw new Error('스타일 정보 또는 군대부호 정보가 있어야 합니다.');
        }

        _options.id = params.id;
        _options.layerId = params.layerId;
        _options.coordInfo = params.coordInfo;
        _options.styleInfo = params.styleInfo;
        _options.type = params.type;
        // 화면좌표 체크
        if (this.valid.checkValue(params.pixel)) {
            _options.pixel = params.pixel;
        }

        // callback 함수 체크
        if (this.valid.checkValue(params.callBackFN)) {
            _options.callBackFN = params.callBackFN;
        }

        // overlayData 체크
        if (this.valid.checkValue(params.overlayData)) {
            _options.overlayData = params.overlayData;
        }

        var feature = new jiFeature(_options);
        if(feature.getFeatureId()){
        this.btlRnk = feature.getFeatureId();
        }
        this.setGlobalFeatures(feature);

        if (!this.checkMapKind()) {
            if (!this.mapObject.getSource(_options.layerId)) {
                this.mapObject.addSource(_options.layerId, {
                    'type' : jiConstant.MAPBOX_SOURCE_TYPE.GEOJSON,
                    'data' : {
                        'type' : 'FeatureCollection',
                        'features' : []
                    }
                });
            }

            this.mapObject.addFeature(feature);
        } else {
            this.mapObject.addFeature(feature);
        }

        if (!this.checkMapKind()) {
            //20200128 jmk 추가
            stmp.getGlobalFeatures().keys().map(function(e) {
                try{
                    var feature = stmp.getGlobalFeatures().get(e);
                    stmp.mapObject.map.on('mouseenter', feature.getLayerId(), function(e){
                        if(stmp.overlayYn == 'Y'){
                            var canvas = stmp.mapObject.map.getCanvasContainer();
                            canvas.style.cursor = "pointer";
                        }
                    });
                    stmp.mapObject.map.on('mouseleave', feature.getLayerId(), function(e){
                        if(stmp.overlayYn == 'Y'){
                            var canvas = stmp.mapObject.map.getCanvasContainer();
                            canvas.style.cursor = "";
                        }
                    });
                    stmp.mapObject.map.on('contextmenu', feature.getLayerId(), function(e){

                        var canvas = stmp.mapObject.map.getCanvasContainer();
                        canvas.style.cursor = "";
                        window._moveYn = false;
                        var layerId = feature.getLayerId();
                        var features = stmp.mapObject.map.queryRenderedFeatures(e.point);
                        var jsonArr = new Array();
                        var jsonArr2 = new Array(); //subMenu생성

                        if (features != null && features != '') {
                            _featureObj = features[0];
                        }
                        if (stmp.mapObject.map.getLayer(layerId)) {
                            if (layerId.slice(0, 2) == 'SF' || layerId.slice(0, 2) == 'SH') {
                                var addJSON = {
                                    "title" : "전투서열 연결관계 도시",
                                    "func" : _runTimeId + ".fn_btlRank",
                                    "div" : "2", // 기능
                                    "param" : ""
                                };
                                jsonArr.push(addJSON);

                                if (layerId.indexOf("btlRnk_BASE_MILSYMBOL") > -1) {
                                    var deleteJSON = {
                                        "title" : "전투서열 연결관계 삭제",
                                        "func" : _runTimeId + ".fn_deletebtlRnk",
                                        "div" : "2", // 기능
                                        "param" : ""
                                    };
                                    jsonArr.push(deleteJSON);
                                }

                                if (layerId.slice(0, 2) == 'SH') { //적군
                                    // 적부대위치이동 컨텍스트 메뉴 추가
                                    _ctxtItem.enmyUnitMove.func = _runTimeId + ".fn_enmyUnitMove";
                                    jsonArr.push(_ctxtItem.enmyUnitMove);
                                }
                            }
                            if (layerId.slice(0, 6) == 'MICA01' ) {
                                var addJSON = {
                                    "title" : "전자전투서열",
                                  "func" : _runTimeId + ".fn_eobPopop",
                                    "div" : "2", // 기능
                                    "param" : ""
                                };
                            }
                                jsonArr.push(addJSON);
                            if(jsonArr.length > 0){
                                _scwin2DMap.layerInit(e, jsonArr, jsonArr2);
                            }

                        }
                    });
                } catch(e){
                    console.log(e);
                }
            });

        }

    },
    addDitemFeatures : function addDitemFeatures(params) {
        if (!this.valid.checkValue(params.data)) {
            console.log('data 항목이 없습니다.');
            return;
        }
        var _type = params.type;
        var _datas = params.data;

        var _features = [];
        var _layer = undefined;
        var _stmpLayerId = undefined;
        var _feature = undefined;

        _datas.map(function(e) {
            var _options = {
                id : e['ditemIdtfId'],
                layerId : 'testLayer',
                type : stmp.DRAW_TYPE_KIND.BASE_MILSYMBOL,
                coordInfo : {
                    type : stmp.COORDINATE_SYSTEM.MGRS,
                    coords : [e['ditemCordc']]
                },
                styleInfo : {
                    milsymbol : {
                        sdic : e['ditemCd'],
                        options : {}
                    },
                    style : {
                        size : 1
                    }
                }
            };

            var feature = new jiFeature(_options);

            stmp.setGlobalFeatures(feature);

            _features.push(feature.getGeoJson());

            if (!stmp.checkMapKind()) {     // 현재 map 이 2D 일 경우
                stmp.mapObject._addImage(feature);  // 이미지 등록
            }

            if (!stmp.valid.checkValue(_layer)) {
                _layer = feature.getLayer();
            }
            if (!stmp.valid.checkValue(_stmpLayerId)) {
                _stmpLayerId = feature.getStmpLayerId();
            }
            if (!stmp.valid.checkValue(_feature)) {
                _feature = feature;
            }
        });

        if (!this.checkMapKind()) {
            if (!this.mapObject.getSource(_stmpLayerId)) {
                this.mapObject.addSource(_stmpLayerId, {
                    'type' : jiConstant.MAPBOX_SOURCE_TYPE.GEOJSON,
                    'data' : {
                        'type' : 'FeatureCollection',
                        'features' : _features
                    }
                });
            } else {
                this.mapObject._addGeoJsonDatas(_stmpLayerId, _features);
            }

            var sourceLoadFn = function () {
                stmp.mapObject.removeEvent('sourcedata', sourceLoadFn);
                stmp.mapObject.addLayer(_feature);
            };

            this.mapObject.addEvent('sourcedata', sourceLoadFn);
        }
    },
    addDitemFlashFeature : function addDitemFeature(params) {
        if (!this.valid.checkValue(params.data)) {
            console.log('data 항목이 없습니다.');
            return;
        }

        var _datas = params.data;

        _datas.map(function(e) {
            var _options = {
                id : e['ditemIdtfId'],
                layerId : 'testLayer',
                type : stmp.DRAW_TYPE_KIND.BASE_MILSYMBOL,
                coordInfo : {
                    type : stmp.COORDINATE_SYSTEM.MGRS,
                    coords : [e['ditemCordc']]
                },
                styleInfo : {
                    milsymbol : {
                        sdic : e['ditemCd'],
                        options : {}
                    },
                    style : {
                        size : 1
                    }
                }
            };

            var feature = new jiFeature(_options);

            stmp.setFlashFeatures(feature, 0);

            stmp.setGlobalFeatures(feature);            // 전역 feature 변수에 저장

            stmp.mapObject.addFeature(feature);

            window.setTimeout(stmp.flashEvent, 1000, feature.getFeatureId());
        });

        stmp.ditem.removeDitemLts();        // ditem 객체 초기화
    },
    /**
     *
     * @param feature
     */
    getFeature : function getFeature(feature) {
        var json = this.mapObject.getFeature(feature);
        // json 의 id 값을 읽어 globalFeatures 에 있는 feature 객체를 반환

    },
    /**
     * 도시 되어 있는 객체를 제거 한다
     * @param feature
     */
    removeMap : function removeMap() {

        this.mapObject.removeMap();     // 전체 도시요소 객체 정보에서 객체 삭제
        this.setMapSource('world:truemarble');
    },
    /**
     * 도시 되어 있는 객체를 제거 한다
     * @param feature
     */
    removeFeature : function removeFeature(feature) {
        this.mapObject.removeFeature(feature);  // 지도에서 객체 삭제

        this.removeGlobalFeatures(feature);     // 전체 도시요소 객체 정보에서 객체 삭제
    },
    /**
     * 도시 되어 있는 객체들을 제거 한다
     * @param features
     */
    removeFeatures : function removeFeatures(features) {
        for (var i = 0; i < features.length; i++) {
            this.removeFeature(features[i]);
        }
    },
    /**
     * 이벤트 등록
     * @param type
     * @param fn
     */
    addEvent : function addEvent(type, fn) {
        this.mapObject.addEvent(type, fn);
    },
    /**
     * 이벤트 제거
     * @param type
     * @param fn
     */
    removeEvent : function removeEvent(type, fn) {
        this.mapObject.removeEvent(type, fn);
    },
    /**
     * 도시요소 최신화 점멸 이벤트
     * @param featureId
     */
    flashEvent : function flashEvent(featureId) {
        if (stmp.getFlashFeatures().containsKey(featureId) && stmp.getGlobalFeatures().containsKey(featureId)) {
            var feature = stmp.getGlobalFeatures().get(featureId);

            var count = stmp.getFlashFeatures().get(featureId);
            count++;

            feature.getProperties().isVisible = (count % 2) === 0;

            if (stmp.checkMapKind()) {          // 3D
                var entity = stmp.mapObject.getFeature(featureId);
                entity.show = feature.getProperties().isVisible;
            } else {                            // 2D
                stmp.mapObject._modifyGeoJsonData(feature);
            }

            if ((count / 2) !== stmp.getDitemLtsFlashNum()) {
                stmp.getFlashFeatures().put(featureId, count);
                stmp.getGlobalFeatures().put(featureId, feature);
                window.setTimeout(stmp.flashEvent, 1000, featureId);
            } else {
                stmp.getFlashFeatures().remove(featureId);
                window.clearTimeout(this);
            }
        }
    },
    resetMap : function resetMap() {
        this.mapObject.reset();
    },
    /**
     * 간략부호 적용
     * 기본군대부호 일 경우만 적용
     */
    changeSignCnvsnLink : function changeSignCnvsnLink() {
        var prop = 0.5;
        if (this.checkMapKind()) {      // 3D
            this.getGlobalFeatures().keys().map(function(e) {
                var feature = stmp.getGlobalFeatures().get(e);

                if (feature.getTypeCd() === stmp.DRAW_TYPE_KIND.BASE_MILSYMBOL.CD) {
                    var id = feature.getFeatureId();

                    if (stmp.mapObject.hasEntity(id)) {
                        if (!stmp.getSignCnvsnLink()) {
                            prop = feature.getProperties().size;
                        }

                        stmp.mapObject.getFeature(id).billboard.scale.setValue(prop);
                    }
                }
            });
        } else {                        // 2D
            if (!this.getSignCnvsnLink()) {     // 버튼 Off 일 경우
                prop = ['get', 'size'];
            }

            this.getLayerList2d().keys().map(function(e) {
                if (e.lastIndexOf(stmp.DRAW_TYPE_KIND.BASE_MILSYMBOL.LAYER_NAME) > -1) {
                    stmp.mapObject.setLayoutProperty(e, 'icon-size', prop);
                }
            });
        }
    },
    /**
     * 군대부호 자동조절 여부 / 값
     */
    warsblInfo : {
        YN : 'N',
        value : 1
    },
    getWarsblYn : function getWarsblYn() {
        return this.warsblInfo.YN === 'Y';
    },
    setWarsblYn : function setWarsblYn(value) {
        this.warsblInfo.YN = value.toLocaleUpperCase();
    },
    setWarsblValue : function setWarsblValue(value) {
        this.warsblInfo.value = Number(value);
    },
    getWarsblValue : function getWarsblValue() {
        return this.warsblInfo.value;
    },
    /**
     * 군대부호 자동조절
     * 기본군대부호 일 경우만 적용
     */
    changeWarsblAuto : function changeWarsblAuto() {
        var _value = this.getWarsblValue();
        if (this.checkMapKind()) {      // 3D
            this.getGlobalFeatures().keys().map(function(e) {
                var feature = stmp.getGlobalFeatures().get(e);

                if (feature.getTypeCd() === stmp.DRAW_TYPE_KIND.BASE_MILSYMBOL.CD) {
                    var id = feature.getFeatureId();

                    if (!stmp.getWarsblYn()) {  // 크기 조절을 안했을 경우
                        _value = feature.getProperties().size;
                    }

                    if (stmp.mapObject.hasEntity(id)) {
                        stmp.mapObject.getFeature(id).billboard.scale.setValue(_value);
                    }
                }
            });
        } else {                        // 2D
            if (!stmp.getWarsblYn()) {     // 크기 조절을 안했을 경우
                _value = ['get', 'size'];   // properties 에 정의 되어 있는 size 참조
            }

            this.getLayerList2d().keys().map(function(e) {
                if (e.lastIndexOf(stmp.DRAW_TYPE_KIND.BASE_MILSYMBOL.LAYER_NAME) > -1) {
                    stmp.mapObject.setLayoutProperty(e, 'icon-size', _value);
                }
            });
        }
    },
    /**
     * 군대부호 자동조절 초기화 함수
     */
    resetWarsblAuto : function resetWarsblAuto() {
        stmp.setWarsblYn('N');
        stmp.setWarsblValue(1);
        this.changeWarsblAuto();
    },
    /**
     * 2D -> 3D or 3D -> 2D 로 변경 시 호출 함수
     * globalFeatures 객체에 값이 있을 경우
     * 3D 일 경우 1개의 entities 에 모든 객체를 입력
     * 2D 일 경우 source 그룹, layer 그룹을 나눠서 입력
     */
    changeMapDrawFeatures : function changeMapDrawFeatures() {
        if (stmp.getGlobalFeatures().size() > 0) {
            console.log('layer 구성 시작');
            if (this.getLayerList2d().size() > 0) {     // 2D 에 등록되어 있는 layer 정보 제거
                this.getLayerList2d().clear();
            }

            this.mapObject.changeMapDrawFeatures(this.getGlobalFeatures());
            console.log('layer 구성 완료');
        }
    },
    /**
     * 현재 지도의 base map 을 변경 한다
     */
    changeBaseMap : function changeBaseMap(value) {
        this.setMapSource(value);
        this.mapObject.changeBaseMap();
    },
    ditem : {
        ditemLts : [],
        ditemLtsYn1 : {},
        ditemLtsDttm : function ditemLtsDttm() {

        },
        setDitemLts : function setDitemLts(data) {
            this.ditemLts = data;

        },
        getDitemLts : function getDitemLts() {
            return this.ditemLts;
        },
        removeDitemLts : function removeDitemLts() {
            this.ditemLts = [];
        },
        setTestData : function setTestData() {
            var baseLon = 125;
            var baseLat = 35;
            for (var i = 0; i < 10000; i++) {
                var lon = baseLon + (Math.cos(i) * 2);
                var lat = baseLat + (Math.sin(i) * 2);
                var mgrs = stmp.convert.lonLatToMgrs(lon, lat);
                var ditemObj = {
                    'ditemId' : 'ditem_' + i,
                    'ditemIdtfId' : 'ditem_' + i,
                    'ditemNm' : 'ditem_' + i,
                    'ditemKndId' : 'SFUD010000000000',
                    'ditemBasicInfoCtnt' : '테스트_' + i,
                    'ditemIdtfKndCd' : '01',
                    'ditemCordc' : mgrs,
                    'ditemCoordAddtnInfoCtnt' : mgrs,
                    'ditemDvsCd' : '01',
                    'ditemCd' : 'SHG-UCIL---D',
                    'bfLtstizeTime' : '2019-12-18 16:19:43'
                };

                this.ditemLts.push(ditemObj);

                if ((i % 1000) === 0) {
                    baseLon = baseLon + 0.2;
                    baseLat = baseLat + 0.2;
                }
            }
        },
        removeTestData : function removeTestData() {
            this.ditemLts = [];
        }
    },

    /******************  투명도 관련 공통 객체 모음 ********************/
    //맵에서 부터 군대부호 대상 투명도 편집 캔버스로 전환
    mapToCanvasMilSymbol : {}
    ,
    //투명도 전환여부
    overlayYn : ''
    ,
    //투명도 편집 여부
    drawMode : false
    ,
    //투명도 캔버스 ID
    d3Canvas : null
    ,
    //군대부호 정보(그려진 부호 편집을 위해)
    milSymEditOjb : null
    ,
    //투명도 편집중 맵이동 여부 체크
    editingToMoveMapYn : false
    ,
    // 공통속성 오브젝트 저장
    svgCommonAttr : null
    ,
    //  서버 저장용
    saveShapeData : null

    /******************** 투명도 관련 객체 끝 **************************/

    /**
     * 우측 문자열 채우기 함수
     */
    , rpad : function rpad(oStr, totalLen, pStr) {
        if ( oStr.length > totalLen ) throw new Error('기존 문자 길이가 전체 길이보다 큽니다.');
        else if ( pStr.length > totalLen ) throw new Error('추가할 문자 길이가 전체 길이보다 큽니다.');

        while( oStr.length < totalLen ){
            oStr += pStr;
        }
        return oStr;
    }
    /**
     * 좌측 문자열 채우기 함수
     */
    , lpad : function lpad(oStr, totalLen, pStr) {
        if ( oStr.length > totalLen ) throw new Error('기존 문자 길이가 전체 길이보다 큽니다.');
        else if ( pStr.length > totalLen ) throw new Error('추가할 문자 길이가 전체 길이보다 큽니다.');

        while( oStr.length < totalLen ){
            oStr = pStr + oStr;
        }
        return oStr;
    }
    /* GRID area */
    , graticulesLabelGenerator : function(evt){
        var options = stmp.mapObject.map._graticules_options
        if(options === undefined || options.graticulesDrawn === true){
            return
        }
        var bounds = stmp.mapObject.map.getBounds()
        options.bounds = [bounds.getWest(),bounds.getSouth(),bounds.getEast(),bounds.getNorth()]
        // 기존 layer 제거 및 숨김(wms)
        var layers = stmp.mapObject.map.getStyle().layers
        jQuery.each(layers, function(idx, layer){
            if(layer.id.indexOf("graticules-") > -1){
                if(layer.type === "raster"){
                    stmp.mapObject.map.setLayoutProperty(layer.id,"visibility","none")
                }else{
                    stmp.mapObject.map.removeLayer(layer.id)
                }
            }
        })
        // 기존 source 제거
        var sources = stmp.mapObject.map.getStyle().sources
        jQuery.each(sources, function(key, source){
            if(key.indexOf("graticules-") > -1){
                if(source.type === "raster"){
                    // nothing
                }else{
                    stmp.mapObject.map.removeSource(key)
                }
            }
        })
        //
        var xmin = options.bounds[0]
        var ymin = options.bounds[1]
        var xmax = options.bounds[2]
        var ymax = options.bounds[3]
        var currZoom = stmp.mapObject.map.getZoom()
        console.log("ZOOM : " , currZoom)
        jQuery.each(options.graticules,function(idx, item){
            var minZoom = item.minZoom
            var maxZoom = item.maxZoom
            if(minZoom >= currZoom || currZoom > maxZoom){
                return
            }
            var type = item.type
            var prefixLayerId = "graticules-" + type + "-" + minZoom + "-" + maxZoom
            // get Style
            var lineWidth = item.lineWidth ? item.lineWidth : 0.3
            var lineColor = item.lineColor ? item.lineColor : '#333333'
            var labelSize = item.labelSize ? item.labelSize : 10
            var labelColor = item.labelColor ? item.labelColor : '#0055ff'

            // use Map Server
            var mapServer = item.mapServer
            if(mapServer){
                var wmsLayers = item.layers
                var labelField = item.labelField
                if(!(wmsLayers instanceof Array)){
                    wmsLayers = [item.layers]
                }
                jQuery.each(wmsLayers,function(idx, wmsLayer){
                    var labelStyle = null
                    if(labelField){
                        labelStyle = "<Rule><TextSymbolizer><Label><ogc:PropertyName>"+labelField+"</ogc:PropertyName></Label><Font>" +
                            "<CssParameter name='font-size'>[LABEL_SIZE]</CssParameter>" +
                            "<CssParameter name='font-weight'>bold</CssParameter></Font>" +
                            "<Halo><Radius>2</Radius><Fill><CssParameter name='fill'>#FFFFFF</CssParameter></Fill></Halo>" +
                            "<Fill><CssParameter name='fill'>[LABEL_COLOR]</CssParameter></Fill><VendorOption name='followLine'>true</VendorOption></TextSymbolizer></Rule>"
                    }
                    var sldBody = "<StyledLayerDescriptor version = '1.0.0' xsi:schemaLocation='http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd' xmlns='http://www.opengis.net/sld' xmlns:ogc='http://www.opengis.net/ogc' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>"
                    sldBody += "<NamedLayer>" +
                        "<Name>[LAYER_NAME]</Name>" +
                        "<UserStyle><FeatureTypeStyle><Rule><LineSymbolizer>" +
                        "<Stroke>" +
                        "<CssParameter name='stroke'>[LINE_COLOR]</CssParameter>" +
                        "<CssParameter name='stroke-width'>[LINE_WIDTH]</CssParameter>" +
                        "<CssParameter name='stroke-dasharray'>10 8</CssParameter>" +
                        "</Stroke>" +
                        "</LineSymbolizer></Rule>"+(labelStyle ? labelStyle : "")+"</FeatureTypeStyle></UserStyle></NamedLayer>";
                    sldBody += "</StyledLayerDescriptor>"
                    sldBody = sldBody.replace(/\[LAYER_NAME\]/gi,wmsLayer);
                    sldBody = sldBody.replace(/\[LINE_WIDTH\]/gi,lineWidth);
                    sldBody = sldBody.replace(/\[LINE_COLOR\]/gi,lineColor);
                    sldBody = sldBody.replace(/\[LABEL_SIZE\]/gi,labelSize);
                    sldBody = sldBody.replace(/\[LABEL_COLOR\]/gi,labelColor);
                    var tileUrl = mapServer + '?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=true&' +
                        'LAYERS=' + wmsLayer + "&SLD_BODY=" + encodeURIComponent(sldBody) +
                        '&exceptions=application%2Fvnd.ogc.se_inimage&SRS=EPSG:3857&WIDTH=256&HEIGHT=256&BBOX={bbox-epsg-3857}'
                    if(stmp.mapObject.map.getLayer(prefixLayerId + "-wms-" + wmsLayer) === undefined
                    || stmp.mapObject.map.getSource(prefixLayerId + "-wms-" + wmsLayer).tiles[0] !== tileUrl
                        ){
                        if(stmp.mapObject.map.getSource(prefixLayerId + "-wms-" + wmsLayer)) {
                            stmp.mapObject.map.removeLayer(prefixLayerId + "-wms-" + wmsLayer)
                            stmp.mapObject.map.removeSource(prefixLayerId + "-wms-" + wmsLayer)
                        }
                        stmp.mapObject.map.addLayer({
                            'id': prefixLayerId + "-wms-" + wmsLayer,
                            'type': 'raster',
                            'source': {
                                'type': 'raster',
                                'tiles': [tileUrl],
                                'tileSize': 256
                            }
                        });
                    }
                    stmp.mapObject.map.setLayoutProperty(prefixLayerId + "-wms-" + wmsLayer,"visibility",'visible')
                })
                return
            }
            // use Generate
            var features = []
            var labelFeatures = []
            if(xmin >= 0){
                xmin = xmin - (xmin % item.coordStepX)
            }else{
                xmin = xmin - (item.coordStepX + (xmin % item.coordStepX))
            }
            if(ymin >= 0){
                ymin = ymin - (ymin % item.coordStepY)
            }else{
                ymin = ymin - (item.coordStepY + (ymin % item.coordStepY))
            }
            var startpoint = [xmin, ymin] //좌하단
            labelSize = item.labelSize ? item.labelSize : labelSize
            var labelType = item.labelType
            var horizonPoints = [startpoint.slice(0)]
            var verticalPoints = [startpoint.slice(0)]
            while(true){
                var lastpoint = horizonPoints[horizonPoints.length - 1]
                var x = lastpoint[0] + item.coordStepX
                var y = lastpoint[1]
                var newpoint = [x,y]
                horizonPoints.push(newpoint)
                if(x >= xmax){
                    break
                }
            }
            while(true){
                var lastpoint = verticalPoints[verticalPoints.length - 1]
                var x = lastpoint[0]
                var y = lastpoint[1] + item.coordStepY
                var newpoint = [x,y]
                verticalPoints.push(newpoint)
                if(y >= ymax){
                    break
                }
            }
            // create box features
            var newMaxY = verticalPoints[verticalPoints.length - 1]
            var newMaxX = horizonPoints[horizonPoints.length - 1]
            var box = [] //bound box
            box.push([xmin, ymin])
            box.push([xmin, newMaxY[1]])
            box.push([newMaxX[0], newMaxY[1]])
            box.push([newMaxX[0], ymin])
            box.push([xmin, ymin])
            var boxFeatures = [{
                type: 'Feature',
                properties: {zoomRange: item.zoomRange},
                geometry: {
                    type: 'Polygon',
                    coordinates: [box]
                }}]
            // create line features
            var horizonFeatures = []
            var verticalFeatures = []
            jQuery.each(horizonPoints,function(idx, first){
                var newVerticalPoints =  JSON.parse(JSON.stringify(verticalPoints))
                jQuery.each(newVerticalPoints,function(_,pos){
                    pos[0] = first[0]
                })
                verticalFeatures.push({
                    type: 'Feature',
                    geometry: {
                        type: 'LineString',
                        coordinates: newVerticalPoints
                    }
                })
            })
            jQuery.each(verticalPoints,function(idx, first){
                var newHorizonPoints =  JSON.parse(JSON.stringify(horizonPoints))
                jQuery.each(newHorizonPoints,function(_,pos){
                    pos[1] = first[1]
                })
                horizonFeatures.push({
                    type: 'Feature',
                    geometry: {
                        type: 'LineString',
                        coordinates: newHorizonPoints
                    }
                })
            })
            features = features.concat(boxFeatures)
            features = features.concat(horizonFeatures)
            features = features.concat(verticalFeatures)
            // create text features
            jQuery.each(horizonPoints,function(hidx, hpos){
                jQuery.each(verticalPoints,function(vidx, vpos){
                    if(labelType == "outside"){
                        if(hidx === 0){
                            //left
                            var latlon = vpos
                            var label = stmp.getGridLabel(latlon[1], 'lat', type)
                            labelFeatures.push({type: 'Feature', properties: {description: label, labelType: labelType, origin: latlon, position: 'left'}, 'geometry': {'type': 'Point', 'coordinates': latlon}})
                        }
                        if(hidx === horizonPoints.length - 1){
                            //right
                            var latlon = [hpos[0],vpos[1]]
                            var label = stmp.getGridLabel(latlon[1], 'lat', type)
                            labelFeatures.push({type: 'Feature', properties: {description: label, labelType: labelType, origin: latlon, position: 'right'}, 'geometry': {'type': 'Point', 'coordinates': latlon}})
                        }
                        if(vidx === 0){
                            //bottom
                            var latlon = hpos
                            var label = stmp.getGridLabel(latlon[0], 'lon', type)
                            labelFeatures.push({type: 'Feature', properties: {description: label, labelType: labelType, origin: latlon, position: 'bottom'}, 'geometry': {'type': 'Point', 'coordinates': latlon}})
                        }
                        if(vidx === verticalPoints.length - 1){
                            //top
                            var latlon = [hpos[0],vpos[1]]
                            var label = stmp.getGridLabel(latlon[0], 'lon', type)
                            labelFeatures.push({type: 'Feature', properties: {description: label, labelType: labelType, origin: latlon, position: 'top'}, 'geometry': {'type': 'Point', 'coordinates': latlon}})
                        }
                    }
                    //
                    if(labelType == "inside"){
                        if(hidx !== horizonPoints.length - 1 && vidx !== verticalPoints.length - 1){
                            var latlon = [hpos[0] + (item.coordStepX / 2), vpos[1] + (item.coordStepY / 2)]
                            var label = null
                            var labelX = stmp.getGridLabel(hpos[0], 'lon', type, item.coordStepX, item.labelStepX)
                            var labelY = stmp.getGridLabel(vpos[1], 'lat', type, item.coordStepY, item.labelStepY)
                            if($.isEmptyObject(labelX) && $.isEmptyObject(labelY)){
                                return
                            }
                            if(type == "GEOREF"){
                                if(labelX.length === 1){
                                    label = labelX.charAt(0) + labelY.charAt(0)
                                }else if(labelX.length === 2){
                                    label = labelX.charAt(0) + labelY.charAt(0) + labelX.charAt(1) + labelY.charAt(1)
                                }else{ // length : 4
                                    label = labelX.charAt(0) + labelY.charAt(0) + labelX.charAt(1) + labelY.charAt(1) + labelX.charAt(2) + labelX.charAt(3) + labelY.charAt(2) + labelY.charAt(3)
                                }
                            }else if(type == "GARS"){
                                // add suffix number
                                label = labelX + "" + labelY
                                if(item.coordStepX === 0.25 && item.coordStepY === 0.25){
                                    var hnum = hpos[0]
                                    var vnum = vpos[1]
                                    var hdx = (hnum % 0.5 / 0.25) + 1
                                    var vdx = (1 - (vnum % 0.5 / 0.25)) * 2
                                    label += (hdx + vdx)
                                }else if(item.coordStepX === 5/60 && item.coordStepY === 5/60){
                                    var h = Math.round(hpos[0] * 100) / 100
                                    var hnum = Math.round((h - (h % 0.25)) * 100) / 100
                                    var vnum = Math.round((vpos[1] - (vpos[1] % 0.25)) * 100) / 100
                                    var hdx = Math.round(hnum % 0.5 / 0.25) + 1
                                    var vdx = (1 - Math.round(vnum % 0.5 / 0.25)) * 2
                                    label += (hdx + vdx)
                                    var _hnum = h
                                    var _vnum = vpos[1]
                                    var _hdx = Math.round(_hnum % 0.25 / (5/60)) + 1
                                    var _vdx = (2 - Math.round(_vnum % 0.25 / (5/60))) * 3
                                    label += (_hdx + _vdx)
                                }
                            }else {
                                label = labelX + "" + labelY
                            }
                            labelFeatures.push({type: 'Feature', properties: {description: label, labelType: labelType, labelStepX: item.labelStepX, labelStepY: item.labelStepY, origin: latlon, position: 'inside'}, 'geometry': {'type': 'Point', 'coordinates': latlon}})
                        }
                    }
                })
            })
            // draw grid line
            stmp.mapObject.map.addSource(prefixLayerId + '-source-feature', {
                type: 'geojson',
                data: {
                    type: 'FeatureCollection',
                    features: features
                }
            })
            stmp.mapObject.map.addLayer({
                'id': prefixLayerId + '-line',
                'type': 'line',
                'source': prefixLayerId + '-source-feature',
                'paint': {
                    'line-color': lineColor,
                    'line-width': lineWidth,
                    "line-dasharray": [10, 8]
                },
                'filter': ['==', '$type', 'LineString']
            })
            // draw grid label
            var e = stmp.mapObject.map.getBounds().getEast()
            var w = stmp.mapObject.map.getBounds().getWest()
            var n = stmp.mapObject.map.getBounds().getNorth()
            var s = stmp.mapObject.map.getBounds().getSouth()
            var newLabelfeatures = []
            jQuery.each(labelFeatures, function (idx, feature) {
                var coordinates = feature.properties.origin
                var labelType = feature.properties.labelType
                if(labelType == "inside"){
                    var lon = coordinates[0]
                    var lat = coordinates[1]
                    var newcoordinates = [lon, lat]
                    if (w > lon) {
                        newcoordinates[0] = w
                        feature.properties.position = "left"
                    }else if (lon > e) {
                        newcoordinates[0] = e
                        feature.properties.position = "right"
                    }else if (s > lat) {
                        newcoordinates[1] = s
                        feature.properties.position = "bottom"
                    }else if (lat > n) {
                        newcoordinates[1] = n
                        feature.properties.position = "top"
                    }else{
                        feature.properties.position = "inside"
                    }
                    feature.geometry.coordinates = newcoordinates
                    newLabelfeatures.push(feature)
                }else{
                    var lat = coordinates[0]
                    var lon = coordinates[1]
                    var newcoordinates = [lat, lon]
                    if (w > lat) {
                        newcoordinates[0] = w
                    }
                    if (lat > e) {
                        newcoordinates[0] = e
                    }
                    if (s > lon) {
                        newcoordinates[1] = s
                    }
                    if (lon > n) {
                        newcoordinates[1] = n
                    }
                    feature.geometry.coordinates = newcoordinates
                    newLabelfeatures.push(feature)
                }
            })
            stmp.mapObject.map.addSource(prefixLayerId + '-source-label', {
                type: 'geojson',
                data: {
                    type: 'FeatureCollection',
                    features: newLabelfeatures
                }
            })
            stmp.mapObject.map.addLayer({
                id: prefixLayerId + '-label-top',
                type: 'symbol',
                source: prefixLayerId + '-source-label',
                layout: {
                    'text-field': ['get', 'description'],
                    'text-variable-anchor': ['top'],
                    'text-radial-offset': 0.2,
                    "text-size": labelSize,
                    //"text-font": ["Gosanja"]
                },
                paint: {
                    "text-color": labelColor,
                    "text-halo-color": "#fff",
                    "text-halo-width": 1
                },
                filter: ["all", ['==', '$type', 'Point'], ["==", "position", 'top']]
            });
            stmp.mapObject.map.addLayer({
                id: prefixLayerId + '-label-bottom',
                type: 'symbol',
                source: prefixLayerId + '-source-label',
                layout: {
                    'text-field': ['get', 'description'],
                    'text-variable-anchor': ['bottom'],
                    'text-radial-offset': 0.2,
                    "text-size": labelSize,
                    //"text-font": ["Gosanja"]
                },
                paint: {
                    "text-color": labelColor,
                    "text-halo-color": "#fff",
                    "text-halo-width": 1
                },
                filter: ["all", ['==', '$type', 'Point'], ["==", "position", 'bottom']]
            });
            stmp.mapObject.map.addLayer({
                id: prefixLayerId + '-label-left',
                type: 'symbol',
                source: prefixLayerId + '-source-label',
                layout: {
                    'text-field': ['get', 'description'],
                    'text-variable-anchor': ['left'],
                    'text-radial-offset': 0.2,
                    "text-size": labelSize,
                    //"text-font": ["Gosanja"]
                },
                paint: {
                    "text-color": labelColor,
                    "text-halo-color": "#fff",
                    "text-halo-width": 1
                },
                filter: ["all", ['==', '$type', 'Point'], ["==", "position", 'left']]
            });
            stmp.mapObject.map.addLayer({
                id: prefixLayerId + '-label-right',
                type: 'symbol',
                source: prefixLayerId + '-source-label',
                layout: {
                    'text-field': ['get', 'description'],
                    'text-variable-anchor': ['right'],
                    'text-radial-offset': 0.2,
                    "text-size": labelSize,
                    //"text-font": ["Gosanja"]
                },
                paint: {
                    "text-color": labelColor,
                    "text-halo-color": "#fff",
                    "text-halo-width": 1
                },
                filter: ["all", ['==', '$type', 'Point'], ["==", "position", 'right']]
            })
            stmp.mapObject.map.addLayer({
                id: prefixLayerId + '-source-label',
                type: 'symbol',
                source: prefixLayerId + '-source-label',
                layout: {
                    'text-field': ['get', 'description'],
                    "text-size": labelSize,
                    //"text-font": ["Gosanja"]
                },
                paint: {
                    "text-color": labelColor,
                    "text-halo-color": "#fff",
                    "text-halo-width": 1
                },
                filter: ["all", ['==', '$type', 'Point'], ["==", "position", 'inside']]
            })
        })
    }
    /** graticules option
      {
          type : <WGS84,MGRS,UTM,GARS,GEOREF>,
          // 자동생성
          minZoom : <최소축척 / 초과>,
          maxZoom : <최소축척 / 이하>,
          coordStepX : <자동생성 그리드 간격>,
          coordStepX : <자동생성 그리드 간격>,
          labelType : <자동생성 라벨 위치>,
          labelStepX : <자동생성 라벨 단계>,
          labelStepY : <자동생성 라벨 단계>,
          // 맵서버로부터
          mapServer : <맵서버>,
          layers : <맵서버-레이어>,
          labelField : <맵서버-라벨필드명>,
          // 스타일옵션 / stmp.setGraticulesStyle(...) 사용가능
          lineWidth : <그리드 굵기>,
          lineColor : <그리드 색상>,
          labelSize : <라벨 크기>,
          labelColor : <라벨 색상>,
      }
     * @param types - "WGS84" or ["WGS84"] or ["WGS84","MGRS"]
     */
    , drawGraticules : function(types){
        if(types === undefined || types === null){
            console.warn("그리드 타입이 지정되지 않았습니다.")
            return
        }
        if(!(types instanceof Array)){
            types = [types]
        }
        var options = {
            types : types,
            graticules : [],
            labelGenerator : stmp.graticulesLabelGenerator
        }
        if(types.indexOf("WGS84") > -1){
            options.graticules.push({type: "WGS84", minZoom : 0,  maxZoom : 5,  coordStepX: 10,     coordStepY: 10,     labelType:"outside"}) //10도
            options.graticules.push({type: "WGS84", minZoom : 5,  maxZoom : 8,  coordStepX: 1,      coordStepY: 1,      labelType:"outside"}) //1도
            options.graticules.push({type: "WGS84", minZoom : 8,  maxZoom : 10, coordStepX: 0.5,    coordStepY: 0.5,    labelType:"outside"}) //0.5도
            options.graticules.push({type: "WGS84", minZoom : 10, maxZoom : 17, coordStepX: 1/60,   coordStepY: 1/60,   labelType:"outside"}) //1분
            options.graticules.push({type: "WGS84", minZoom : 17, maxZoom : 20, coordStepX: 1/60/60,coordStepY: 1/60/60,labelType:"outside"}) //1초
        }
        if(types.indexOf("MGRS") > -1){
            options.graticules.push({type: "MGRS", minZoom : 0,  maxZoom : 5,  coordStepX: 6, coordStepY: 8, labelType: "inside", labelStepX : 1, labelStepY : 'B'})  //6*8도
            options.graticules.push({type: "MGRS", minZoom : 5,  maxZoom : 10, mapServer: 'http://116.121.199.25:8080/geoserver/jimap/wms', layers: 'jimap:mgrs_100km', labelField: 'GRID100K'})  // from Mapserver(Geoserver)
            options.graticules.push({type: "MGRS", minZoom : 10, maxZoom : 20, mapServer: 'http://116.121.199.25:8080/geoserver/jimap/wms', layers: 'jimap:mgrs_10km', labelField: 'UTM_LABEL'})  // from Mapserver(Geoserver)
        }
        if(types.indexOf("UTM") > -1){
            options.graticules.push({type: "UTM", minZoom : 0,  maxZoom : 3,  coordStepX: 6, coordStepY: 90, labelType: "inside", labelStepX : 1})  //6*180도
            options.graticules.push({type: "UTM", minZoom : 3,  maxZoom : 12, coordStepX: 6, coordStepY: 8,  labelType: "inside", labelStepX : 1, labelStepY : 'B'})  //6*8도
            options.graticules.push({type: "UTM", minZoom : 12, maxZoom : 20, mapServer: 'http://116.121.199.25:8080/geoserver/jimap/wms', layers: ['jimap:UTM_1km_51n_S','jimap:UTM_1km_51n_T','jimap:UTM_1km_52n_S','jimap:UTM_1km_52n_T'], labelField: 'UTM'})  // from Mapserver(Geoserver)
        }
        if(types.indexOf("GARS") > -1){
            options.graticules.push({type: "GARS", minZoom : 0,  maxZoom : 6,  coordStepX: 20,   coordStepY: 20,   labelType: "inside", labelStepX : 20, labelStepY : 20})  //20도
            options.graticules.push({type: "GARS", minZoom : 6,  maxZoom : 9,  coordStepX: 0.5,  coordStepY: 0.5,  labelType: "inside", labelStepX : 1,  labelStepY : 'AA'})  //30분
            options.graticules.push({type: "GARS", minZoom : 9,  maxZoom : 11, coordStepX: 0.25, coordStepY: 0.25, labelType: "inside", labelStepX : 1,  labelStepY : 'AA'})  //15분 / Custom Label
            options.graticules.push({type: "GARS", minZoom : 11, maxZoom : 20, coordStepX: 5/60, coordStepY: 5/60, labelType: "inside", labelStepX : 1,  labelStepY : 'AA'})  //5분 / Custom Label
        }
        if(types.indexOf("GEOREF") > -1){
            options.graticules.push({type: "GEOREF", minZoom : 0,  maxZoom : 5,  coordStepX: 15,   coordStepY: 15,   labelType: "inside", labelStepX : 'A', labelStepY : 'A'})  //15도
            options.graticules.push({type: "GEOREF", minZoom : 5,  maxZoom : 11, coordStepX: 1,    coordStepY: 1,    labelType: "inside", labelStepX : 'A', labelStepY : 'A'})  //1도 / Custom Label
            options.graticules.push({type: "GEOREF", minZoom : 11, maxZoom : 20, coordStepX: 1/60, coordStepY: 1/60, labelType: "inside", labelStepX : 'A', labelStepY : 'A'})  //1분 / Custom Label
        }
        options.graticulesDrawn = false
        stmp.mapObject.map._graticules_options = options
        stmp.mapObject.map.resize() // call grid generator
    }
    /**
     * stmp.setGraticulesStyle("MGRS","labelColor","#ff0000")
     * stmp.setGraticulesStyle("MGRS","labelSize",20)
     * stmp.setGraticulesStyle("GARS","lineColor","#ffff00")
     * @param types
     * @param styleName - lineWidth, lineColor, labelSize, labelColor
     * @param styleValue
     */
    , setGraticulesStyle : function(types,styleName,styleValue){
        var options = stmp.mapObject.map._graticules_options
        if(options === undefined){
            console.error("생성된 그리드가 없습니다.")
            return
        }
        if(!(types instanceof Array)){
            types = [types]
        }
        jQuery.each(options.graticules,function(idx, item){
            if(types.indexOf(item.type) > -1){
                item[styleName] = styleValue
            }
        })
        stmp.mapObject.map.resize() // call grid generator
    }
    , getDMS : function(dd, longOrLat){
        let hemisphere = /^[WE]|(?:lon)/i.test(longOrLat)? dd < 0 ? "W" : "E" : dd < 0 ? "S" : "N";
        const absDD = Math.abs(dd);
        const degrees = absDD > 0 ? Math.floor(absDD) : Math.ceil(absDD)
        const minutes = ((absDD - degrees) * 60) > 0 ? Math.floor((absDD - degrees) * 60) : Math.ceil((absDD - degrees) * 60)
        const seconds = ((absDD - degrees - minutes / 60) * Math.pow(60, 2)).toFixed(0);
        let dmsArray = [degrees, minutes, seconds, hemisphere];
        return `${dmsArray[0]}°${dmsArray[1]}'${dmsArray[2]}" ${dmsArray[3]}`;
    }
    , getGridLabel : function(value, longOrLat, type, coordStep, firstLabel){
        var alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ" // I,O 없음
        if(type === "WGS84"){
            // WGS84 / 경계라벨
            return stmp.getDMS(value, longOrLat)
        }else if(type === "GEOREF"){
            // GEOREF / 박스형
            var start = (longOrLat === "lon" ? -180 : -90)
            var label = null
            var stepIndex = Math.floor((value - start) / 15)
            var charIndex = (alphabet.length + alphabet.indexOf(firstLabel)) % alphabet.length
            var charRemain = (charIndex + stepIndex) % alphabet.length
            label = alphabet.substring(charRemain,charRemain + 1)
            if(coordStep < 15){
                var secondCharRemain = (15 + Math.floor(value % 15)) % 15
                label += alphabet.substring(secondCharRemain,secondCharRemain + 1)
            }
            if(coordStep < 1){
                var thirdCharRemain = Math.round((value - Math.floor(value)) / coordStep)
                label += (thirdCharRemain + "").padStart(2,"0")
            }
            console.log(value, label)
            return label
        }else{
            // MGRS, UTM, GARS / 순차형
            var start = (longOrLat === "lon" ? -180 : -90)
            if(type === "GARS"){
                if(coordStep === 20){
                    start = 0
                }
                if(coordStep < 0.5){
                    coordStep = 0.5
                }
            }
            if(jQuery.isEmptyObject(firstLabel)){
                return ''
            }
            firstLabel = firstLabel + ""
            var label = null
            var stepIndex = Math.floor((value - start) / coordStep)
            var isAlphabet = (alphabet.indexOf(firstLabel.charAt(0)) > -1 ? true : false)
            if(isAlphabet){
                if(firstLabel.length === 1){
                    var charIndex = (alphabet.length + alphabet.indexOf(firstLabel)) % alphabet.length
                    var charRemain = (charIndex + stepIndex) % alphabet.length
                    label = alphabet.substring(charRemain,charRemain + 1)
                }else if(firstLabel.length === 2){
                    var firstChar = firstLabel.charAt(0)
                    var secondChar = firstLabel.charAt(1)
                    var firstCharIndex = (alphabet.length + alphabet.indexOf(firstChar)) % alphabet.length
                    var secondCharIndex = (alphabet.length + alphabet.indexOf(secondChar)) % alphabet.length
                    var firstCharRemain = (firstCharIndex + Math.floor((secondCharIndex + stepIndex) / alphabet.length)) % alphabet.length
                    var secondCharRemain = (secondCharIndex + stepIndex) % alphabet.length
                    label = alphabet.substring(firstCharRemain,firstCharRemain + 1) + alphabet.substring(secondCharRemain,secondCharRemain + 1)
                }else{
                    console.warn(value,"getGridLabel()::","3자리 지원안함...")
                    return
                }
            }else{
                // number
                var calcLabel = (parseInt(firstLabel) + stepIndex)
                var maxIndex = 360 / coordStep
                if(type === "MGRS"){
                    if(calcLabel <= 0){
                        calcLabel += maxIndex
                    }
                }
                if(type === "GARS" && coordStep === 20){
                    let hemisphere = /^[WE]|(?:lon)/i.test(longOrLat)? label < 0 ? "W" : "E" : label < 0 ? "S" : "N";
                    label = Math.abs(calcLabel) + hemisphere
                }else{
                    label = (calcLabel + "").padStart((maxIndex + "").length,"0")
                }
            }
            return label
        }
    }
    /* GRID area */
    /* 군대부호 area start */
    /**
     * 군대부호 변경
     * @param evt
     */
    , milsymbolsPreview : function(evt){
        console.log(evt)
        var source = stmp.mapObject.map.getSource('milsymbols-source-feature')
        if(source === undefined){
            return
        }
        var features = evt.features
        var sourceData = source._data
        jQuery.each(features, function(idx, feature){
            var drawId = feature.id
            var drawGeometry = feature.geometry
            sourceData = stmp.milsymbolsChangeSourceData(sourceData,drawId,drawGeometry,"move")
        })
        if(sourceData !== null){
            source.setData(sourceData)
        }
    }
    /**
     * 군대부호 소스 데이터 변경
     * @param sourceData
     * @param drawId
     * @param drawGeometry
     * @param type
     * @returns {null|*}
     */
    , milsymbolsChangeSourceData : function(sourceData, drawId, drawGeometry, type){
        var features = sourceData.features
        var removeFeatures = []
        jQuery.each(features, function(idx, feature){
            if(feature.properties.drawId !== drawId){
                return
            }
            if(feature.geometry.type === "Point" && feature.properties.type !== "Label") {
                if(type === "move"){
                    feature.geometry = drawGeometry
                }else if(type === "draw"){
                    var options = stmp.getMilsymbolOptions()
                    var symbol = new ms.Symbol(options) // 심볼생성
                    var imageData = symbol.asCanvas().toDataURL()
                    stmp.mapObject.map.loadImage(imageData,function(e,image){
                        if(stmp.mapObject.map.hasImage(drawId + "-image")){
                            stmp.mapObject.map.removeImage(drawId + "-image")
                        }
                        stmp.mapObject.map.addImage(drawId + "-image",image)
                    })
                    feature.properties.imageId = drawId + "-image"
                }
            }else{
                feature.position = idx
                removeFeatures.splice( 0, 0, feature)
            }
        })
        if(removeFeatures.length > 0){
            jQuery.each(removeFeatures, function(idx, removeFeature){
                sourceData.features.splice(removeFeature.position, 1)
            })
            //
            var coord = drawGeometry.coordinates
            stmp.mapObject.map._drawing_milsymbol_coordinates = []
            var coordinates = stmp.mapObject.map._drawing_milsymbol_coordinates
            jQuery.each(coord, function(idx, point){
                coordinates.push({x:point[0],y:point[1]})
            })
            drawMsymbol(-1, 'geoJSON')
            var data = JSON.parse(stmp.mapObject.map._drawing_milsymbol._geojson)
            jQuery.each(data.features, function(idx, feature){
                feature.properties.drawId = drawId
                if(feature.geometry.type === "Point"){
                    feature.properties.type = "Label"
                    feature.properties.labelOffset = [feature.properties.labelXOffset,feature.properties.labelYOffset]
                }
            })
            sourceData.features = sourceData.features.concat(data.features)
        }
        return sourceData
    }
    , milsymbolsGenerator : function(evt){
        var features = evt.features
        var drawId = features[0].id
        var geometryType = features[0].geometry.type
        var coord = features[0].geometry.coordinates
        //
        var options = stmp.mapObject.map._drawing_milsymbol.options
        var constraint = options._constraint
        var coordinates = stmp.mapObject.map._drawing_milsymbol_coordinates
        var datas = []
        if(geometryType === "Point"){
            if(constraint === "milSym"){
                var imageData = stmp.mapObject.map._drawing_milsymbol.asCanvas().toDataURL()
                stmp.mapObject.map.loadImage(imageData,function(e,image){
                    stmp.mapObject.map.addImage(drawId + "-image", image)
                })
            }else{
                drawMsymbol(options._symbol_serial, 'SVG', null, geometryType);
                if(jQuery("#svg-draw").length == 0){
                    jQuery("body").append("<div id='svg-draw'></div>")
                }
                jQuery("#svg-draw").empty()
                jQuery("#svg-draw").append(stmp.mapObject.map._drawing_milsymbol._svg_symbol.getSVG())
                html2canvas(jQuery("#svg-draw svg")[0],{backgroundColor: "rgba(0,0,0,0)"}).then(function(canvas){
                    stmp.mapObject.map.loadImage(canvas.toDataURL(),function(e,image){
                        stmp.mapObject.map.addImage(drawId + "-image", image)
                    })
                })
            }
            datas.push({
                type: 'Feature',
                properties: {
                    type: 'Image',
                    imageId: drawId + "-image",
                    drawId : drawId
                },
                geometry: {
                    type: 'Point',
                    coordinates: coord
                }
            })
        }else if(geometryType === "LineString"){
            jQuery.each(coord, function(idx, point){
                coordinates.push({x:point[0],y:point[1]})
            })
            drawMsymbol(options._symbol_serial, 'geoJSON', null, geometryType)
            var data = JSON.parse(stmp.mapObject.map._drawing_milsymbol._geojson)
            jQuery.each(data.features, function(idx, feature){
                feature.properties.drawId = drawId
                if(feature.geometry.type === "Point"){
                    feature.properties.type = "Label"
                    feature.properties.labelOffset = [feature.properties.labelXOffset,feature.properties.labelYOffset]
                }
            })
            datas = datas.concat(data.features);
        }
        // draw milsymbol
        var source = stmp.mapObject.map.getSource('milsymbols-source-feature')
        if(source === undefined){
            stmp.mapObject.map.addSource('milsymbols-source-feature', {
                type: jiConstant.MAPBOX_SOURCE_TYPE.GEOJSON,
                data: {
                    type: 'FeatureCollection',
                    features: datas
                }
            })
            source = stmp.mapObject.map.getSource('milsymbols-source-feature')
        }else{
            var sourceData = source._data
            sourceData.features = sourceData.features.concat(datas)
            source.setData(sourceData)
        }
        // 단일심볼 레이어
        var imageLayer = stmp.mapObject.map.getLayer('milsymbols-layer-image')
        if(imageLayer === undefined){
            stmp.mapObject.map.addLayer({
                id: 'milsymbols-layer-image',
                type: 'symbol',
                source: source.id,
                layout: {
                    'icon-image': ['get', 'imageId'],
                    'icon-allow-overlap': true
                },
                filter: ["all", ['==', '$type', 'Point'], ["==", "type", 'Image']]
            },"gl-draw-polygon-fill-inactive.cold")
        }
        // 라인 레이어
        var lineLayer = stmp.mapObject.map.getLayer('milsymbols-layer-line')
        if(lineLayer === undefined){
            stmp.mapObject.map.addLayer({
                id: 'milsymbols-layer-line',
                type: 'line',
                source: source.id,
                paint: {
                    "line-color": ['get', 'strokeColor'],
                    "line-width": ['get', 'strokeWidth']
                },
                filter: ['==', '$type', 'LineString']
            },"gl-draw-polygon-fill-inactive.cold")
        }
        // 면 레이어
        var fillLayer = stmp.mapObject.map.getLayer('milsymbols-layer-fill')
        if(fillLayer === undefined){
            stmp.mapObject.map.addLayer({
                id: 'milsymbols-layer-fill',
                type: 'fill',
                source: source.id,
                paint: {
                    "fill-color": ['get', 'fillColor']
                },
                filter: ['==', '$type', 'Polygon']
            },"gl-draw-polygon-fill-inactive.cold")
        }

        // 라벨 레이어
        var labelLayer = stmp.mapObject.map.getLayer('milsymbols-layer-label')
        if(labelLayer === undefined){
            stmp.mapObject.map.addLayer({
                id: 'milsymbols-layer-label',
                type: 'symbol',
                source: source.id,
                layout: {
                    'text-field': ['get', 'label'],
                    'text-allow-overlap' : true,
                    "text-size": ['get', 'fontSize'],
                    'text-rotate': ['get','angle'],
                    "text-font": ["Gosanja"]
                },
                paint: {
                    "text-color": ['get', 'fontColor'],
                    "text-halo-color": ['get', 'labelOutlineColor'],
                    "text-halo-width": ['get', 'labelOutlineWidth'],
                },
                filter: ["all", ['==', '$type', 'Point'], ["==", "type", 'Label']]
            },"gl-draw-polygon-fill-inactive.cold")
        }
    }
    /**
     * @param options
     */
    , drawMilsymbol : function(options){
        if(options === null || options === undefined){
            toastr.error("MilSymbol options 값이 없습니다.")
            return
        }
        var symbol = new ms.Symbol(options) // 심볼생성
        var sidc = options.SIDC
        if (sidc.charAt(0) === 'W' || sidc.charAt(0) === 'G') {
            var drawInfo = getDrawGraphicsInfo(sidc);
            if (drawInfo === undefined || drawInfo.draw_type === '') {
                toastr.warning("군대부호["+sidc+"] 의 정보를 가져올 수 없습니다.")
                return;
            }
            symbol.options._min_point = drawInfo.min_point
            symbol.options._max_point = drawInfo.max_point
            symbol.options._draw_type = drawInfo.draw_type
            symbol.options._constraint = drawInfo.constraint
            var mode = "draw_line_string"
            if(drawInfo.draw_type === 'Point'){
                mode = "draw_point"
            }
            if(stmp.PRESENT_MAP_KIND == stmp.MAP_KIND.MAP_2D){
                // 맵박스
                stmp.drawControl.changeMode(mode)
            }else if(stmp.PRESENT_MAP_KIND == stmp.MAP_KIND.MAP_3D){
                // 세슘

            }
        } else {
            symbol.options._min_point = 1
            symbol.options._max_point = 1
            symbol.options._draw_type = "Point"
            symbol.options._constraint = "milSym"
            if(stmp.PRESENT_MAP_KIND == stmp.MAP_KIND.MAP_2D){
                // 맵박스
                stmp.drawControl.changeMode("draw_point")
            }else if(stmp.PRESENT_MAP_KIND == stmp.MAP_KIND.MAP_3D){
                // 세슘

            }
        }
        symbol.options._symbol_serial = (stmp.mapObject.map._drawing_milsymbol ? stmp.mapObject.map._drawing_milsymbol.options._symbol_serial + 1 : 0)
        // 심볼생성
        stmp.mapObject.map._drawing_milsymbol = symbol
        stmp.mapObject.map._drawing_milsymbol_coordinates = []
    }
    /**
     *
     * @returns {{strokeWidth, sidcsymbolModifier12, civilianColor, evaluationRating, fillOpacity, hqStafLength, combatEffectiveness, uniqueDesignation, signatureEquipment, hostile, icon, staffComments, infoColor, type, speed, R, infoSize, dtg, monoColor1: *, iffSif, fillPercent, direction, additionalInformation, quantity, AG, colorMode: *, fill, outlineWidth, size, reinforcedReduced, outlineColor, altitudeDepth, specialHeadquarters, infoFields, location, higherFormation, frame}}
     */
    , getMilsymbolOptions : function(){
        if ( bChangeID ) { // 20200305 부호 변경 시 기존 수식정보 모두 초기화. 초기화가 되지 않을 경우 변경하지 않아야 할 정보도 자동으로 변경되어 버림.
            // A:기본부호지정, B:부대단위, C:장비수량, D:기동부대식별,
            // F:부대증감, G:군 및 국가 구분, H:추가사항, H1:추가사항, H2:추가사항,
            // J:평가등급, K:전투효과, L:신호정보장비, M:상급부대, N:적군표시,
            // P:피아식별모드/코드, Q:이동방향, R:이동수단, R2:신호정보 장비 이동성,
            // S:지휘소표시/실제위치표시, T:고유명칭, T1:고유명칭1, V:장비명, W:활동시각, W1:활동시각1,
            // X:고도/심도, X1:고도/심도1, XN:고도/심도[], Y:위치, Z:속도, AA:지휘통제소, AB:가장/가상식별부호,
            // AD:기반형태, AE:장비분해시간, AF:공통명칭, AG:보조장비 식별부호,
            // AH:불확정영역, AI:선위의 추측선, AJ:속도선, AM:거리(미터), AN:각도(도)
            var propNm = ['B', 'C', 'D', 'F', 'G', 'H', 'H1', 'H2', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'R2', 'S', 'T', 'T1', 'V', 'W', 'W1', 'X', 'X1', 'XN', 'Y', 'Z', 'AA', 'AB', 'AD', 'AE', 'AF', 'AG', 'AH', 'AI', 'AJ', 'AM', 'AN'];
            for (var j = 0; j < propNm.length; j++) {
                if (propNm[j] === 'B') {
                    $('#SIDCSYMBOLMODIFIER12').find("option:eq(0)").prop("selected", true);
                }else if (propNm[j] === 'J' || propNm[j] === 'R' || propNm[j] === 'R2' || propNm[j] === 'AD' || propNm[j] === 'AG') {
                    if ($('#'+propNm[j]) !== undefined) {
                        $('#'+propNm[j]).find("option:eq(0)").prop("selected", true);
                    }
                }else{
                    if ( propNm[j] !== '' && $('#'+propNm[j]) !== undefined) {
                        $('#'+propNm[j]).val("");
                    }
                }
            }
        }
        var options = {
            colorMode: document.getElementById("ColorMode").value,
            monoColor1: jQuery("input:checkbox[id='MonoColorChk']").is(":checked") ? jQuery('#MonoColor1').val() : '',
            hqStafLength: document.getElementById("hqStafLength").value,
            infoColor: document.getElementById("InfoColor").value,
            infoSize: document.getElementById("infoSize").value,
            frame:  document.getElementById("Frame").checked,
            fill: document.getElementById("Fill").checked,
            fillPercent: document.getElementById("FillPercent").value,
            fillOpacity: document.getElementById("FillOpacity").value,
            icon: document.getElementById("DisplayIcon").checked,
            civilianColor: document.getElementById("CivilianColors").checked,
            infoFields: document.getElementById("infoFields").checked,
            outlineColor: document.getElementById("outlineColor").value,
            outlineWidth: document.getElementById("outlineWidth").value,
            size: document.getElementById("Size").value,
            strokeWidth: document.getElementById("StrokeWidth").value,
            sidcsymbolModifier12: document.getElementById("SIDCSYMBOLMODIFIER12").value,
            quantity: document.getElementById("C").value,
            reinforcedReduced: document.getElementById("F").value,
            staffComments: document.getElementById("G").value,
            additionalInformation: document.getElementById("H").value,
            evaluationRating: document.getElementById("J").value,
            combatEffectiveness: document.getElementById("K").value,
            signatureEquipment: document.getElementById("L").value,
            higherFormation: document.getElementById("M").value,
            hostile: document.getElementById("N").value,
            iffSif: document.getElementById("P").value,
            direction: document.getElementById("Q").value,
            R: document.getElementById("R").value,
            uniqueDesignation: document.getElementById("T").value,
            type: document.getElementById("V").value,
            dtg: document.getElementById("W").value,
            altitudeDepth: document.getElementById("X").value,
            location: document.getElementById("Y").value,
            speed: document.getElementById("Z").value,
            specialHeadquarters: document.getElementById("AA").value,
            AG: document.getElementById("AG").value,
        }
        options.SIDC = buildSymbolID(function_sidc, options);
        return options
    }
    /* 군대부호 area end */
};
if (window.stmp === undefined) {
    window.stmp = stmp;
}

if (stmp.URL === undefined) {
    stmp.URL = stmp.PROTOCOL + '//' + stmp.SERVER_DOMAIN + (stmp.SERVER_MAP_PORT !== 0 ? ':' + stmp.SERVER_MAP_PORT : '');
}