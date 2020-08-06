'use strict';

// 작도는 2D 에서만 한다.
var JimapOverlay = function JimapOverlay(options) {
    if (!(this instanceof JimapOverlay)) {
        throw new Error('new 로 객체를 생성해야 합니다.')
    }

    this.isDraw = false;
    this._drawMode = 999;

    this._svg = d3.select(options.canvas).append('svg');
    this._transform = d3.geoTransform({point : this.projectPoint});
    this._path = d3.geoPath().projection(this._transform);

    this._points = new jsMap();
    this._lines = new jsMap();
    this._rectangles = new jsMap();
    this._triangles = new jsMap();
    this._selectors = new jsMap();

    jiCommon.addMapEvent('viewreset', this.update);
    jiCommon.addMapEvent('move', this.update);
    jiCommon.addMapEvent('moveend', this.update);
}

JimapOverlay.prototype.constructor = JimapOverlay;

JimapOverlay.prototype.projectPoint = function projectPoint(lon, lat) {
    var point = jiCommon.convert.lonLatToPixel({'lon' : lon, 'lat' : lat});
    this.stream.point(point.x, point.y);
}

JimapOverlay.prototype.update = function update() {
    jiCommon.overlay.updatePoint();
    jiCommon.overlay.updateLine();
    jiCommon.overlay.updateRectangle();
    jiCommon.overlay.updateTriangle();
    jiCommon.overlay.updateSelector();
}

JimapOverlay.prototype.updateSelector = function updateSelector() {
    if (jiCommon.overlay._selectors.size() > 0) {
        jiCommon.overlay._selectors.values().forEach(e => {
            if (!e.feature.isSelect) {
                if (e.feature.getType() === jiConstant.GEOMETRY_TYPE.POINT) {
                    var _pointSvg = e.feature.getSvg();
                    e.svg.attr('cx', _pointSvg.cx).attr('cy', _pointSvg.cy).attr('r', _pointSvg.r);
                }
            }
        });
    }
}

JimapOverlay.prototype.updatePoint = function updatePoint() {
    if (jiCommon.overlay._points.size() > 0) {
        jiCommon.overlay._points.values().forEach(e => {
            if (!e.feature.isScreen) {
                var _pointSvg = e.feature.getSvg();
                e.svg.attr('cx', _pointSvg.cx).attr('cy', _pointSvg.cy).attr('r', _pointSvg.r);
            }
        });
    }
}

JimapOverlay.prototype.updateLine = function updateLine() {
    if (jiCommon.overlay._lines.size() > 0) {
        jiCommon.overlay._lines.values().forEach(e => {
            if (!e.feature.isScreen) {
                // 현재 map extents 와 겹치는지 부분만 update
                // 원본 좌표는 e 객체에 들어 있음
                var _lineSvg = e.feature.getSvg();
                e.svg.attr('x1', _lineSvg.x1).attr('y1', _lineSvg.y1)
                    .attr('x2', _lineSvg.x2).attr('y2', _lineSvg.y2);
                //e.svg.attr('d', e.feature.getPath());
            }
        });
    }
}

JimapOverlay.prototype.updateRectangle = function updateRectangle() {
    if (jiCommon.overlay._rectangles.size() > 0) {
        jiCommon.overlay._rectangles.values().forEach(e => {
            if (!e.feature.isScreen) {
                // 현재 map extents 와 겹치는지 부분만 update
                // 원본 좌표는 e 객체에 들어 있음
                var _rectSvg = e.feature.getSvg();
                e.svg.attr('x', _rectSvg.x).attr('y', _rectSvg.y)
                    .attr('width', _rectSvg.w).attr('height', _rectSvg.h);
                //e.svg.attr('d', e.feature.getPath());
            }
        });
    }
}

JimapOverlay.prototype.updateTriangle = function updateTriangle() {
    if (jiCommon.overlay._triangles.size() > 0) {
        jiCommon.overlay._triangles.values().forEach(e => {
            if (!e.feature.isScreen()) {
                // 현재 map extents 와 겹치는지 부분만 update
                // 원본 좌표는 e 객체에 들어 있음
                var _triSvg = e.feature.getSvg();
                var _points = _triSvg.npoint.x + ',' + _triSvg.npoint.y + ' '
                    + _triSvg.spoint.x + ',' + _triSvg.spoint.y + ' '
                    + _triSvg.wpoint.x + ',' + _triSvg.wpoint.y;
                e.svg.attr('points', _points);
                //e.svg.attr('d', e.feature.getPath());
            }
        });
    }
}

