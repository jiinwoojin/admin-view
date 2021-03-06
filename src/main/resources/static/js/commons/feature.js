/**
 * 도시요소를 지도에 도시하기 위해 정의한 객체
 * @param params
 * TODO 군대부호 관련 수식 정보 추가 필요 (cop db 정의 시)
 */
var jiFeature = function jiFeature(params) {
	if (!(this instanceof jiFeature)) {
		throw new Error('new 로 생성해야 함.');
	}

	this.id = params.id;								// 객체 ID
	this.stmpLayerId = params.layerId;					// 레이어 ID
	this.type = params.type;							// 객체 TYPE
	this.geometryType = null;							// geometry Type
	this.mapBoxLayerType = null;						// mapbox layer Type
	this.cesiumLayerType = null;						// cesium layer Type
	this.olLayerType = null;							// ol layer Type

	this._checkGeometryType();
	this.coordInfo = params.coordInfo;					// 좌표 정보
	this.pixel = params.pixel || undefined;				// 화면 좌표 정보
	this.callBackFN = params.callBackFN || undefined;	// callback 함수 정의
	this.overlayData = params.overlayData || undefined;	// 투명도 데이터
	this.featureId = this.getStmpLayerId() + '_' + this.getId();

	this.styleInfo = params.styleInfo;					// 객체 Style 정보
	this.img = this._setImage();
	this.imgId = this._setImageId();

	this.geometry = this._createGeometry();				// 객체 Geometry

    this.properties = this._setProperties();
};

