'use strict';
/**
 * 상황도 공통
 */
var stmp = {
    // 상황도 서버 도메인 서버 IP
    SERVER_DOMAIN : '211.172.246.71',
    // 상황도 서버 지도 PORT
    SERVER_MAP_PORT : '12000',
    // 상황도 서버 이미지 PORT
    SERVER_IMG_PORT : '11200',
    STMP_UI_PATH : '/ui/CF/SI',
    BASE_MAP_SOURCE : 'world:truemarble',
    BASE_MAP_LAYER_ID : 'BASE_MAP',
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
    /**
     * Source 타입 정의 (2D)
     */
    SOURCE_TYPE : {
        GEOJSON : 'geojson',
        VECTOR : 'vector',
        RASTER : 'raster',
        IMAGE : 'image'
    },
    /**
     * Vector Symbol 타입 정의 (2D)
     */
    LAYER_TYPE : {
        RASTER : 'raster',
        CIRCLE : 'circle',
        LINE : 'line',
        SYMBOL : 'symbol',
        FILL : 'fill'
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
    GEOMETRY_TYPE : {
        POINT : 'Point',
        MULTIPOINT : 'MultiPoint',
        LINESTRING : 'LineString',
        MULTILINESTRING : 'MultiLineString',
        POLYGON : 'Polygon',
        MULTIPOLYGON : 'MultiPolygon'
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
            }else if( Array.isArray(a) && a.length < 1 ){
                return false;
            }else{
                return true;
            }

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
    /**
     * Geometry Type 설정
     */
    checkGeometryType : function checkGeometryType(type) {
        var _geometryType;
        var _layerType;

        switch (type) {
            case stmp.DRAW_TYPE_KIND.POINT.CD :
                _geometryType = stmp.GEOMETRY_TYPE.POINT;
                _layerType = stmp.LAYER_TYPE.CIRCLE;
                break;
            case stmp.DRAW_TYPE_KIND.BASE_MILSYMBOL.CD :
            case stmp.DRAW_TYPE_KIND.IMAGE.CD :
            case stmp.DRAW_TYPE_KIND.OVERLAY.CD :
                _geometryType = stmp.GEOMETRY_TYPE.POINT;
                _layerType = stmp.LAYER_TYPE.SYMBOL;
                break;
            case stmp.DRAW_TYPE_KIND.LINE.CD :
                _geometryType = stmp.GEOMETRY_TYPE.LINESTRING;
                _layerType = stmp.LAYER_TYPE.LINE;
                break;
            case stmp.DRAW_TYPE_KIND.CIRCLE.CD :
            case stmp.DRAW_TYPE_KIND.POLYGON.CD :
                _geometryType = stmp.GEOMETRY_TYPE.POLYGON;
                _layerType = stmp.LAYER_TYPE.FILL;
                break;
        }

        return {
            'geometryType' : _geometryType,
            'layerType' : _layerType
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

        var _typeValue = this.checkGeometryType(params.type.CD);

        _options.geometryType = _typeValue.geometryType;
        _options.layerType = _typeValue.layerType;

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

        var feature = new jiFeature(_options);
        if(feature.getFeatureId()){
        this.btlRnk = feature.getFeatureId();
        }
        this.setGlobalFeatures(feature);

        if (!this.checkMapKind()) {
            if (!this.mapObject.getSource(_options.layerId)) {
                this.mapObject.addSource(_options.layerId, {
                    'type' : stmp.SOURCE_TYPE.GEOJSON,
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
                        _moveYn = false;
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

            var _typeValue = stmp.checkGeometryType(_options.type.CD);
            _options.geometryType = _typeValue.geometryType;
            _options.layerType = _typeValue.layerType;

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
                    'type' : stmp.SOURCE_TYPE.GEOJSON,
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

            var _typeValue = stmp.checkGeometryType(_options.type.CD);
            _options.geometryType = _typeValue.geometryType;
            _options.layerType = _typeValue.layerType;

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
    //투명도 캔버스 ID
    milSymEditOjb : null

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

};

if (window.stmp === undefined) {
    window.stmp = stmp;
}