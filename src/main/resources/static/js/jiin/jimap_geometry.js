"use strict";

var Geometry = function Geometry() {

}

var Point = function Point(x, y) {
    // x, y valid 체크

    this.type = 'Point';
    this.coordinates = [x, y];
}

Point.prototype = {
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
}

var MultiPoint = function MultiPoint() {

}

var LineString = function LineString(coordinates) {
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
}

LineString.prototype = {
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
}

var MultiLineString = function MultiLineString() {

}

var Polygon = function Polygon(coordinates) {
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
}

Polygon.prototype = {
    getType : function getType() {
        return this.type;
    },
    getCoordinates : function getCoordinates() {
        return this.coordinates;
    },
    getCenter : function getCenter() {

    }
}

var MultiPolygon = function MultiPolygon() {

}

var Circle = function Circle(center, radius, units) {
    if (!center) {
        throw new Error('중심 좌표값이 없습니다.');
    }
    if (!radius) {
        throw new Error('반지름 값이 없습니다.');
    }
    if (!units) {	// 반지름 단위
        units = jiConstant.UNITS.KILOMETERS;
    }

    var steps = 128;	// 생성할 버텍스 수

    var coordinates = [];
    for (var i = 0; i < steps; i++) {
        coordinates.push(turf.destination(center, radius, i * -360 / steps, {'units' : units}).geometry.coordinates);
    }

    coordinates.push(coordinates[0]);

    this.center = center;
    this.radius = radius;
    this.units = units;
    this.type = 'Polygon';
    this.coordinates = new Polygon([coordinates]).getCoordinates();
}