JimapOverlay.prototype.updatePath = function updatePath(svg, feature) {
    svg.attr('d', feature.getPath());
}

JimapOverlay.prototype.selectedShape = function selectedShape(shape) {
    if (this.isDraw) {
        this._drawMode = 999;
        this.isDraw = false;

        jiCommon.enableDragPan();
    } else {
        this._drawMode = shape;
        this.isDraw = true;
        this.keep = false;
    }

    this._eventsController();
}

JimapOverlay.prototype.idMaker = function idMaker() {
    var makeId;
    switch (this._drawMode) {
        case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
            makeId = jiConstant.OVERLAY_DRAW_MODE.LINE.NAME + (++overlayCommon.svgShapeNum.line);
            break;
        case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
            makeId = jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.NAME + (++overlayCommon.svgShapeNum.rect);
            break;
        case jiConstant.OVERLAY_DRAW_MODE.ROUNDED_RECTANGLE.CD :
            makeId = jiConstant.OVERLAY_DRAW_MODE.ROUNDED_RECTANGLE.NAME + (++overlayCommon.svgShapeNum.roundrect);
            break;
        case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
            makeId = jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.NAME + (++overlayCommon.svgShapeNum.triangle);
            break;
    }

    return makeId;
}

JimapOverlay.prototype._eventsController = function _eventsController() {
    // Line | Rectangle | Rounded Rectangle | Triangle | Circle : drawStart, drawDrag, drawEnd
    if (this.isDraw) {
        switch (this._drawMode) {
            case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
            case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
            case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
                jiCommon.addMapEvent(jiConstant.EVENTS.MOUSEDOWN, this.drawStart);
                jiCommon.addMapEvent(jiConstant.EVENTS.MOUSEUP, this.drawEnd);
                break;
        }
    } else {
        switch (this._drawMode) {
            case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
            case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
            case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
                jiCommon.removeMapEvent(jiConstant.EVENTS.MOUSEDOWN, this.drawStart);
                jiCommon.removeMapEvent(jiConstant.EVENTS.MOUSEMOVE, this.drawDrag);
                jiCommon.removeMapEvent(jiConstant.EVENTS.MOUSEUP, this.drawEnd);
                break;
        }
    }
}

JimapOverlay.prototype.createGeometry = function createGeometry() {
    var feature;

    switch (jiCommon.overlay._drawMode) {
        case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
            feature = new JimapOverlayGeometry.Line(jiCommon.overlay.object.x1,
                jiCommon.overlay.object.y1, jiCommon.overlay.object.x2, jiCommon.overlay.object.y2);
            break;
        case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
            feature = new JimapOverlayGeometry.Rectangle(jiCommon.overlay.object.x1,
                jiCommon.overlay.object.y1, jiCommon.overlay.object.x2, jiCommon.overlay.object.y2);
            break;
        case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
            feature = new JimapOverlayGeometry.Triangle(jiCommon.overlay.object.x1,
                jiCommon.overlay.object.y1, jiCommon.overlay.object.x2, jiCommon.overlay.object.y2);
            break;
    }

    return feature;
}

