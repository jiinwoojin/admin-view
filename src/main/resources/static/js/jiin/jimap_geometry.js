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

var Line = function Line(x1, y1, x2, y2, screen = false) {
    this.type = 'Line';
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.screen = screen;
}

Line.prototype = {
    constructor : Line,
    getType : function getType() {
        return this.type;
    },
    getX1 : function getX1() {
        return this.x1;
    },
    setX1 : function setX1(x1) {
        this.x1 = x1;
    },
    getY1 : function getY1() {
        return this.y1;
    },
    setY1 : function setY1(y1) {
        this.y1 = y1;
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
    getLine : function getLine() {
        return turf.lineString([this.x1, this.y1], [this.x2, this.y2]);
    },
    isScreen : function isScreen() {
        return this.screen;
    },
    setScreen : function setScreen(screen) {
        this.screen = screen;
    },
    getPath : function getPath() {
        var pixel1 = this.isScreen() ? {'x' : this.x1, 'y' : this.y1} : jiCommon.convert.lonLatToPixel({'lon' : this.x1, 'lat' : this.y1});
        var pixel2 = this.isScreen() ? {'x' : this.x2, 'y' : this.y2} : jiCommon.convert.lonLatToPixel({'lon' : this.x2, 'lat' : this.y2});

        return Path.moveTo(pixel1.x, pixel1.y) + Path.lineTo(pixel2.x, pixel2.y) + Path.closePath();
    }
}

var Rectangle = function Rectangle(x1, y1, x2, y2, screen = false) {
    this.type = 'Rectangle';
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.screen = screen;
}

Rectangle.prototype = {
    constructor : Rectangle,
    getType : function getType() {
        return this.type;
    },
    getX1 : function getX1() {
        return this.x1;
    },
    setX1 : function setX1(x1) {
        this.x1 = x1;
    },
    getY1 : function getY1() {
        return this.y1;
    },
    setY1 : function setY1(y1) {
        this.y1 = y1;
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
    isScreen : function isScreen() {
        return this.screen;
    },
    setScreen : function setScreen(screen) {
        this.screen = screen;
    },
    getRectangle : function getRectangle() {
        return turf.bboxPolygon([Math.max(this.x1, this.x2), Math.min(this.y1, this.y2),
            Math.min(this.x1, this.y2), Math.max(this.x1, this.y2)]);
    },
    getCenter : function getCenter() {
        var feature = turf.centroid(this.rectangle);
        return {'lon' : feature.geometry.coordinates[0], 'lat' : feature.geometry.coordinates[1]};
    },
    getPath : function getPath() {
        var pixel1 = this.isScreen() ? {'x' : this.x1, 'y' : this.y1} : jiCommon.convert.lonLatToPixel({'lon' : this.x1, 'lat' : this.y1});
        var pixel2 = this.isScreen() ? {'x' : this.x2, 'y' : this.y2} : jiCommon.convert.lonLatToPixel({'lon' : this.x2, 'lat' : this.y2});

        var x = pixel1.x;
        var y = pixel1.y;
        var w = pixel2.x - pixel1.x;
        var h = pixel2.y - pixel1.y;

        return Path.rect(x, y, w, h);
    }
}

var Triangle = function Triangle(x1, y1, x2, y2, screen = false) {
    this.type = 'Triangle';
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.screen = screen;
}

Triangle.prototype = {
    constructor : Triangle,
    getType : function getType() {
        return this.type;
    },
    getX1 : function getX1() {
        return this.x1;
    },
    setX1 : function setX1(x1) {
        this.x1 = x1;
    },
    getY1 : function getY1() {
        return this.y1;
    },
    setY1 : function setY1(y1) {
        this.y1 = y1;
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
    changeTriangle : function changeTriangle() {
        this.cx = (this.x1 + this.x2) / 2;
        this.cy = Math.max(this.y1, this.y2);
        this.rx = Math.max(this.x1, this.x2);
        this.ry = Math.min(this.y1, this.y2);
        this.lx = Math.min(this.x1, this.x2);
        this.ly = Math.min(this.y1, this.y2);
    },
    isScreen : function isScreen() {
        return this.screen;
    },
    setScreen : function setScreen(screen) {
        this.screen = screen;
    },
    getPath : function getPath() {
        this.changeTriangle();
        var pixel1 = this.isScreen() ? {'x' : this.cx, 'y' : this.cy} : jiCommon.convert.lonLatToPixel({'lon' : this.cx, 'lat' : this.cy});
        var pixel2 = this.isScreen() ? {'x' : this.rx, 'y' : this.ry} : jiCommon.convert.lonLatToPixel({'lon' : this.rx, 'lat' : this.ry});
        var pixel3 = this.isScreen() ? {'x' : this.lx, 'y' : this.ly} : jiCommon.convert.lonLatToPixel({'lon' : this.lx, 'lat' : this.ly});

        return Path.moveTo(pixel1.x, pixel1.y) + Path.lineTo(pixel2.x, pixel2.y) + Path.lineTo(pixel3.x, pixel3.y) + Path.closePath();
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