jiFeature.prototype = {
	// 객체 ID
	getId : function getId() {
		return this.id;
	},
	// 레이어 ID + 객체 ID, globalFeatures, flashFeatures, 3D entity 관리에 쓰일 KEY 값
	getFeatureId : function getFeatureId() {
		return this.featureId;
	},
	// 레이어 ID - source id
	getStmpLayerId : function getStmpLayerId() {
		return this.stmpLayerId;
	},
	getType : function getType() {
		return this.type;
	},
	getTypeCd : function getTypeCd() {
		return this.type.CD;
	},
	getTypeName : function getTypeName() {
		return this.type.LAYER_NAME;
	},
	getGeometry : function getGeometry() {
		return this.geometry;
	},
	getMapBoxLayerType : function getMapBoxLayerType() {
		return this.mapBoxLayerType;
	},
	getLayerId : function getLayerId() {
		return this.getStmpLayerId() + '_' + this.getTypeName();
	},
	getGeoJson : function getGeoJson() {
		return {
			'type' : 'Feature',
			'id' : this.getId(),
			'geometry' : {
				'type' : this.getGeometry().getType(),
				'coordinates' : this.getGeometry().getCoordinates()
			},
			'properties' : this.getProperties()
		}
	},
	getPixel : function getPixel() {
		return this.pixel;
	},
	getCoordinates : function getCoordinates() {
		// 좌표는 모두 경위도로 변환
		var _coordinates;
		switch (this.coordInfo.type) {
			case stmp.COORDINATE_SYSTEM.WGS84 :
				_coordinates = this.coordInfo.coords;
				break;
			case stmp.COORDINATE_SYSTEM.MGRS :
				_coordinates = this._mgrsToLonLat();
				break;
			case stmp.COORDINATE_SYSTEM.UTM :
				_coordinates = this._utmToLonLat();
				break;
			case stmp.COORDINATE_SYSTEM.GEOREF :
				_coordinates = this._geoRefToLonLat();
				break;
			case stmp.COORDINATE_SYSTEM.GARS :
				_coordinates = this._garsToLonLat();
				break;
		}
		return _coordinates;
	},
	getImage : function getImage() {
		return this.img;
	},
	getProperties : function getProperties() {
        return this.properties;
	},
    getImageId : function getImageId() {
        return this.imgId;
    },
	getSymbol : function getSymbol() {
		return this.styleInfo.milsymbol.symbol;
	},
	getGeometryType : function getGeometryType() {
		return this.geometryType;
	},
	isSymbol : function isSymbol() {
		return stmp.valid.checkValue(this.styleInfo.milsymbol);
	},
    _checkImgStyle : function _checkImgStyle() {

    },
    _setProperties : function _setProperties() {
	    var _properties = {};

        _properties.id = this.getId();

        if (this.getMapBoxLayerType() === jiConstant.MAPBOX_LAYER_TYPE.SYMBOL) {
            _properties.img = this.getImageId();

            var _size;
            if (this.styleInfo.style) {
                _size = stmp.valid.defaultValue(this.styleInfo.style.size, 1);
            } else {
                _size = 1;
            }

            _properties.size = _size;
        } else if (this.getMapBoxLayerType() === jiConstant.MAPBOX_LAYER_TYPE.CIRCLE) {
            _properties.radius = stmp.valid.defaultValue(this.styleInfo.style.radius,
                stmp.STYLE_2D_PAINT.CIRCLE.RADIUS.default);
            _properties.color = stmp.valid.defaultValue(this.styleInfo.style.color,
                stmp.STYLE_2D_PAINT.CIRCLE.COLOR.default);
            _properties.opacity = stmp.valid.defaultValue(this.styleInfo.style.opacity,
                stmp.STYLE_2D_PAINT.CIRCLE.OPACITY.default);
        } else if (this.getMapBoxLayerType() === jiConstant.MAPBOX_LAYER_TYPE.FILL) {
        	_properties.color = stmp.valid.defaultValue(this.styleInfo.style.color,
				stmp.STYLE_2D_PAINT.FILL.COLOR.default);
        	_properties.opacity = stmp.valid.defaultValue(this.styleInfo.style.opacity,
				stmp.STYLE_2D_PAINT.FILL.OPACITY.default);
        	if (this.styleInfo.style['outline-color']) {
        		_properties['outline-color'] = this.styleInfo.style['outline-color'];
			}
		} else if (this.getMapBoxLayerType() === jiConstant.MAPBOX_LAYER_TYPE.LINE) {
        	_properties.cap = stmp.valid.defaultValue(this.styleInfo.style.cap,
				stmp.STYLE_2D_LAYOUT.LINE.CAP.default);
        	_properties.join = stmp.valid.defaultValue(this.styleInfo.style.join,
				stmp.STYLE_2D_LAYOUT.LINE.JOIN.default);
        	_properties['line-color'] = stmp.valid.defaultValue(this.styleInfo.style.color,
				stmp.STYLE_2D_PAINT.LINE.COLOR.default);
        	_properties['line-width'] = stmp.valid.defaultValue(this.styleInfo.style.width,
				stmp.STYLE_2D_PAINT.LINE.WIDTH.default);
        	_properties['line-opacity'] = stmp.valid.defaultValue(this.styleInfo.style.opacity,
				stmp.STYLE_2D_PAINT.LINE.OPACITY.default);
		}

        _properties.type = this.getTypeName();

        _properties.layerType = '';
        _properties.isVisible = true;			// 도시 여부

        return _properties;
    },
    _setImage : function _setImage() {
        var _img;
        if (this.styleInfo.milsymbol) {			// 군대부호
            this.styleInfo.milsymbol.symbol =
                this._setSymbol(this.styleInfo.milsymbol.sdic,
                    this.styleInfo.milsymbol.options);
            _img = this.styleInfo.milsymbol.symbol.asCanvas().toDataURL('image/png');
        } else if (this.styleInfo.image) {
            if (this.styleInfo.image.path) {
                // TODO 이미지 load
				_img = this.styleInfo.image.path;
            } else {
                if (this.styleInfo.image.data.indexOf('svg') > -1) {
                    _img = 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(this.styleInfo.image.data)));
                } else {
                    _img = this.styleInfo.image.data;
                }
            }
        }

        return _img;
    },
    //
    _setImageId : function _setImageId() {
        return this.getLayerId() + '_' + this.getId() + '_'
            + ((this.getTypeCd() === stmp.DRAW_TYPE_KIND.BASE_MILSYMBOL.CD
				|| this.getTypeCd() === stmp.DRAW_TYPE_KIND.MILITARY_MILSYMBOL.CD) ? 'SYMBOL' : 'IMAGE');
    },
	_setSymbol : function _setSymbol(sidc, options) {
		return stmp.setSymbolInfo(sidc, options);
	},
	_createGeometry : function _createGeometry() {
		var _geometry;
		switch (this.geometryType) {
			case jiConstant.GEOMETRY_TYPE.POINT :
				_geometry = new Geometry.Point(this.getCoordinates());
				break;
			case jiConstant.GEOMETRY_TYPE.LINESTRING :
				_geometry = new Geometry.LineString(this.getCoordinates());
				break;
			case jiConstant.GEOMETRY_TYPE.POLYGON :
				if (this.getTypeCd() === stmp.DRAW_TYPE_KIND.CIRCLE.CD) {
					_geometry = new Geometry.Circle(this.getCoordinates(), this.styleInfo.style.radius);
				} else {
					_geometry = new Geometry.Polygon([this.getCoordinates()]);
				}
				break;
			default :
				throw new Error('알수없는 Geometry 타입 입니다.');
		}

		return _geometry;
	},
	_mgrsToLonLat : function _mgrsToLonLat() {
		var _lonLat;
		var _coordinates = [];

		if (this.coordInfo.coords.length > 1) {
			for (var i = 0; i < this.coordInfo.coords.length; i++) {
				_lonLat = stmp.convert.mgrsToLonLat(this.coordInfo.coords[i]);
				_coordinates.push([_lonLat.lon, _lonLat.lat]);
			}
		} else {
			_lonLat = stmp.convert.mgrsToLonLat(this.coordInfo.coords[0]);
			_coordinates.push(_lonLat.lon);
			_coordinates.push(_lonLat.lat);
		}

		return _coordinates;
	},
	_utmToLonLat : function _utmToLonLat() {
		var _lonLat;
		var _coordinates = [];

		if (this.coordInfo.coords.length > 1) {
			for (var i = 0; i < this.coordInfo.coords.length; i++) {
				_lonLat = stmp.convert.utmToLonLat(this.coordInfo.coords[i]);
				_coordinates.push([_lonLat.lon, _lonLat.lat]);
			}
		} else {
			_lonLat = stmp.convert.utmToLonLat(this.coordInfo.coords[0]);
			_coordinates.push(_lonLat.lon);
			_coordinates.push(_lonLat.lat);
		}

		return _coordinates;
	},
	_geoRefToLonLat : function _geoRefToLonLat() {
		var _lonLat;
		var _coordinates = [];

		if (this.coordInfo.coords.length > 1) {
			for (var i = 0; i < this.coordInfo.coords.length; i++) {
				_lonLat = stmp.convert.geoRefToLonLat(this.coordInfo.coords[i]);
				_coordinates.push([_lonLat.lon, _lonLat.lat]);
			}
		} else {
			_lonLat = stmp.convert.geoRefToLonLat(this.coordInfo.coords[0]);
			_coordinates.push(_lonLat.lon);
			_coordinates.push(_lonLat.lat);
		}

		return _coordinates;
	},
	_garsToLonLat : function _garsToLonLat() {
		var _lonLat;
		var _coordinates = [];

		if (this.coordInfo.coords.length > 1) {
			for (var i = 0; i < this.coordInfo.coords.length; i++) {
				_lonLat = stmp.convert.garsToLonLat(this.coordInfo.coords[i]);
				_coordinates.push([_lonLat.lon, _lonLat.lat]);
			}
		} else {
			_lonLat = stmp.convert.garsToLonLat(this.coordInfo.coords[0]);
			_coordinates.push(_lonLat.lon);
			_coordinates.push(_lonLat.lat);
		}

		return _coordinates;
	},
	_checkGeometryType : function _checkGeometryType() {
		var _geometryType;
		var _mapboxLayerType;

		switch (this.type.CD) {
			case stmp.DRAW_TYPE_KIND.POINT.CD :
				_geometryType = jiConstant.GEOMETRY_TYPE.POINT;
				_mapboxLayerType = jiConstant.MAPBOX_LAYER_TYPE.CIRCLE;
				break;
			case stmp.DRAW_TYPE_KIND.BASE_MILSYMBOL.CD :
			case stmp.DRAW_TYPE_KIND.IMAGE.CD :
				_geometryType = jiConstant.GEOMETRY_TYPE.POINT;
				_mapboxLayerType = jiConstant.MAPBOX_LAYER_TYPE.SYMBOL;
				break;
			case stmp.DRAW_TYPE_KIND.LINE.CD :
				_geometryType = jiConstant.GEOMETRY_TYPE.LINESTRING;
				_mapboxLayerType = jiConstant.MAPBOX_LAYER_TYPE.LINE;
				break;
			case stmp.DRAW_TYPE_KIND.CIRCLE.CD :
			case stmp.DRAW_TYPE_KIND.POLYGON.CD :
				_geometryType = jiConstant.GEOMETRY_TYPE.POLYGON;
				_mapboxLayerType = jiConstant.MAPBOX_LAYER_TYPE.FILL;
				break;
			case stmp.DRAW_TYPE_KIND.OVERLAY.CD :
				switch (this.overlayData.type) {
					case stmp.OVERLAY_TYPE.LINE :
						_geometryType = jiConstant.GEOMETRY_TYPE.LINESTRING;
						_mapboxLayerType = jiConstant.MAPBOX_LAYER_TYPE.LINE;
						break;
					case stmp.OVERLAY_TYPE.ARC :
						break;
				}
				break;
		}

		this.geometryType = _geometryType;
		this.mapBoxLayerType = _mapboxLayerType;
	}
};