JimapOverlay.prototype.createSvg = function createSvg() {
    var svg;
    var _id = jiCommon.overlay.object.id;
    var _feature = jiCommon.overlay.object.feature;

    switch (jiCommon.overlay._drawMode) {
        case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
            var _lineSvg = _feature.getSvg();
            svg = jiCommon.overlay._svg.append('g').attr('id', 'g-line-' + _id)
                /*.append('path').attr('id', 'path-line-' + _id)
                .attr('d', _feature.getPath())*/
                .append('line').attr('id', 'line-' + _id)
                .attr('x1', _lineSvg.x1).attr('y1', _lineSvg.y1)
                .attr('x2', _lineSvg.x2).attr('y2', _lineSvg.y2)
                .style('stroke', overlayCommon.commonAttr.stroke)
                .style('stroke-dasharray', '5 5')
                .style('opacity', 0.8)
                .style('stroke-width', overlayCommon.commonAttr.strokeWidth)
                .on('click', function() {
                    jiCommon.overlay.setActive(jiConstant.OVERLAY_DRAW_MODE.LINE.CD, _id);
                });
            break;
        case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
            var _rectSvg = _feature.getSvg();
            svg = jiCommon.overlay._svg.append('g').attr('id', 'g-rect-' + _id)
                /*.append('path').attr('id', 'path-rect-' + _id)
                .attr('d', _feature.getPath())*/
                .append('rect').attr('id', 'rect-' + _id)
                .attr('x', _rectSvg.x).attr('y', _rectSvg.y)
                .attr('width', _rectSvg.w)
                .attr('height', _rectSvg.h)
                .style('stroke', overlayCommon.commonAttr.stroke)
                .style('stroke-width', overlayCommon.commonAttr.strokeWidth)
                .style('fill', overlayCommon.commonAttr.color)
                .style('fill-opacity', 0.4)
                .on('click', function() {console.log(d3.select(this))});
            break;
        case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
            var _triSvg = _feature.getSvg();
            var _points = _triSvg.npoint.x + ',' + _triSvg.npoint.y + ' '
                + _triSvg.spoint.x + ',' + _triSvg.spoint.y + ' '
                + _triSvg.wpoint.x + ',' + _triSvg.wpoint.y;
            svg = jiCommon.overlay._svg.append('g').attr('id', 'g-triangle-' + _id)
                /*.append('path').attr('id', 'path-triangle-' + _id)
                .attr('d', _feature.getPath())*/
                .append('polygon').attr('id', 'triangle-' + _id)
                .attr('points', _points)
                .style('stroke', overlayCommon.commonAttr.stroke)
                .style('stroke-width', overlayCommon.commonAttr.strokeWidth)
                .style('fill', overlayCommon.commonAttr.color)
                .style('fill-opacity', 0.4)
                .on('click', function() {alert('Triangle Click Event.')});
            break;
    }

    return svg;
}

JimapOverlay.prototype.updateObject = function updateObject() {
    var _id = jiCommon.overlay.object.id;
    var _feature = jiCommon.overlay.object.feature;

    if (_feature instanceof JimapOverlayGeometry.Line) {
        if (!jiCommon.overlay._lines.containsKey(_id)) {
            jiCommon.overlay._lines.put(_id, jiCommon.overlay.object);
        }
    } else if (_feature instanceof JimapOverlayGeometry.Rectangle) {
        if (!jiCommon.overlay._rectangles.containsKey(_id)) {
            jiCommon.overlay._rectangles.put(_id, jiCommon.overlay.object);
        }
    } else if (_feature instanceof JimapOverlayGeometry.Triangle) {
        if (!jiCommon.overlay._triangles.containsKey(_id)) {
            jiCommon.overlay._triangles.put(_id, jiCommon.overlay.object);
        }
    }
}

JimapOverlay.prototype.individualUpdate = function individualUpdate() {
    var _feature = jiCommon.overlay.object.feature;

    if (_feature instanceof JimapOverlayGeometry.Line) {
        jiCommon.overlay.updateLine();
    } else if (_feature instanceof JimapOverlayGeometry.Rectangle) {
        jiCommon.overlay.updateRectangle();
    } else if (_feature instanceof JimapOverlayGeometry.Triangle) {
        jiCommon.overlay.updateTriangle();
    }

    jiCommon.overlay.updateSelector();
}

JimapOverlay.prototype.drawStart = function drawStart(e) {
    jiCommon.overlay.keep = true;
    jiCommon.disableDragPan();

    jiCommon.overlay.object = {};
    jiCommon.overlay.object.id = jiCommon.overlay.idMaker();
    jiCommon.overlay.object.x1 = e.point.x;
    jiCommon.overlay.object.y1 = e.point.y;

    switch (jiCommon.overlay._drawMode) {
        case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
        case jiConstant.OVERLAY_DRAW_MODE.RECTANGLE.CD :
        case jiConstant.OVERLAY_DRAW_MODE.TRIANGLE.CD :
            jiCommon.addMapEvent(jiConstant.EVENTS.MOUSEMOVE, jiCommon.overlay.drawDrag);
            break;
    }
}

// 입력되는 좌표열을 2개만 관리 할 경우
JimapOverlay.prototype.drawDrag = function drawDrag(e) {
    if (jiCommon.overlay.keep) {
        jiCommon.overlay.object.x2 = e.point.x;
        jiCommon.overlay.object.y2 = e.point.y;

        if (jiCommon.overlay.object.x1 !== jiCommon.overlay.object.x2) {
            if (jiCommon.overlay.object.feature === undefined) {
                jiCommon.overlay.object.feature = jiCommon.overlay.createGeometry();
            } else {
                jiCommon.overlay.object.feature.setX2(jiCommon.overlay.object.x2);
                jiCommon.overlay.object.feature.setY2(jiCommon.overlay.object.y2);
            }

            if (jiCommon.overlay.object.svg === undefined) {
                jiCommon.overlay.object.svg = jiCommon.overlay.createSvg();
            }

            jiCommon.overlay.updateObject();
            jiCommon.overlay.individualUpdate();
        }
    }
}