Circle.prototype = {
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

var JimapOverlayGeometry = {};
JimapOverlayGeometry.Point = function Point(cx, cy, r, isScreen = false) {
    if (!(this instanceof JimapOverlayGeometry.Point)) {
        throw new Error('new 로 생성해야 합니다.');
    }

    this.type = jiConstant.GEOMETRY_TYPE.POINT;
    this.cx = cx;
    this.cy = cy;
    this.r = r;
    this.geoPoint = '';
    this.isScreen = isScreen;
}

JimapOverlayGeometry.Point.prototype = {
    constructor : JimapOverlayGeometry.Point,
    getType : function getType() {
        return this.type;
    },
    getCx : function getCx() {
        return this.cx;
    },
    setCx : function setCx(cx) {
        this.cx = cx;
        this.setGeoPoint();
    },
    getCy : function getCy() {
        return this.cy;
    },
    setCy : function setCy(cy) {
        this.cy = cy;
        this.setGeoPoint();
    },
    setGeoPoint : function setGeoPoint(point) {
        if (point === undefined) {
            if (this.cx !== undefined && this.cy !== undefined) {
                this.geoPoint = jiCommon.convert.pixelToLonLat({'x' : this.cx, 'y' : this.cy});
            }
        } else {
            this.geoPoint = point;
            var _pixelPoint = this.geometryToPixel();
            this.cx = _pixelPoint.x;
            this.cy = _pixelPoint.y;
        }
    },
    setIsScreen : function setIsScreen(isScreen) {
        this.isScreen = isScreen;
    },
    setGeometry : function setGeometry(geometry) {
        this.geometry = geometry;
    },
    geometryToPixel : function geometryToPixel() {
        return jiCommon.convert.lonLatToPixel({'lon' : this.geoPoint.lon, 'lat' : this.geoPoint.lat});
    },
    pixelToGeometry : function pixelToGeometry() {
        return jiCommon.convert.pixelToLonLat({'x' : this.cx, 'y' : this.cy});
    },
    getPoint : function getPoint() {
        if (this.geoPoint === '') {
            this.setGeoPoint();
        }

        return this.isScreen ? {'x' : this.cx, 'y' : this.cy} : this.geometryToPixel();
    },
    getSvg : function getSvg() {
        var _point = this.getPoint();

        return {
            'cx' : _point.x,
            'cy' : _point.y,
            'r' : this.r
        }
    }
}

JimapOverlayGeometry.Line = function Line(x1, y1, x2, y2, isScreen = false) {
    if (!(this instanceof JimapOverlayGeometry.Line)) {
        throw new Error('new 로 생성해야 합니다.');
    }

    this.type = jiConstant.GEOMETRY_TYPE.LINESTRING;
    this.x1 = x1;           // 화면좌표 start point x
    this.y1 = y1;           // 화면좌표 start point y
    this.x2 = x2;           // 화면좌표 end point x
    this.y2 = y2;           // 화면좌표 end point y
    this.geoStartPoint = '';   // geometry start point [x, y]
    this.geoEndPoint = '';     // geometry end point [x, y]
    this.isScreen = isScreen;   // 화면 좌표 사용 여부
}

JimapOverlayGeometry.Line.prototype = {
    constructor : JimapOverlayGeometry.Line,
    getType : function getType() {
        return this.type;
    },
    getX1 : function getX1() {
        return this.x1;
    },
    setX1 : function setX1(x1) {
        this.x1 = x1;
        this.setGeoStartPoint();
    },
    getY1 : function getY1() {
        return this.y1;
    },
    setY1 : function setY1(y1) {
        this.y1 = y1;
        this.setGeoStartPoint();
    },
    getX2 : function getX2() {
        return this.x2;
    },
    setX2 : function setX2(x2) {
        this.x2 = x2;
        this.setGeoEndPoint();
    },
    getY2 : function getY2() {
        return this.y2;
    },
    setY2 : function setY2(y2) {
        this.y2 = y2;
        this.setGeoEndPoint();
    },
    setGeoStartPoint : function setGeoStartPoint() {
        if (this.x1 !== undefined && this.y1 !== undefined) {
            this.geoStartPoint = jiCommon.convert.pixelToLonLat({'x' : this.x1, 'y' : this.y1});
        }
    },
    setGeoEndPoint : function setGeoEndPoint() {
        if (this.x2 !== undefined && this.y2 !== undefined) {
            this.geoEndPoint = jiCommon.convert.pixelToLonLat({'x' : this.x2, 'y' : this.y2});
        }
    },
    getLine : function getLine() {
        return turf.lineString([this.startPoint.lon, this.startPoint.lat],
            [this.endPoint.lon, this.endPoint.lat]);
    },
    setIsScreen : function setIsScreen(isScreen) {
        this.isScreen = isScreen;
    },
    getStartPoint : function getStartPoint() {
        if (this.geoStartPoint === '') {
            this.setGeoStartPoint();
        }
        return this.isScreen ? {'x' : this.x1, 'y' : this.y1} : jiCommon.convert.lonLatToPixel({'lon' : this.geoStartPoint.lon, 'lat' : this.geoStartPoint.lat});
    },
    getEndPoint : function getEndPoint() {
        if (this.geoEndPoint === '') {
            this.setGeoEndPoint();
        }
        return this.isScreen ? {'x' : this.x2, 'y' : this.y2} : jiCommon.convert.lonLatToPixel({'lon' : this.geoEndPoint.lon, 'lat' : this.geoEndPoint.lat});
    },
    getPath : function getPath() {
        var _startPoint = this.getStartPoint();
        var _endPoint = this.getEndPoint();

        return Path.moveTo(_startPoint.x, _startPoint.y) + Path.lineTo(_endPoint.x, _endPoint.y) + Path.closePath();
    },
    getSvg : function getSvg() {
        var _startPoint = this.getStartPoint();
        var _endPoint = this.getEndPoint();

        return {
            'x1' : _startPoint.x,
            'y1' : _startPoint.y,
            'x2' : _endPoint.x,
            'y2' : _endPoint.y
        }
    }
}

JimapOverlayGeometry.Rectangle = function Rectangle(x1, y1, x2, y2, isScreen = false) {
    if (!(this instanceof JimapOverlayGeometry.Rectangle)) {
        throw new Error('new 로 생성해야 합니다.');
    }

    this.type = jiConstant.GEOMETRY_TYPE.POLYGON;
    this.x1 = x1;           // 화면좌표 start point x
    this.y1 = y1;           // 화면좌표 start point y
    this.x2 = x2;           // 화면좌표 end point x
    this.y2 = y2;           // 화면좌표 end point y
    this.geoStartPoint = '';   // geometry start point [x, y]
    this.geoEndPoint = '';     // geometry end point [x, y]
    this.isScreen = isScreen;
}

JimapOverlayGeometry.Rectangle.prototype = {
    constructor : JimapOverlayGeometry.Rectangle,
    getType : function getType() {
        return this.type;
    },
    getX1 : function getX1() {
        return this.x1;
    },
    setX1 : function setX1(x1) {
        this.x1 = x1;
        this.setGeoStartPoint();
    },
    getY1 : function getY1() {
        return this.y1;
    },
    setY1 : function setY1(y1) {
        this.y1 = y1;
        this.setGeoStartPoint();
    },
    getX2 : function getX2() {
        return this.x2;
    },
    setX2 : function setX2(x2) {
        this.x2 = x2;
        this.setGeoEndPoint();
    },
    getY2 : function getY2() {
        return this.y2;
    },
    setY2 : function setY2(y2) {
        this.y2 = y2;
        this.setGeoEndPoint();
    },
    setIsScreen : function setIsScreen(isScreen) {
        this.isScreen = isScreen;
    },
    getRectangle : function getRectangle() {
        return turf.bboxPolygon([Math.max(this.x1, this.x2), Math.min(this.y1, this.y2),
            Math.min(this.x1, this.y2), Math.max(this.x1, this.y2)]);
    },
    getCenter : function getCenter() {
        var feature = turf.centroid(this.rectangle);
        return {'lon' : feature.geometry.coordinates[0], 'lat' : feature.geometry.coordinates[1]};
    },
    getStartPoint : function getStartPoint() {
        if (this.geoStartPoint === '') {
            this.setGeoStartPoint();
        }
        return this.isScreen ? {'x' : this.x1, 'y' : this.y1} : jiCommon.convert.lonLatToPixel({'lon' : this.geoStartPoint.lon, 'lat' : this.geoStartPoint.lat});
    },
    setGeoStartPoint : function setGeoStartPoint() {
        if (this.x1 !== undefined && this.y1 !== undefined) {
            this.geoStartPoint = jiCommon.convert.pixelToLonLat({'x' : this.x1, 'y' : this.y1});
        }
    },
    getEndPoint : function getEndPoint() {
        if (this.geoEndPoint === '') {
            this.setGeoEndPoint();
        }
        return this.isScreen ? {'x' : this.x2, 'y' : this.y2} : jiCommon.convert.lonLatToPixel({'lon' : this.geoEndPoint.lon, 'lat' : this.geoEndPoint.lat});
    },
    setGeoEndPoint : function setGeoEndPoint() {
        if (this.x2 !== undefined && this.y2 !== undefined) {
            this.geoEndPoint = jiCommon.convert.pixelToLonLat({'x' : this.x2, 'y' : this.y2});
        }
    },
    getPath : function getPath() {
        var _startPoint = this.getStartPoint();
        var _endPoint = this.getEndPoint();

        var x = Math.min(_startPoint.x, _endPoint.x);
        var y = Math.min(_startPoint.y, _endPoint.y);
        var w = Math.abs(_endPoint.x - _startPoint.x);
        var h = Math.abs(_endPoint.y - _startPoint.y);

        return Path.rect(x, y, w, h);
    },
    getSvg : function getSvg() {
        var _startPoint = this.getStartPoint();
        var _endPoint = this.getEndPoint();

        return {
            'x' : Math.min(_startPoint.x, _endPoint.x),
            'y' : Math.min(_startPoint.y, _endPoint.y),
            'w' : Math.abs(_startPoint.x - _endPoint.x),
            'h' : Math.abs(_startPoint.y - _endPoint.y)
        }
    }
}

JimapOverlayGeometry.Triangle = function Triangle(x1, y1, x2, y2, isScreen = false) {
    if (!(this instanceof JimapOverlayGeometry.Triangle)) {
        throw new Error('new 로 생성해야 합니다.');
    }

    this.type = jiConstant.GEOMETRY_TYPE.POLYGON;
    this.startX = x1;
    this.startY = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.nPoint = '';
    this.sPoint = '';
    this.wPoint = '';
    this.cPoint = '';
    this.nGeoPoint = '';        // geometry n point
    this.sGeoPoint = '';        // geometry s point
    this.wGeoPoint = '';        // geometry w point
    this.cGeoPoint = '';        // geometry c point
    this.isScreen = isScreen;
}

JimapOverlayGeometry.Triangle.prototype = {
    constructor : JimapOverlayGeometry.Triangle,
    getType : function getType() {
        return this.type;
    },
    getStartX : function getStartX() {
        return this.startX;
    },
    setStartX : function setStartX(x1) {
        this.startX = x1;
    },
    getStartY : function getStartY() {
        return this.startY;
    },
    setStartY : function setStartY(y1) {
        this.startY = y1;
    },
    getX2 : function getX2() {
        return this.x2;
    },
    setX2 : function setX2(x2) {
        this.x2 = x2;
    },
    getY2 : function getY2() {
        return this.y2;
    },
    setY2 : function setY2(y2) {
        this.y2 = y2;
    },
    getCenter : function getCenter() {
        return {
            'lon' : this.startX,
            'lat' : this.startY
        }
    },
    transformRotate : function transformRotate(angle) {
        var _centroid = this.getCenter();
        var rotatedPoly = turf.transformRotate(this.getTriangle(), angle, {pivot : [_centroid.lon, _centroid.lat]});

        var nPoint = rotatedPoly.geometry.coordinates[0];
        var sPoint = rotatedPoly.geometry.coordinates[1];
        var wPoint = rotatedPoly.geometry.coordinates[2];

        this.nx = nPoint[0];
        this.ny = nPoint[1];
        this.sx = sPoint[0];
        this.sy = sPoint[1];
        this.wx = wPoint[0];
        this.wy = wPoint[1];
    },
    getTriangle : function getTriangle() {
        this.changeTriangle();
        return turf.polygon([[[this.nx, this.ny], [this.sx, this.sy], [this.wx, this.wy], [this.nx, this.ny]]]);
    },
    getDistance : function getDistance() {
        return jiCommon.calculation.distance({'x' : this.startX, 'y' : this.startY}, {'x' : this.x2, 'y' : this.y2});
    },
    changeTriangle : function changeTriangle() {
        this.cx = this.startX;
        this.cy = this.startY;
        var xDist = Math.abs(this.x2 - this.cx);
        var yDist = Math.abs(this.y2 - this.cy);
        this.sx = this.cx + xDist;
        this.sy = this.cy - yDist;
        this.wx = this.cx - xDist;
        this.wy = this.cy - yDist;
        this.nx = this.cx;
        this.ny = this.cy + yDist;
    },
    setIsScreen : function setIsScreen(isScreen) {
        this.isScreen = isScreen;
    },
    getNPoint : function getNPoint() {
        return this.isScreen ? {'x' : this.nx, 'y' : this.ny} : jiCommon.convert.lonLatToPixel({'lon' : this.nx, 'lat' : this.ny});
    },
    getWPoint : function getWPoint() {
        return this.isScreen ? {'x' : this.wx, 'y' : this.wy} : jiCommon.convert.lonLatToPixel({'lon' : this.wx, 'lat' : this.wy});
    },
    getSPoint : function getSPoint() {
        return this.isScreen ? {'x' : this.sx, 'y' : this.sy} : jiCommon.convert.lonLatToPixel({'lon' : this.sx, 'lat' : this.sy});
    },
    getPath : function getPath() {
        this.changeTriangle();
        var nPoint = this.getNPoint();
        var sPoint = this.getSPoint();
        var wPoint = this.getWPoint();

        return Path.moveTo(nPoint.x, nPoint.y) + Path.lineTo(sPoint.x, sPoint.y) + Path.lineTo(wPoint.x, wPoint.y) + Path.closePath();
    },
    getSvg : function getSvg() {
        this.changeTriangle();
        var nPoint = this.getNPoint();
        var sPoint = this.getSPoint();
        var wPoint = this.getWPoint();

        return {
            'npoint' : nPoint,
            'spoint' : sPoint,
            'wpoint' : wPoint
        }
    }
}

var Path = {
    moveTo : function moveTo(x, y) {
        return 'M' + (+x) + ',' + (+y);
    },
    closePath : function closePath() {
        return 'Z';
    },
    lineTo : function lineTo(x, y) {
        return 'L' + (+x) + ',' + (+y);
    },
    quadraticCurveTo : function quadraticCurveTo(x1, y1, x, y) {
        return 'Q' + (+x1) + ',' + (+y1) + ',' + (+x) + ',' + (+y);
    },
    bezierCurveTo : function bezierCurveTo(x1, y1, x2, y2, x, y) {
        return 'C' + (+x1) + ',' + (+y1) + ',' + (+x2) + ',' + (+y2) + ',' + (+x) + ',' + (+y);
    },
    rect : function rect(x, y, w, h) {
        return this.moveTo(x, y) + 'h' + (+w) + 'v' + (+h) + 'h' + (-w) + this.closePath();
    }
};