var Geometry = {};

Geometry.create = function create(type, coordinates, options) {

}

/**
 * Point 객체
 * @constructor
 */
Geometry.Point = function Point(coordinates) {
	if (!coordinates) {
		throw new Error('좌표값이 없습니다.');
	}
	if (!Array.isArray(coordinates)) {
		throw new Error('좌표값은 Array 값이어야 합니다.');
	}
	if (coordinates.length < 2) {
		throw new Error('좌표값은 2개의 Number 값이어야 합니다.');
	}
	if (!stmp.isNumber(coordinates[0]) || !stmp.isNumber(coordinates[1])) {
		throw new Error('좌표값이 숫자가 아닙니다.');
	}

	this.type = 'Point';
	this.coordinates = coordinates;
};

Geometry.Point.prototype = {
	getType : function getType() {
		return this.type;
	},
	getX : function getX() {
		return this.coordinates[0];
	},
	setX : function setX(x) {
		this.coordinates[0] = x;
	},
	getY : function getY() {
		return this.coordinates[1];
	},
	setY : function setY(y) {
		this.coordinates[1] = y;
	},
	getCoordinates : function getCoordinates() {
		return this.coordinates;
	},
	getPoint : function getPoint() {
		return this;
	},
	setPoint : function setPoint(coordinates) {
		this.coordinates = coordinates;
	}
};