JimapOverlay.prototype.drawEnd = function drawEnd(e) {
    jiCommon.overlay.individualUpdate();

    jiCommon.overlay.isDraw = false;

    jiCommon.overlay._eventsController();

    jiCommon.enableDragPan();

    var _drawMode = jiCommon.overlay._drawMode;
    var _id = jiCommon.overlay.object.id;

    jiCommon.overlay._drawMode = 999;
    jiCommon.overlay.object = {};
    jiCommon.overlay.setActive(_drawMode, _id);
}

JimapOverlay.prototype.selectObject = function selectObject() {

}

JimapOverlay.prototype.setActive = function setActive(drawMode, id) {
    switch (drawMode) {
        case jiConstant.OVERLAY_DRAW_MODE.LINE.CD :
            this.setLineSelector(id);
            break;
    }
}

JimapOverlay.prototype.createPointSvg = function createPointSvg(gId, object) {

}

JimapOverlay.prototype.createLineSvg = function createLineSvg(gId, object) {

}

JimapOverlay.prototype.createRectangleSvg = function createRectangleSvg(gId, object) {

}

JimapOverlay.prototype.createTriangleSvg = function createTriangleSvg(gId, object) {

}

JimapOverlay.prototype.setLineSelector = function setLineSelector(id) {
    if (this._lines.containsKey(id)) {
        var d3DragEvent = function d3DragEvent() {
            var start = function start() {
                var _objectId = d3.select(this).node().id.split('-')[2];
                if (jiCommon.overlay._lines.containsKey(_objectId)) {
                    jiCommon.overlay.isDraw = true;
                    jiCommon.overlay.keep = true;
                    jiCommon.overlay.object = jiCommon.overlay._lines.get(_objectId);
                }
            };
            var drag = function drag() {
                if (jiCommon.overlay.keep) {
                    console.log('drag');
                    console.log(this);
                }
            };
            var end = function end() {
                console.log('end');
                console.log(this);
            };
            return d3.drag().on('start', start).on('drag', drag).on('end', end);
        }
        var _object = this._lines.get(id);
        var _feature = _object.feature;

        var lineId = '#g-line-' + id;
        var geoStartPoint = _feature.geoStartPoint;
        var geoEndPoint = _feature.geoEndPoint;

        // 3개의 점을 생성한다. 시작점, 중간점, 끝점
        // geometry 로 생성 해야함
        var startObject = {};
        startObject.id = 'selector-start';
        startObject.updateId = id;
        startObject.cx = _object.x1;
        startObject.cy = _object.y1;
        startObject.r = 5;
        startObject.feature = new JimapOverlayGeometry.Point(startObject.cx, startObject.cy, 5);
        startObject.feature.setGeoPoint(geoStartPoint);
        startObject.svg = d3.select(lineId).append('circle').attr('id', startObject.id)
            .attr('r', 5)
            .attr('cx', startObject.feature.getCx())
            .attr('cy', startObject.feature.getCy())
            .attr('stroke', 'rgb(255, 255, 255)')
            .attr('stroke-width', 1)
            .style('fill', 'rgb(0, 255, 0)')
            .style('fill-opacity', 1).call(d3DragEvent());

        this._selectors.put(startObject.id, startObject);

        var endObject = {};
        endObject.id = 'selector-end';
        endObject.updateId = id;
        endObject.cx = _object.x2;
        endObject.cy = _object.y2;
        endObject.r = 5;
        endObject.feature = new JimapOverlayGeometry.Point(endObject.cx, endObject.cy, 5);
        endObject.feature.setGeoPoint(geoEndPoint);
        endObject.svg = d3.select(lineId).append('circle').attr('id', endObject.id)
            .attr('r', 5)
            .attr('cx', endObject.feature.getCx())
            .attr('cy', endObject.feature.getCy())
            .attr('stroke', 'rgb(255, 255, 255)')
            .attr('stroke-width', 1)
            .style('fill', 'rgb(0, 255, 0)')
            .style('fill-opacity', 1).call(d3DragEvent());

        this._selectors.put(endObject.id, endObject);
        // 각각 이벤트 정의
    } else {
        console.log(id + ' : 해당 객체가 없습니다.');
    }
}

JimapOverlay.prototype.setRectSelector = function setRectSelector(id) {

}