/**
 * MultiPoint 객체
 * @constructor
 */
Geometry.MultiPoint = function MultiPoint() {

};

/**
 * LineString 객체
 * @constructor
 */
Geometry.LineString = function LineString(coordinates) {
	if (!coordinates) {
		throw new Error('좌표값이 없습니다.');
	}
	if (!Array.isArray(coordinates)) {
		throw new Error('좌표값은 Array 값이어야 합니다.');
	}
	if (coordinates.length < 2) {
		throw new Error('좌표값은 2개 이상의 좌표열이 필요 합니다.');
	}
	if (!stmp.isNumber(coordinates[0][0]) || !stmp.isNumber(coordinates[0][1])) {
		throw new Error('좌표값이 숫자가 아닙니다.');
	}

	this.type = 'LineString';
	this.coordinates = coordinates;
};

Geometry.LineString.prototype = {
	getType : function getType() {
		return this.type;
	},
	getNumPoints : function getNumPoints() {
		return this.coordinates.length;
	},
	getStartPoint : function getStartPoint() {
		return this.coordinates[0];
	},
	getEndPoint : function getEndPoint() {
		return this.coordinates[this.getNumPoints() - 1];
	},
	getCoordinates : function getCoordinates() {
		return this.coordinates;
	},
	getCenter : function getCenter() {
		return [(this.getStartPoint()[0] + this.getEndPoint()[0]) / 2,
			(this.getStartPoint()[1] + this.getEndPoint()[1]) / 2];
	},
	getAngle : function getAngle() {
		var p0 = this.getStartPoint();
		var p1 = this.getEndPoint();

		return Math.atan2((p1[1] - p0[1]), (p1[0] - p0[0]));
	}
};

/**
 * MultiLineString 객체
 * @constructor
 */
Geometry.MultiLineString = function MultiLineString() {

};

/**
 * Polygon 객체
 * @constructor
 */
Geometry.Polygon = function Polygon(coordinates) {
	if (!(this instanceof Polygon)) {
		throw new Error('new 로 생성해야 함.');
	}

	if (!coordinates) {
		throw new Error('좌표값이 없습니다.');
	}
	if (!stmp.valid.polygon(coordinates)) {
		throw new Error('시작 좌표와 종료 좌표가 동일하지 않습니다.');
	}

	this.type = 'Polygon';
	this.coordinates = coordinates;
};

Geometry.Polygon.prototype = {
	getType : function getType() {
		return this.type;
	},
	getCoordinates : function getCoordinates() {
		return this.coordinates;
	},
	getCenter : function getCenter() {

	}
};

/**
 * MultiPolygon 객체
 * @constructor
 */
Geometry.MultiPolygon = function MultiPolygon() {

};

/**
 * Circle 객체
 * @param center
 * @param radius
 * @param units
 * @constructor
 */
Geometry.Circle = function Circle(center, radius, units) {
	if (!center) {
		throw new Error('중심 좌표값이 없습니다.');
	}
	if (!radius) {
		throw new Error('반지름 값이 없습니다.');
	}
	if (!units) {	// 반지름 단위
		units = 'kilometers';
	}

	var steps = 128;	// 생성할 버텍스 수

	var coordinates = [];
	for (var i = 0; i < steps; i++) {
		coordinates.push(Geometry.destination(center, radius, i * -360 / steps, units));
	}

	this.center = center;
	this.radius = radius;
	this.units = units;
	this.type = 'Polygon';
	this.coordinates = new Geometry.Polygon([coordinates]).getCoordinates();
};
// Circle Prototype
Geometry.Circle.prototype = {
	getType : function getType() {
		return this.type;
	},
	getCoordinates : function getCoordinates() {
		return this.coordinates;
	},
	getCenter : function getCenter() {
		return this.center;
	},
	getRadius : function getRadius() {
		return this.radius;
	},
	getUnits : function getUnits() {
		return this.units;
	}
}

Geometry.destination = function destination(origin, distance, bearing, units) {
	var lon1 = Geometry.toRadians(origin[0]);
	var lat1 = Geometry.toRadians(origin[1]);
	var bearing_rad = Geometry.toRadians(bearing);
	var radians = Geometry.lengthToRadians(distance, units);

	var lat2 = Math.asin(Math.sin(lat1) * Math.cos(radians) +
		Math.cos(lat1) * Math.sin(radians) * Math.cos(bearing_rad));
	var lon2 = lon1 + Math.atan2(Math.sin(bearing_rad) * Math.sin(radians) * Math.cos(lat1),
		Math.cos(radians) - Math.sin(lat1) * Math.sin(lat2));

	var lon = Geometry.toDegrees(lon2);
	var lat = Geometry.toDegrees(lat2);

	return [lon, lat];
};

/**
 * PI * radian = 180도
 * 1 radian = 180 / PI
 * @param degrees
 * @returns {number}
 */
Geometry.toRadians = function toRadians(degrees) {
	var radians = degrees % 360;
	return (radians * Math.PI) / 180;
};

/**
 * 각도로 변환 0 ~ 360
 * @param radians
 * @returns {number}
 */
Geometry.toDegrees = function toDegrees(radians) {
	var degrees = radians % (2 * Math.PI);
	return (degrees * 180) / Math.PI;
};

Geometry.lengthToRadians = function lengthToRadians(distance, units) {
	return distance / Geometry.factors[units];
};

Geometry.earthRadius = 6371008.8;	// 지구 평균 반지름

Geometry.factors = {
	meters : Geometry.earthRadius,
	metres : Geometry.earthRadius,
	millimeters : Geometry.earthRadius * 1000,
	millimetres : Geometry.earthRadius * 1000,
	centimeters : Geometry.earthRadius * 100,
	centimetres : Geometry.earthRadius * 100,
	kilometers : Geometry.earthRadius / 1000,
	kilometres : Geometry.earthRadius / 1000,
	radians : 1,
	degrees : Geometry.earthRadius / 111325
};

if (window.jiFeature === undefined) {
	window.jiFeature = jiFeature;
}

if (window.Geometry === undefined) {
	window.Geometry = Geometry;
}

/**
 	투명도 객체를 맵에 넣을경우 해당 svg 를 geojson 으로 변환하여 넣는다.
 	원 -> polygon 으로 변환하여 저장
 	선 -> 가운데 line은 그대로 표현, 시작점과 끝점이 arrow 일 경우 point에 arrow 이미지 패턴으로 표현
 	사각형 -> polygon 으로 표현
 	둥근사각형 -> svg 를 geojson 으로 변환하여 표현
 	호 -> svg 를 geojson 으로 변환하여 표현
 **/